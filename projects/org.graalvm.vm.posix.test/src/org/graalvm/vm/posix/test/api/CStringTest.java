package org.graalvm.vm.posix.test.api;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.CString;
import org.graalvm.vm.posix.api.PosixPointer;
import org.junit.Test;

public class CStringTest {
	public static final byte[] data = { 0, 'n', 'a', 'm', 'e', '.', 0, 'V', 'a', 'r', 'i', 'a', 'b', 'l', 'e', 0,
			'a', 'b', 'l', 'e', 0, 0, 'x', 'x', 0 };

	private static PosixPointer ptr(int offset) {
		return new BytePosixPointer(data, offset);
	}

	private static BytePosixPointer buf(int sz) {
		return new BytePosixPointer(new byte[sz]);
	}

	@Test
	public void testRead1() {
		String ref = "";
		String act = CString.cstr(ptr(0));
		assertEquals(ref, act);
	}

	@Test
	public void testRead2() {
		String ref = "name.";
		String act = CString.cstr(ptr(1));
		assertEquals(ref, act);
	}

	@Test
	public void testRead3() {
		String ref = "Variable";
		String act = CString.cstr(ptr(7));
		assertEquals(ref, act);
	}

	@Test
	public void testRead4() {
		String ref = "able";
		String act = CString.cstr(ptr(11));
		assertEquals(ref, act);
	}

	@Test
	public void testRead5() {
		String ref = "able";
		String act = CString.cstr(ptr(16));
		assertEquals(ref, act);
	}

	@Test
	public void testRead6() {
		String ref = "";
		String act = CString.cstr(ptr(24));
		assertEquals(ref, act);
	}

	@Test
	public void testWrite1() {
		BytePosixPointer buf = buf(6);
		byte[] ref = { 'h', 'e', 'l', 'l', 'o', 0 };
		CString.strcpy(buf, "hello");
		assertArrayEquals(ref, buf.memory);
	}

	@Test
	public void testWrite2() {
		BytePosixPointer buf = buf(12);
		byte[] ref = { 'h', 'e', 'l', 'l', 'o', 0, 'w', 'o', 'r', 'l', 'd', 0 };
		CString.strcpy(buf, "hello\0world");
		assertArrayEquals(ref, buf.memory);
	}
}
