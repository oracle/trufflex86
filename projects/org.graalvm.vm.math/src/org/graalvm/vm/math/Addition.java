package org.graalvm.vm.math;

public class Addition {
	public static boolean carry(long x, long y) {
		long result = x + y;
		return((x < 0 && y < 0) || ((x < 0 || y < 0) && result >= 0));
	}

	public static boolean carry(long x, long y, boolean ca) {
		boolean c1 = carry(x, y);
		long r = x + y;
		boolean c2 = carry(r, ca ? 1 : 0);
		return c1 | c2;
	}

	public static boolean carry(int x, int y) {
		long result = Integer.toUnsignedLong(x) + Integer.toUnsignedLong(y);
		return (result & 0x100000000L) != 0;
	}

	public static boolean carry(int x, int y, boolean ca) {
		long result = Integer.toUnsignedLong(x) + Integer.toUnsignedLong(y) + (ca ? 1L : 0L);
		return (result & 0x100000000L) != 0;
	}
}
