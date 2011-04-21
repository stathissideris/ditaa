package org.ditaa.web;

import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Generate a Ditaa image in a separate process (isolate memory leaks and busy loops). */
@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
public class ExternalRenderer {
    private static final boolean DEBUG = false;

    // millis between checking the renderer for new output
    private static final int RENDERER_POLL_INTERVAL = 100;

    public static final String CONTENT_IMAGE = "image/png", CONTENT_TEXT = "text/plain";

    private static final int GRACE_PERIOD_MS = 1500;

    // -- initial state --
    private ConversionOptions options;
    private String backgroundColorString; // avoid converting it back to hex format
    private String gridText;
    private long timeoutMillis;
    private HttpServletResponse response;
    private ServletContext context;
    private long start;

    // -- rendering state --
    private boolean used = false; // this is a single-use object
    /** Errors that occurred during {@link #render}.
     *  If an error had no exception associated with it, the value will be null. */
    private List<Pair<String, Throwable>> errors = new ArrayList<Pair<String, Throwable>>();
    // 2 total: 1 for write thread, 1 for read thread
    private CountDownLatch waitForIt = new CountDownLatch(2);
    // the external rendering process
    private Process process;
    // the content-type written to the response -- CONTENT_IMAGE or CONTENT_TEXT
    private String contentType = null;
    // the external process's exit value
    private Integer exit = null;

    // set to true if we ran out of time before output was received
    private boolean timedOut = false;
    // set to true when we start returning image output from the external process
    private boolean receivedImageOutput = false;

    public ExternalRenderer
            (ConversionOptions options, String backgroundColorString, String gridText, long timeoutMillis,
             ServletContext context, HttpServletResponse response)
            throws IOException
    {
        this.options = options;
        this.backgroundColorString = backgroundColorString;
        this.gridText = gridText;
        this.timeoutMillis = timeoutMillis;
        this.context = context;
        this.response = response;
        this.start = System.currentTimeMillis();
    }

    private void addError(String msg) { addError(msg, null); }
    private void addError(String msg, Throwable t) {
        errors.add(new Pair<String, Throwable>(msg, t));
    }

    /** The exit value of the external process. Null if none (not yet complete or terminated). */
    public Integer getExitValue() { return exit; }

    /** Fires up an external process using ProcessBuilder and then feeds it input & watches its output
     *  in two separate threads; returns when both finish or when time has expired.
     *  Errors that occurred are stored in
     * @throws InterruptedException if the render does not complete in time
     *  (or if threads are interrupted) */
    public void render() throws IOException, TimeoutException {
        if (used) throw new IllegalStateException("This renderer has already been used.");
        used = true;
        start = System.currentTimeMillis();
        debug("External renderer: allowing " + timeoutMillis + " ms to complete.");

        {
            TextGrid grid = new TextGrid();
            grid.initialiseWithText(gridText, options.processingOptions);
            grid.printDebug();
            debug("Printed grid in " + (System.currentTimeMillis() - start) + " ms");
        }

        File workingDir = new File("/tmp");
        String classPath = Config.getExternalRenderClasspath(context);
        String javaExecutable = Config.getJavaExecutable(context);
        String className = org.stathissideris.ascii2image.core.CommandLineConverter.class
                .getCanonicalName();
        if (classPath == null) throw new IOException
                ("Missing configuration: " + Config.KEY_EXTERNAL_RENDER_CP
                        + " (should be written during build to automatic.properties).");
        List<String> command = new ArrayList<String>
	    (Arrays.asList(javaExecutable, "-cp", classPath, className));
        command.addAll(getCommandLineOptions());
        command.add("-"); // tell the renderer to use standard input & output
        debug("Command = " + command);
        process = new ProcessBuilder(command).directory(workingDir).start();

        // testing
//        contentType = CONTENT_TEXT;
//        response.setContentType(contentType);
//        process = new ProcessBuilder("ls", "-lh")
//                .directory(new File("/usr/local/tomcat/default"))

//                .start();

        // one thread to write ASCII art to the external process
        Thread writeThread = new Thread() {
            @Override
            public void run() {
                try {
                    PrintWriter writer = new PrintWriter(process.getOutputStream());
                    writer.print(gridText);
                    writer.close();
                } catch(Throwable t) {
                    addError("Exception writing grid text.", t);
                } finally {
                    waitForIt.countDown();
                }
            }
        };
        writeThread.start();

        // another thread to read the resulting image back in
        Thread readThread = new Thread(new Reader());
        readThread.start();

        try {
            long startExternal = System.currentTimeMillis();
            long remaining = timeoutMillis - (startExternal - start);
            boolean completed = waitForIt.await(remaining, TimeUnit.MILLISECONDS);
            long endExternal = System.currentTimeMillis();
            print("External renderer: " + (completed ? "completed" : "failed to complete")
                    + " in " + (endExternal - startExternal) + " ms");
            if (!completed) {
                // in case of race condition between timedOut and receivedImageOutput,
                // try to lean in favor of receivedImageOutput, to avoid fighting over response
                if (receivedImageOutput) {
                    // if we timed out but already received output, wait another second (kind of hackish)
                    completed = waitForIt.await(GRACE_PERIOD_MS, TimeUnit.SECONDS);
                    if (!completed)
                        timedOut = true;
                    debug("But output had already started; "
                            + (completed ? "completed" : "failed to complete")
                            + " after " + GRACE_PERIOD_MS + " ms grace period.");
                } else
                    timedOut = true;
                if (timedOut) {
                    process.destroy();
                    debug("Destroyed external process. Throwing TimeoutException.");
                    throw new TimeoutException("Render did not complete in time.");
                }
            }
        } catch (InterruptedException e) { // no reason for this to happen
            addError("Interrupted while waiting for external renderer.", e);
            process.destroy();
        }

        // should already be set by now, but just in case
        if (!isContentTypeSet())
            setContentTypeText();

        if (isContentTypeText()) {
            if (errors == null) { // should only be set on an error, unless we've overlooked something
                String msg = "No image available; reason unknown.";
                new IllegalStateException(msg).printStackTrace();
                response.getWriter().println(msg);
            }
            else { // print exceptions to response
                for (Pair<String, Throwable> p : errors) {
                    String msg = p.getFirst();
                    Throwable t = p.getSecond();
                    print(msg + (t == null ? "" : ":"));
                    response.getWriter().println(msg + (t == null ? "" : ":"));
                    if (t != null) {
                        t.printStackTrace(System.out);
                        t.printStackTrace(response.getWriter());
                    }
                }
            }
        }
    }

