package org.graalvm.vm.posix.test.vfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.vfs.NativeFileStream;
import org.junit.Test;

public class NativeFileStreamTest {
	@Test
	public void testOpen1() throws Exception {
		NativeFileStream stream = new NativeFileStream(Paths.get("/proc/cpuinfo"), Fcntl.O_RDONLY);
		assertEquals(0, stream.close());
	}

	@Test
	public void testNonExistent1() throws Exception {
		try {
			NativeFileStream stream = new NativeFileStream(Paths.get("/nonexistent"), Fcntl.O_RDONLY);
			fail();
			stream.close();
		} catch(PosixException e) {
			assertEquals(Errno.ENOENT, e.getErrno());
		}
	}
}
