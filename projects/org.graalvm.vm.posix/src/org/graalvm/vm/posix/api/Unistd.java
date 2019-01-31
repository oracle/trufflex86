package org.graalvm.vm.posix.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.util.BitTest;

public class Unistd {
	public static final int R_OK = 4; /* Test for read permission. */
	public static final int W_OK = 2; /* Test for write permission. */
	public static final int X_OK = 1; /* Test for execute permission. */
	public static final int F_OK = 0; /* Test for existence. */

	public static String amode(int amode) {
		if(amode == F_OK) {
			return "F_OK";
		}

		List<String> result = new ArrayList<>();
		if(BitTest.test(amode, R_OK)) {
			result.add("R_OK");
		}
		if(BitTest.test(amode, W_OK)) {
			result.add("W_OK");
		}
		if(BitTest.test(amode, X_OK)) {
			result.add("X_OK");
		}

		return result.stream().collect(Collectors.joining("|"));
	}
}
