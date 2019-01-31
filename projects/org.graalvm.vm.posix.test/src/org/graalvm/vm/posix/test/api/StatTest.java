package org.graalvm.vm.posix.test.api;

import static org.junit.Assert.assertEquals;

import org.graalvm.vm.posix.api.BytePosixPointer;
import org.graalvm.vm.posix.api.Posix;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.Stat;
import org.junit.Before;
import org.junit.Test;

public class StatTest {
	private Posix posix;

	@Before
	public void setup() {
		posix = new Posix();
	}

	@Test
	public void posixFstat001() throws PosixException {
		Stat stat = new Stat();
		posix.fstat(1, stat);
		assertEquals(0, stat.st_dev);
		assertEquals(0x2190, stat.st_mode);
	}

	@Test
	public void posixFstat002() throws PosixException {
		Stat stat = new Stat();
		posix.fstat(1, stat);
		byte[] memory = new byte[256];
		for(int i = 0; i < memory.length; i++) {
			memory[i] = 0x55;
		}
		PosixPointer ptr = new BytePosixPointer(memory);
		stat.write64(ptr);
		assertEquals(0, memory[0]);
		assertEquals(0, memory[1]);
		assertEquals(0, memory[2]);
		assertEquals(0, memory[3]);
		assertEquals(0, memory[4]);
		assertEquals(0, memory[5]);
		assertEquals(0, memory[6]);
		assertEquals(0, memory[7]);
	}

	@Test
	public void posixFstat003() throws PosixException {
		Stat stat = new Stat();
		posix.fstat(1, stat);
		byte[] memory = new byte[256];
		for(int i = 0; i < memory.length; i++) {
			memory[i] = 0x55;
		}
		PosixPointer ptr = new BytePosixPointer(memory, 16);
		stat.write64(ptr);
		assertEquals(0x55, memory[0]);
		assertEquals(0x55, memory[15]);
		assertEquals(0, memory[16]);
		assertEquals(0, memory[23]);
	}
}
