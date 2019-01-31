package org.graalvm.vm.posix.api.io.tty;

public class Kd {
	public static final int KDGKBTYPE = 0x4B33; /* get keyboard type */
	public static final int KB_84 = 0x01;
	public static final int KB_101 = 0x02; /* this is what we always answer */
	public static final int KB_OTHER = 0x03;
}
