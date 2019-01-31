package org.graalvm.vm.posix.test.api;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.api.io.Ioctl;
import org.graalvm.vm.posix.api.io.Ioctls;
import org.junit.Test;

public class IoctlsTest {
	public static final int TCGETS = Ioctl._IOR('t', 19, 44);
	public static final int TCSETS = Ioctl._IOW('t', 20, 44);
	public static final int TCSETSW = Ioctl._IOW('t', 21, 44);
	public static final int TCSETSF = Ioctl._IOW('t', 22, 44);

	public static final int TIOCSWINSZ = Ioctl._IOW('t', 103, 8);
	public static final int TIOCGWINSZ = Ioctl._IOR('t', 104, 8);
	public static final int TIOCSTART = Ioctl._IO('t', 110); /* start output, like ^Q */
	public static final int TIOCSTOP = Ioctl._IO('t', 111); /* stop output, like ^S */
	public static final int TIOCOUTQ = Ioctl._IOR('t', 115, 4); /* output queue size */

	@Test
	public void testIoctlValues() {
		assertEquals(TCGETS, Ioctls.TCGETS);
		assertEquals(TCSETS, Ioctls.TCSETS);
		assertEquals(TCSETSW, Ioctls.TCSETSW);
		assertEquals(TCSETSF, Ioctls.TCSETSF);

		assertEquals(TIOCSWINSZ, Ioctls.TIOCSWINSZ);
		assertEquals(TIOCGWINSZ, Ioctls.TIOCGWINSZ);
		assertEquals(TIOCSTART, Ioctls.TIOCSTART);
		assertEquals(TIOCSTOP, Ioctls.TIOCSTOP);
		assertEquals(TIOCOUTQ, Ioctls.TIOCOUTQ);
	}
}
