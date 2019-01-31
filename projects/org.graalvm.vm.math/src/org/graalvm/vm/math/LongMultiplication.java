package org.graalvm.vm.math;

public class LongMultiplication {
	private static final long MASK32 = 0xFFFFFFFFL;
	private static final int SHIFT32 = 32;

	public static long multiplyHigh(long x, long y) {
		if(x < 0 || y < 0) {
			// Use technique from section 8-2 of Henry S. Warren, Jr.,
			// Hacker's Delight (2nd ed.) (Addison Wesley, 2013), 173-174.
			long x1 = x >> SHIFT32;
			long x2 = x & MASK32;
			long y1 = y >> SHIFT32;
			long y2 = y & MASK32;
			long z2 = x2 * y2;
			long t = x1 * y2 + (z2 >>> SHIFT32);
			long z1 = t & MASK32;
			long z0 = t >> SHIFT32;
			z1 += x2 * y1;
			return x1 * y1 + z0 + (z1 >> SHIFT32);
		} else {
			// Use Karatsuba technique with two base 2^32 digits.
			long x1 = x >>> SHIFT32;
			long y1 = y >>> SHIFT32;
			long x2 = x & MASK32;
			long y2 = y & MASK32;
			long a = x1 * y1;
			long b = x2 * y2;
			long c = (x1 + x2) * (y1 + y2);
			long k = c - a - b;
			return (((b >>> SHIFT32) + k) >>> SHIFT32) + a;
		}
	}

	public static long multiplyHighUnsigned(long x, long y) {
		long high = multiplyHigh(x, y);
		return high + (((x < 0) ? y : 0) + ((y < 0) ? x : 0));
	}

	public static final int mulhwu(int a, int b) {
		long prod = Integer.toUnsignedLong(a) * Integer.toUnsignedLong(b);
		return (int) (prod >>> 32);
	}
}