    private static void debug(String msg) { debug(msg, DEBUG); }
    private static void print(String msg) { debug(msg, true); }
    private static void debug(String msg, boolean actuallyPrint) {
        if (actuallyPrint)
            System.out.println("ExternalRenderer: " + msg);
    }

    public boolean isContentTypeSet() { return contentType != null; }
    public boolean isContentTypeImage() { return CONTENT_IMAGE.equals(contentType); }
    public boolean isContentTypeText() { return CONTENT_TEXT.equals(contentType); }
    public String getContentType() { return contentType; }

    private void setContentTypeText() { setContentType(false); }
    private void setContentTypeImage() { setContentType(true); }
    private void setContentType(boolean image) {
        if (contentType != null)
            throw new IllegalStateException("Content type already set to " + contentType);
        else {
            contentType = image ? CONTENT_IMAGE : CONTENT_TEXT;
            response.setContentType(contentType);
        }
    }

    private class Reader implements Runnable {
        private byte[] buffer = new byte[4096];

        @Override
        public void run() {
            try {
                // watch for output, in a loop, until exit occurs
                while (!timedOut) {
                    checkRendererOutputs();
                    try {
                        exit = process.exitValue();
                        debug(" - Exit value = " + exit);
                        checkRendererOutputs(); // one last time, to avoid race condition
                        break; // done
                    } catch(IllegalThreadStateException ignored) { // thrown when no exit
                        // there was no exit yet, so wait a little while for more output
                        Thread.sleep(RENDERER_POLL_INTERVAL);
                    }
                }
                if (timedOut)
                    debug(" --> Timed out; abandoning external renderer.");
                else
                    debug(" - Complete.");
            } catch(Throwable t) {
                addError("Exception reading renderer output.", t);
            } finally {
                waitForIt.countDown();
            }
        }
        private void checkRendererOutputs() throws IOException {
            debug("Checking renderer outputs ...");
            while (true) {
                int errorAvail = process.getErrorStream().available();
                if (errorAvail <= 0) break;

                debug(" - Received output on stderr: " + errorAvail + " bytes");
                LineNumberReader reader = new LineNumberReader
                        (new InputStreamReader(process.getErrorStream()));
                if (!isContentTypeSet())
                    setContentTypeText();
                while (true) {
                    String line = reader.readLine();
                    if (line == null)
                        break;
                    else {
                        if (isContentTypeText())
                            response.getWriter().println(line);
                        else {
                            String msg = "Unexpected stderr from renderer: " + line;
                            addError(msg);
                        }
                    }
                }
            }

            while (true) {
                int bytesRead = process.getInputStream().read(buffer);
                if (bytesRead <= 0) break; // nothing yet
                // if we timed out but already started receiving data, keep sending, because
                // the browser is already building an image; too late to back out now
                if (timedOut && !receivedImageOutput) break;

                debug(" - Received " + bytesRead + " bytes from external renderer process.");
                receivedImageOutput = true;
                if (!isContentTypeSet())
                    setContentTypeImage();
                if (isContentTypeImage())
                    response.getOutputStream().write(buffer, 0, bytesRead);
                else {
                    addError("Received output from renderer after content type already set to "
                            + contentType + "; attempting to print.");
                    LineNumberReader reader = new LineNumberReader
                            (new InputStreamReader(new ByteArrayInputStream(buffer, 0, bytesRead)));
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) break;
                        else addError(line);
                    }
                }
            }
        }
    }

    private List<String> getCommandLineOptions() {
        List<String> result = new ArrayList<String>();
        if (!options.renderingOptions.performAntialias()) result.add("-A");
        if (!options.renderingOptions.dropShadows()) result.add("-S");
        if (options.renderingOptions.getScale() != 1) {
            result.add("-s");
            result.add("" + options.renderingOptions.getScale());
        }
        if (options.processingOptions.areAllCornersRound()) result.add("-r");
        if (!options.processingOptions.performSeparationOfCommonEdges()) result.add("-E");
        if (options.renderingOptions.isFixedSlope()) result.add("-W");
        if (options.renderingOptions.needsTransparency()) result.add("-T");
        if (backgroundColorString != null) {
            result.add("-b");
            result.add(backgroundColorString);
        }
        // always UTF-8 encoding
        result.add("-e");
        result.add("UTF-8");
        return result;
    }
}
