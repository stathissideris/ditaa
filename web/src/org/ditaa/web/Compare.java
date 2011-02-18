package org.ditaa.web;

public class Compare {
    static public boolean isBlank(String s) {
        return s == null || s.length() == 0 || s.trim().length() == 0;
    }
}
