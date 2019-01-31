package org.graalvm.vm.util;

public class StringUtils {
    public static String repeat(String s, int count) {
        StringBuffer result = new StringBuffer(count * s.length());
        for (int i = 0; i < count; i++) {
            result.append(s);
        }
        return result.toString();
    }

    public static String fit(String s, int width) {
        if (s.length() > width) {
            if (width <= 7) {
                return s.substring(0, width);
            }
            int center = width / 2 + ((width & 1) == 0 ? 0 : 1);
            int p1 = center - 2;
            int p2 = s.length() - width + center + 1;
            String s1 = s.substring(0, p1);
            String s2 = s.substring(p2);
            return s1 + "..." + s2;
        } else {
            return s + repeat(" ", width - s.length());
        }
    }

    public static String pad(String s, int width) {
        return pad(s, width, true);
    }

    public static String rpad(String s, int width) {
        return pad(s, width, false);
    }

    public static String pad(String s, int width, boolean first) {
        if (s.length() > width) {
            if (first) {
                if (width <= 3) {
                    return s.substring(0, width);
                }
                return s.substring(0, width - 3) + "...";
            } else {
                if (width <= 3) {
                    return s.substring(s.length() - width);
                }
                return "..." + s.substring(s.length() - width + 3);
            }
        } else {
            return s + repeat(" ", width - s.length());
        }
    }

    public static String padWithOverflow(String s, int width) {
        if (s.length() >= width) {
            return s;
        } else {
            return s + repeat(" ", width - s.length());
        }
    }

    public static String tab(String s, int tabSize) {
        int pos = 0;
        StringBuilder buf = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\t') {
                do {
                    pos++;
                    pos %= tabSize;
                    buf.append(' ');
                } while (pos != 0);
            } else if (c == '\n') {
                pos = 0;
                buf.append(c);
            } else {
                pos++;
                pos %= tabSize;
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
