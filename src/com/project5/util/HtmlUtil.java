package com.project5.util;

/**
 * Escapes text before it is echoed back into HTML (e.g. repopulating a form
 * field after a validation error, or showing a user's name). Prevents
 * stored/reflected XSS from user-supplied values such as first name.
 */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
