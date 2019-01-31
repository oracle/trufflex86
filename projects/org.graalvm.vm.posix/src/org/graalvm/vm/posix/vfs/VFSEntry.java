package org.graalvm.vm.posix.vfs;

import java.util.Date;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.Timespec;
import org.graalvm.vm.posix.api.Utimbuf;
import org.graalvm.vm.posix.api.io.Stat;

public abstract class VFSEntry {
	private String path;
	private long uid;
	private long gid;
	private long permissions;
	private VFSDirectory parent;
	private VFS vfs;

	protected VFSEntry(VFS vfs, String path) {
		this.vfs = vfs;
		if(path.startsWith("/")) {
			this.path = path.substring(1);
		} else {
			this.path = path;
		}
	}

	protected VFSEntry(VFS vfs, String path, long uid, long gid, long permissions) {
		this(vfs, path);
		this.uid = uid;
		this.gid = gid;
		this.permissions = permissions;
	}

	protected VFSEntry(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		this(parent, path);
		this.uid = uid;
		this.gid = gid;
		this.permissions = permissions;
	}

	protected VFSEntry(VFSDirectory parent, String path) {
		this((VFS) null, path);
		this.parent = parent;
	}

	protected VFS getVFS() {
		if(vfs == null && parent != null) {
			return parent.getVFS();
		} else {
			return vfs;
		}
	}

	public VFSDirectory getParent() {
		return parent;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		String[] parts = path.split("/");
		return parts[parts.length - 1];
	}

	public String getEntryPath() {
		int last = path.lastIndexOf("/");
		if(last != -1) {
			return path.substring(0, last);
		} else {
			return "";
		}
	}

	@SuppressWarnings("unused")
	public void chmod(int mode) throws PosixException {
		permissions = mode;
	}

	@SuppressWarnings("unused")
	public void chown(long owner, long group) throws PosixException {
		uid = owner;
		gid = group;
	}

	@SuppressWarnings("unused")
	public long getUID() throws PosixException {
		return uid;
	}

	@SuppressWarnings("unused")
	public long getGID() throws PosixException {
		return gid;
	}

	@SuppressWarnings("unused")
	public long getPermissions() throws PosixException {
		return permissions;
	}

	public abstract long size() throws PosixException;

	public abstract void atime(Date time) throws PosixException;

	public abstract void mtime(Date time) throws PosixException;

	public abstract void ctime(Date time) throws PosixException;

	public abstract Date atime() throws PosixException;

	public abstract Date mtime() throws PosixException;

	public abstract Date ctime() throws PosixException;

	public void utime(Utimbuf times) throws PosixException {
		long atime;
		long mtime;
		if(times == null) {
			atime = new Date().getTime() / 1000;
			mtime = atime;
		} else {
			atime = times.actime;
			mtime = times.modtime;
		}
		atime(new Date(atime * 1000));
		mtime(new Date(mtime * 1000));
	}

	public void stat(Stat buf) throws PosixException {
		buf.st_dev = 0; // TODO
		buf.st_ino = 1; // TODO
		buf.st_mode = (int) getPermissions(); // TODO
		buf.st_nlink = 0; // TODO
		buf.st_uid = (int) getUID();
		buf.st_gid = (int) getGID();
		buf.st_rdev = 0; // TODO
		buf.st_size = size();
		buf.st_blksize = 4096; // TODO
		buf.st_blocks = (long) Math.ceil(buf.st_size / 512.0); // TODO
		buf.st_atim = new Timespec(atime());
		buf.st_mtim = new Timespec(mtime());
		buf.st_ctim = new Timespec(ctime());
	}

	@Override
	public String toString() {
		return "VFSEntry[" + path + "]";
	}
}
