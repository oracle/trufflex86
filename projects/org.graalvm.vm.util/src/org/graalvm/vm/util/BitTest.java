package org.graalvm.vm.util;

public class BitTest {
	public static boolean test(int x, int flag) {
		return (x & flag) == flag;
	}

	public static boolean test(long x, long flag) {
		return (x & flag) == flag;
	}
}
