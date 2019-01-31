package org.graalvm.vm.posix.api.io;

import java.util.HashMap;
import java.util.Map;

public class Ioctls {
	private static final Map<Integer, String> ioctls;

	// NOTE: these fields have to be compile time constant to make switch statements work

	// termios
	public static final int TCGETS = 0x402c7413; // Ioctl._IOR('t', 19, 44);
	public static final int TCSETS = 0x802c7414; // Ioctl._IOW('t', 20, 44);
	public static final int TCSETSW = 0x802c7415; // Ioctl._IOW('t', 21, 44);
	public static final int TCSETSF = 0x802c7416; // Ioctl._IOW('t', 22, 44);

	public static final int TIOCSWINSZ = 0x80087467; // Ioctl._IOW('t', 103, 8);
	public static final int TIOCGWINSZ = 0x40087468; // Ioctl._IOR('t', 104, 8);
	public static final int TIOCSTART = 0x0000746e; // Ioctl._IO('t', 110); /* start output, like ^Q */
	public static final int TIOCSTOP = 0x0000746f; // Ioctl._IO('t', 111); /* stop output, like ^S */
	public static final int TIOCOUTQ = 0x40047473; // Ioctl._IOR('t', 115, 4); /* output queue size */

	public static final int FIOCLEX = 0x20006601; // Ioctl._IO('f', 1);
	public static final int FIONCLEX = 0x20006602; // Ioctl._IO('f', 2);

	static {
		ioctls = new HashMap<>();
		ioctls.put(TCGETS, "TCGETS");
		ioctls.put(TCSETS, "TCSETS");
		ioctls.put(TCSETSW, "TCSETSW");
		ioctls.put(TCSETSF, "TCSETSF");
		ioctls.put(TIOCSWINSZ, "TIOCSWINSZ");
		ioctls.put(TIOCGWINSZ, "TIOCGWINSZ");
		ioctls.put(TIOCSTART, "TIOCSTART");
		ioctls.put(TIOCSTOP, "TIOCSTOP");
		ioctls.put(TIOCOUTQ, "TIOCOUTQ");
		ioctls.put(FIOCLEX, "FIOCLEX");
		ioctls.put(FIONCLEX, "FIONCLEX");
	}

	public static String toString(int nr) {
		String s = ioctls.get(nr);
		if(s != null) {
			return s;
		} else {
			return Ioctl.toString(nr);
		}
	}
}
