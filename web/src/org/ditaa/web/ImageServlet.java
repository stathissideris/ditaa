package org.ditaa.web;

import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

public class ImageServlet extends HttpServlet {
    public static final int MAX_TIMEOUT = 300; // seconds -- anything more than 5 minutes is unlikely to be feasible
    public static final int DEFAULT_TIMEOUT = 10;

    private static final boolean DEBUG = false;

    protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException { transmitImage(req, rsp); }
    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException { transmitImage(req, rsp); }

    public static final float MIN_SCALE = 0.2f, MAX_SCALE = 5f;

    // start the countdown to when we allow a restart
    static { RestartServlet.lastReset[0] = System.currentTimeMillis(); }

    /** Internal renderer */
    private static final Object INTERNAL_SETUP_SYNC = new Object();
    private static ExecutorService RENDER_EXECUTOR;
    static {
        // 12 workers -- setup on first use
        // RENDER_EXECUTOR = Executors.newFixedThreadPool(12);
        // BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(32);
        // RENDER_EXECUTOR = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS, queue);
    }

    /** External renderer: use a semaphore -- max 3 simultaneous. */
    // TODO: make this configurable
    private static final Semaphore EXTERNAL_RENDERER_LOCK = new Semaphore(3);

    public void transmitImage(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException
    {
        long start = System.currentTimeMillis();
        final ConversionOptions options = new ConversionOptions();
        options.processingOptions.setCharacterEncoding("UTF-8");

        @SuppressWarnings({"unchecked"}) Map<String,String[]> paramMap = request.getParameterMap();
        boolean noAntiAlias = paramMap.containsKey("A") || paramMap.containsKey("no-antialias");
        boolean noShadows = paramMap.containsKey("S") || paramMap.containsKey("no-shadows");
        boolean roundCorners = paramMap.containsKey("r") || paramMap.containsKey("round-corners");
        boolean noSeparation = paramMap.containsKey("E") || paramMap.containsKey("no-separation");
        boolean fixedSlope = paramMap.containsKey("W") || paramMap.containsKey("fixed-slope");
        boolean transparent = paramMap.containsKey("T") || paramMap.containsKey("transparent");
        float scale = getScale(request);

        Color background = null;
        String backgroundString = null;
        if (transparent)
            background = new Color(0, 0, 0, 0);
        else {
            String[] backgroundStrings = paramMap.get("background");
            if (backgroundStrings != null && backgroundStrings.length >= 1) {
                backgroundString = backgroundStrings[0];
                if (backgroundString != null)
                    try {
                        background = ConversionOptions.parseColor(backgroundString);
                    } catch(IllegalArgumentException e) {
                        System.err.println("Bad background color \"" + backgroundString + "\": " + e.getMessage());
                    }
            }
        }

        options.renderingOptions.setAntialias(!noAntiAlias);
        options.renderingOptions.setDropShadows(!noShadows);
        options.renderingOptions.setScale(scale);
        options.processingOptions.setAllCornersAreRound(roundCorners);
        options.processingOptions.setPerformSeparationOfCommonEdges(!noSeparation);
        options.renderingOptions.setFixedSlope(fixedSlope);
        if (background != null)
            options.renderingOptions.setBackgroundColor(background);
//        options.renderingOptions.setRenderDebugLines(false);
//        options.processingOptions.setColorCodesProcessingMode(ProcessingOptions.USE_COLOR_CODES);
//        options.processingOptions.setPrintDebugOutput(true);
//        options.processingOptions.setVerbose(true);

        String gridText = request.getParameter("grid");
        if (gridText == null || gridText.trim().length() == 0)
            gridText = "---\n| |\n---";

        System.out.println("Request for " + request.getRequestURI() + " referred from "
                + request.getHeader("referer") + "  from " + request.getRemoteHost() + " - " + new Date() + ":");
        try {
            int timeoutSeconds = getTimeout(request);
            if (Config.isRenderExternal(getServletContext()))
                renderExternal(options, backgroundString, gridText, timeoutSeconds, response);
            else
                renderInternal(options, gridText, timeoutSeconds, response);
            System.out.println("Completed in " + (System.currentTimeMillis() - start) + " ms");
        } catch(TimeoutException e) {
            String url = "timeout.jsp?" + HttpKit.adjustParameters(request, "timeout", "" + getTimeout(request));
            System.out.println("Timed out after " + (System.currentTimeMillis() - start) + " ms");
            debug("  --> Redirecting to " + url);
            response.sendRedirect(url);
        }
    }

    private void debug(String msg) {
        if (DEBUG)
            System.out.println("ImageServlet: " + msg);
    }

    private void renderExternal(final ConversionOptions options, String backgroundString,
                                final String gridText, int timeoutSeconds, HttpServletResponse response)
            throws IOException, TimeoutException
    {
        long timeoutMillis = timeoutSeconds * 1000;
        long start = System.currentTimeMillis();
        boolean acquired = false;
        try {
            acquired = EXTERNAL_RENDERER_LOCK.tryAcquire(timeoutMillis, TimeUnit.SECONDS);
            long elapsed = System.currentTimeMillis() - start;
            if (acquired)
                new ExternalRenderer(options, backgroundString, gridText, timeoutMillis - elapsed, getServletContext(), response).render();
            else {
                throw new TimeoutException("External still busy after "
                        + elapsed + " millis (" + timeoutSeconds + " allowed).");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (acquired)
                EXTERNAL_RENDERER_LOCK.release();
        }
    }

    private void renderInternal(final ConversionOptions options, final String gridText,
                                int timeoutSeconds, HttpServletResponse response)
            throws IOException, TimeoutException
    {
        final TextGrid grid = new TextGrid();

        // initialize thread pool on first use, to avoid unnecessary allocation
        synchronized (INTERNAL_SETUP_SYNC) {
            if (RENDER_EXECUTOR == null)
                RENDER_EXECUTOR = Executors.newFixedThreadPool(12);
        }

        Future<RenderedImage> future = RENDER_EXECUTOR.submit(new Callable<RenderedImage>() {
            public RenderedImage call() throws Exception {
                grid.initialiseWithText(gridText, options.processingOptions);
                grid.printDebug();

                Diagram diagram = new Diagram(grid, options);
                return new BitmapRenderer().renderToImage(diagram, options.renderingOptions);
            }
        });
        try {
            RenderedImage image = future.get(timeoutSeconds, TimeUnit.SECONDS);
            response.setContentType("image/png");
            response.setDateHeader("Expires", System.currentTimeMillis() + 2*60*60*1000L);
            ServletOutputStream os = response.getOutputStream();

            // the simple way -- no metadata
            ImageIO.write(image, "png", os);

            // the fun way -- metadata!
            // cribbed from http://stackoverflow.com/questions/721918
            // unfortunately, this doesn't seem to work -- it doesn't change the image!
            // plus, it creates compile warnings because it uses internal Sun classes
//            PNGMetadata meta = new PNGMetadata();
//            //noinspection unchecked
//            meta.tEXt_keyword.add("ditaa");
//            //noinspection unchecked
//            meta.tEXt_keyword.add("ascii art");
//            //noinspection unchecked
//            meta.tEXt_text.add(request.getRequestURL().toString());
//            ImageWriter writer = ImageIO.getImageWritersBySuffix("png").next();
//            writer.setOutput(ImageIO.createImageOutputStream(os));
//            IIOImage iioImage = new IIOImage(image, null, meta);
//            writer.write(null, iioImage, null);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static float getScale(HttpServletRequest request) {
        float scale = HttpKit.getFloat(request, 1f, "scale");
        if (scale < MIN_SCALE) scale = MIN_SCALE;
        else if (scale > MAX_SCALE) scale = MAX_SCALE;
        return scale;
    }

    /** Timeout in seconds. */
    public static int getTimeout(HttpServletRequest request) {
        int result = DEFAULT_TIMEOUT;
        if (request.getParameter("timeout") != null)
            try {
                result = Integer.parseInt(request.getParameter("timeout"));
            } catch(NumberFormatException ignored) { }
        if (result < 0) result = DEFAULT_TIMEOUT;
        if (result > MAX_TIMEOUT) result = MAX_TIMEOUT;
        return result;
    }

    private static final long serialVersionUID = 1L;
}
