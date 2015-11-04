package org.ditaa.web;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

public class Config {
    public static final String KEY_TITLE = "title";
    public static final String KEY_RESTART_FILE = "restart_file";
    public static final String KEY_RESTART_TIME = "restart_time";
    public static final String KEY_RENDER_EXTERNAL_PROCESS = "render_external";
    public static final String KEY_EXTERNAL_RENDER_CP = "external_render_classpath";
    public static final String KEY_JAVA_EXECUTABLE = "java_executable";

    /** Load all values from .properties files in WEB-INF into a single hash map. */
    public static HashMap<String,String> getAllProperties(ServletContext context) throws IOException {
        File webinf = getWebInfDir(context);
        File[] propFiles = webinf.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) { return name.endsWith(".properties"); }
        });
        HashMap<String,String> result = new HashMap<String,String>();
        for (File propFile : propFiles) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(propFile));
            for (Object k : properties.keySet()) {
                String key = (String) k;
                if (result.containsKey(key)) {
                    IOException ioe = new IOException
                            ("Multiple values for " + key + ": " + result.get(key) + ", " + properties.getProperty(key));
                    ioe.printStackTrace();
                    throw ioe;
                }
                result.put(key, properties.getProperty(key));
            }
        }
        return result;
    }

    public static boolean isRenderExternal(ServletContext context) throws IOException {
        return "true".equalsIgnoreCase(getAllProperties(context).get(KEY_RENDER_EXTERNAL_PROCESS));
    }

    public static String getTitle(ServletContext context) throws IOException {
        return getAllProperties(context).get(KEY_TITLE);
    }

    /** The file whose existence is polled by cron to trigger a reset. */
    public static String getRestartFilename(ServletContext context) throws IOException {
        return getAllProperties(context).get(KEY_RESTART_FILE);
    }

    /** The file whose existence is polled by cron to trigger a reset. */
    public static String getJavaExecutable(ServletContext context) throws IOException {
        String result = getAllProperties(context).get(KEY_JAVA_EXECUTABLE);
        if (result == null)
            result = "java";
        return result;
    }

    /** The classpath for rendering externally. */
    public static String getExternalRenderClasspath(ServletContext context) throws IOException {
        String dirName = getAllProperties(context).get(KEY_EXTERNAL_RENDER_CP);
        if (!dirName.endsWith(System.getProperty("file.separator")))
            dirName += System.getProperty("file.separator");
        File dir = new File(dirName);
        if (!dir.isDirectory()) throw new IOException
                ("Render classpath \"" + dirName + "\" is not a directory.");
        // list JARs
        String[] jarNames = dir.list(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) { return name.endsWith(".jar"); }
        });
        StringBuilder result = new StringBuilder();
        for (String jarName : jarNames)
            result.append(result.length() == 0 ? "" : ":").append(dirName).append(jarName);
        return result.toString();
    }

    /** A text description of the poll interval of the reset file. */
    public static String getRestartTimePeriod(ServletContext context) throws IOException {
        return getAllProperties(context).get(KEY_RESTART_TIME);
    }

    public static File getWebInfDir(ServletContext context) throws IOException {
        String path = context.getRealPath("WEB-INF");
        File result;

        // sometimes the WEB-INF dir "can't be found" even though it exists.
        if (path == null || path.length() == 0) {
            File rootDir = new File(context.getRealPath(""));
            result = new File(rootDir, "WEB-INF");
        }
        else
            result = new File(path);

        if (!result.exists()) throw new IOException(path + " doesn't exist.");
        return result;
    }
}
