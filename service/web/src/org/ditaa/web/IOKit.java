package org.ditaa.web;

import java.io.IOException;
import java.io.InputStream;

public class IOKit {
    public static void close(InputStream i) {
        try { i.close(); } catch (IOException ignored) { }
    }
}
