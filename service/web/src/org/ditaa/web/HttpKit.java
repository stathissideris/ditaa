package org.ditaa.web;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class HttpKit {
    public static float getFloat(HttpServletRequest request, float defaultVal, String name) {
        String s = request.getParameter(name);
        if (Compare.isBlank(s)) return defaultVal;
        else {
            try { return Float.parseFloat(s); }
            catch(NumberFormatException e) {
                System.out.println("Can't parse \"" + s + "\" as a float.  Defaulting to " + defaultVal + ".");
                return defaultVal;
            }
        }
    }

    public static String adjustParameters(HttpServletRequest request, String replaceName, String replaceValue) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(replaceName, replaceValue);
        return adjustParameters(request, map, true);
    }

    /** Rewrite <tt>request</tt>'s query string, replacing any parameters that appear in <tt>replacements</tt>.
     *  Null values in <tt>replacements</tt> mean remove that parameter.
     *  @param addMissing if true, add any values that are present in <tt>replacements</tt> but not in <tt>request</tt>.
     *  If false, ignore the extras in <tt>replacements</tt>. */
    public static String adjustParameters(HttpServletRequest request, Map<String, String> replacements, boolean addMissing) {
        StringBuffer result = new StringBuffer();
        Set<String> dun = new HashSet<String>();
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            dun.add(name);
            if (replacements.containsKey(name)) {
                String value = replacements.get(name);
                if (value != null)
                    appendParameter(result, name, value);
            }
            else {
                String[] values = request.getParameterValues(name);
                for (String v : values)
                    appendParameter(result, name, v);
            }
        }
        if (addMissing) {
            Set<String> missing = new HashSet<String>(replacements.keySet());
            missing.removeAll(dun);
            for (String name : missing)
                appendParameter(result, name, replacements.get(name));
        }
        return result.toString();
    }

    public static void appendParameter(StringBuffer buf, String name, String value) {
        try {
            buf.append(buf.length() == 0 ? "" : "&").append(name).append("=")
                    .append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
