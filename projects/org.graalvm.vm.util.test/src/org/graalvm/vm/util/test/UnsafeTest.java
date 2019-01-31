package org.graalvm.vm.util.test;

import static org.junit.Assert.assertNotNull;

import org.graalvm.vm.util.UnsafeHolder;
import org.junit.Test;

import sun.misc.Unsafe;

public class UnsafeTest {
	@Test
	public void test() {
		Unsafe unsafe = UnsafeHolder.getUnsafe();
		assertNotNull(unsafe);
	}
}
