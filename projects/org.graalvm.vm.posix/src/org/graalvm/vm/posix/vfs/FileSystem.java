package org.graalvm.vm.posix.vfs;

import java.util.Date;
import java.util.List;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;

public abstract class FileSystem {
	private String type;

	public FileSystem(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public VFSDirectory createMountPoint(VFS vfs, String mountPoint) {
		final VFSDirectory root = getRoot();
		return new VFSDirectory(vfs, mountPoint, 0, 0, 0755) {
			@Override
			public Stream opendir(int flags, int mode) throws PosixException {
				return root.open(flags, mode);
			}

			@Override
			public void create(VFSEntry file) throws PosixException {
				root.create(file);
			}

			@Override
			public VFSDirectory createDirectory(String name, long uid, long gid, long permissions)
					throws PosixException {
				return root.createDirectory(name, uid, gid, permissions);
			}

			@Override
			public VFSFile createFile(String name, long uid, long gid, long permissions)
					throws PosixException {
				return root.createFile(name, uid, gid, permissions);
			}

			@Override
			public VFSSymlink createSymlink(String name, long uid, long gid, long permissions,
					String target) throws PosixException {
				return root.createSymlink(name, uid, gid, permissions, target);
			}

			@Override
			public void delete(String name) throws PosixException {
				root.delete(name);
			}

			@Override
			public VFSEntry getEntry(String name) throws PosixException {
				return root.get(name);
			}

			@Override
			public List<VFSEntry> list() throws PosixException {
				return root.list();
			}

			@Override
			public long size() throws PosixException {
				return root.size();
			}

			@Override
			public void atime(Date time) throws PosixException {
				root.atime(time);
			}

			@Override
			public Date atime() throws PosixException {
				return root.atime();
			}

			@Override
			public void mtime(Date time) throws PosixException {
				root.mtime(time);
			}

			@Override
			public Date mtime() throws PosixException {
				return root.mtime();
			}

			@Override
			public void ctime(Date time) throws PosixException {
				root.ctime(time);
			}

			@Override
			public Date ctime() throws PosixException {
				return root.ctime();
			}

			@Override
			public long getUID() throws PosixException {
				return root.getUID();
			}

			@Override
			public long getGID() throws PosixException {
				return root.getGID();
			}

			@Override
			public void stat(Stat buf) throws PosixException {
				root.stat(buf);
			}
		};
	}

	public abstract VFSDirectory getRoot();
}
