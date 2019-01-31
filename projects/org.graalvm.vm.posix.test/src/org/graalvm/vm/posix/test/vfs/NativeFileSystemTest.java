package org.graalvm.vm.posix.test.vfs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.vfs.NativeFileSystem;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.junit.Test;

public class NativeFileSystemTest {
	@Test
	public void testRoot() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		long atime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:lastAccessTime")).toMillis();
		long mtime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:lastModifiedTime")).toMillis();
		long ctime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:ctime")).toMillis();
		VFSDirectory root = fs.getRoot();
		assertEquals(0, root.getUID());
		assertEquals(0, root.getGID());
		assertEquals(mtime, root.mtime().getTime());
		assertEquals(atime, root.atime().getTime());
		assertEquals(ctime, root.ctime().getTime());
		assertEquals(0755, root.getPermissions() & 0777);
	}

	@Test
	public void testProc001() throws PosixException {
		NativeFileSystem fs = new NativeFileSystem(null, "/proc");
		VFSDirectory root = fs.getRoot();
		List<VFSEntry> entries = root.readdir();
		assertEquals(1, entries.stream().filter((x) -> x.getName().equals("cpuinfo")).count());
	}

	@Test
	public void testMount001() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		VFS vfs = new VFS();
		vfs.mount("/", fs);

		BasicFileAttributes info = Files
				.getFileAttributeView(Paths.get("/proc"), BasicFileAttributeView.class)
				.readAttributes();

		Stat buf = new Stat();
		vfs.stat("/proc", buf);

		assertEquals(info.size(), buf.st_size);
		assertEquals(info.lastModifiedTime().toMillis(), buf.st_mtim.toMillis());

		byte[] ref = Files.readAllBytes(Paths.get("/proc/cpuinfo"));
		byte[] act = new byte[ref.length];
		Stream in = vfs.open("/proc/cpuinfo", Fcntl.O_RDONLY, 0);
		assertEquals(act.length, in.read(act, 0, act.length));
		in.close();
		assertArrayEquals(ref, act);
	}

	@Test
	public void testSymlink001() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		VFS vfs = new VFS();
		vfs.mount("/", fs);

		byte[] ref = Files.readAllBytes(Paths.get("/proc/self/cmdline"));
		byte[] act = new byte[ref.length];
		Stream in = vfs.open("/proc/self/cmdline", Fcntl.O_RDONLY, 0);
		assertEquals(act.length, in.read(act, 0, act.length));
		in.close();
		assertArrayEquals(ref, act);
	}

	@Test
	public void testSymlink002() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		VFS vfs = new VFS();
		vfs.mount("/", fs);

		String ref = Files.readSymbolicLink(Paths.get("/proc/self/exe")).toString();
		String act = vfs.readlink("/proc/self/exe");
		assertEquals(ref, act);
	}
}
