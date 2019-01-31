package org.graalvm.vm.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class MessageBox {
	public static void showMessageDialog(Component root, String message, String title, int options) {
		// for copying style
		JLabel label = new JLabel();
		Font font = label.getFont();
		Color fgcolor = label.getForeground();
		Color bgcolor = label.getBackground();

		// create some css from the label's font
		StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
		style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		style.append("font-size:" + font.getSize() + "pt;");
		style.append("color: rgb(" + fgcolor.getRed() + "," + fgcolor.getGreen() + "," + fgcolor.getBlue() +
				");");
		style.append("background-color: rgb(" + bgcolor.getRed() + "," + bgcolor.getGreen() + "," +
				bgcolor.getBlue() + ");");
		JEditorPane ep = new JEditorPane("text/html",
				message.replace("<body>", "<body style=\"" + style + "\">").replace("\n", "<br/>"));
		ep.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(e.getURL().toURI());
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		ep.setEditable(false);
		ep.setBorder(null);
		// ep.getCaret().deinstall(ep);
		JOptionPane.showMessageDialog(root, ep, title, options);
	}

	public static void showError(Component root, Throwable t) {
		t.printStackTrace();
		String message;
		if(t.getMessage() != null) {
			message = t.getClass().getSimpleName() + ": " + t.getMessage();
		} else {
			message = t.toString();
		}
		message = "<html><body>" + message.replace("&", "&amp;").replace("<", "&lt;") + "</body></html>";
		showMessageDialog(root, message, t.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
	}
}
