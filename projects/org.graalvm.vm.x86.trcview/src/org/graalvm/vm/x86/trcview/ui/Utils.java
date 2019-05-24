package org.graalvm.vm.x86.trcview.ui;

import java.awt.Color;

public class Utils {
    public static String color(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
    }

    public static String style(Color color) {
        return "color: " + color(color) + ";";
    }
}
