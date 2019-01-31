package org.graalvm.vm.posix.test.libc;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.libc.CString;
import org.junit.Test;

public class CStringTest {
	public static final byte[] data = { 0, 'n', 'a', 'm', 'e', '.', 0, 'V', 'a', 'r', 'i', 'a', 'b', 'l', 'e', 0,
			'a', 'b', 'l', 'e', 0, 0, 'x', 'x', 0 };

	@Test
	public void test1() {
		String ref = "";
		String act = CString.str(data, 0);
		assertEquals(ref, act);
	}

	@Test
	public void test2() {
		String ref = "name.";
		String act = CString.str(data, 1);
		assertEquals(ref, act);
	}

	@Test
	public void test3() {
		String ref = "Variable";
		String act = CString.str(data, 7);
		assertEquals(ref, act);
	}

	@Test
	public void test4() {
		String ref = "able";
		String act = CString.str(data, 11);
		assertEquals(ref, act);
	}

	@Test
	public void test5() {
		String ref = "able";
		String act = CString.str(data, 16);
		assertEquals(ref, act);
	}

	@Test
	public void test6() {
		String ref = "";
		String act = CString.str(data, 24);
		assertEquals(ref, act);
	}

	@Test
	public void test7() {
		byte[] bytes = { 'a', 'b', 'c' };
		String ref = "abc";
		String act = CString.str(bytes, 0);
		assertEquals(ref, act);
	}

	@Test
	public void test8() {
		byte[] bytes = { 'a', 'b', 'c' };
		String ref = null;
		String act = CString.str(bytes, 3);
		assertEquals(ref, act);
	}
}
