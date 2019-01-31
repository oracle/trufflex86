package org.graalvm.vm.posix.test.vfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.vfs.Tmpfs;
import org.graalvm.vm.posix.vfs.VFS;
import org.graalvm.vm.posix.vfs.VFSDirectory;
import org.graalvm.vm.posix.vfs.VFSEntry;
import org.junit.Before;
import org.junit.Test;

public class VFSTest {
	private VFS vfs;

	@Before
	public void setup() {
		vfs = new VFS();
	}

	@Test
	public void normalize001() {
		assertEquals("/", VFS.normalize("/"));
	}

	@Test
	public void normalize002() {
		assertEquals("/test", VFS.normalize("/test"));
	}

	@Test
	public void normalize003() {
		assertEquals("test", VFS.normalize("./test"));
	}

	@Test
	public void normalize004() {
		assertEquals("test", VFS.normalize("../test"));
	}

	@Test
	public void normalize005() {
		assertEquals("test", VFS.normalize("xyz/../test"));
	}

	@Test
	public void normalize006() {
		assertEquals("xyz/test", VFS.normalize("xyz/./test"));
	}

	@Test
	public void normalize007() {
		assertEquals("/xyz/test", VFS.normalize("/xyz/./test"));
	}

	@Test
	public void normalize008() {
		assertEquals("/xyz/test", VFS.normalize("/xyz//test"));
	}

	@Test
	public void normalize009() {
		assertEquals("xyz/test", VFS.normalize("xyz////test"));
	}

	@Test
	public void normalize010() {
		assertEquals("xyz/test", VFS.normalize("xyz////test/"));
	}

	@Test
	public void normalize011() {
		assertEquals("/xyz/test", VFS.normalize("//xyz////test////"));
	}

	@Test
	public void normalize012() {
		assertEquals("/xyz/test", VFS.normalize("/xyz/test/."));
	}

	@Test
	public void normalize013() {
		assertEquals("/xyz", VFS.normalize("/xyz/test/.."));
	}

	@Test
	public void dirname001() {
		assertEquals("/", VFS.dirname("/test"));
	}

	@Test
	public void dirname002() {
		assertEquals(".", VFS.dirname("test"));
	}

	@Test
	public void dirname003() {
		assertEquals("/tmp", VFS.dirname("/tmp/test.c"));
	}

	@Test
	public void dirname004() {
		assertEquals("/tmp/dir", VFS.dirname("/tmp/dir/test.c"));
	}

	@Test
	public void dirname005() {
		assertEquals("tmp/dir", VFS.dirname("tmp/dir/test.c"));
	}

	@Test
	public void basename001() {
		assertEquals("test", VFS.basename("/test"));
	}

	@Test
	public void basename002() {
		assertEquals("test", VFS.basename("test"));
	}

	@Test
	public void basename003() {
		assertEquals("test.c", VFS.basename("/tmp/test.c"));
	}

	@Test
	public void basename004() {
		assertEquals("test.c", VFS.basename("/tmp/dir/test.c"));
	}

	@Test
	public void basename005() {
		assertEquals("test.c", VFS.basename("tmp/dir/test.c"));
	}

	@Test
	public void testRoot001() throws PosixException {
		VFSEntry root = vfs.get("/");
		assertNotNull(root);
		assertEquals(root.getEntryPath(), "");
		assertTrue(root instanceof VFSDirectory);
	}

	@Test
	public void testMkdir001() throws PosixException {
		VFSDirectory root = vfs.get("/");
		VFSDirectory test = root.mkdir("test", 0, 0, 0755);
		assertNotNull(test);
		assertEquals("test", test.getName());
		assertEquals(0, test.getUID());
		assertEquals(0, test.getGID());
		assertEquals(0755, test.getPermissions());
		assertEquals(1, root.readdir().size());
		assertEquals(test, root.readdir().get(0));
		VFSDirectory dir = vfs.get("/test");
		assertNotNull(dir);
		assertSame(test, dir);
	}

	@Test
	public void testEnoent001() {
		try {
			vfs.get("/file");
			fail();
		} catch(PosixException e) {
			assertEquals(Errno.ENOENT, e.getErrno());
		}
	}

	@Test
	public void testEnotdir001() throws PosixException {
		VFSDirectory root = vfs.get("/");
		VFSDirectory test = root.mkdir("test", 0, 0, 0755);
		test.mkfile("file", 0, 0, 0755);
		try {
			vfs.get("/test/file/xyz");
			fail();
		} catch(PosixException e) {
			assertEquals(Errno.ENOTDIR, e.getErrno());
		}
	}

	@Test
	public void testMount001() throws PosixException {
		Tmpfs tmpfs = new Tmpfs(vfs);
		vfs.mkdir("/tmp", 0, 0, 0755);
		vfs.mount("/tmp", tmpfs);
		VFSDirectory mnt = vfs.get("/tmp");
		assertNotNull(mnt);
		assertEquals("tmp", mnt.getName());
	}
}
