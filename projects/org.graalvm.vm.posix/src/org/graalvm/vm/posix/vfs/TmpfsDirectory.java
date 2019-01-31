package org.graalvm.vm.posix.vfs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.PosixException;

public class TmpfsDirectory extends VFSDirectory {
	private Map<String, VFSEntry> files;
	private Date atime;
	private Date mtime;
	private Date ctime;

	public TmpfsDirectory(VFS vfs, String path, long uid, long gid, long permissions) {
		super(vfs, path, uid, gid, permissions);
		files = new HashMap<>();
		atime = new Date();
		mtime = atime;
		ctime = atime;
	}

	public TmpfsDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		super(parent, path, uid, gid, permissions);
		files = new HashMap<>();
		atime = new Date();
		mtime = atime;
		ctime = atime;
	}

	@Override
	protected void create(VFSEntry file) {
		mtime = new Date();
		files.put(file.getName(), file);
	}

	@Override
	protected VFSDirectory createDirectory(String name, long uid, long gid, long permissions)
			throws PosixException {
		TmpfsDirectory dir = new TmpfsDirectory(this, name, uid, gid, permissions);
		create(dir);
		return dir;
	}

	@Override
	protected TmpfsFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
		TmpfsFile file = new TmpfsFile(this, name, uid, gid, permissions);
		create(file);
		return file;
	}

	@Override
	protected VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target)
			throws PosixException {
		TmpfsSymlink symlink = new TmpfsSymlink(this, name, uid, gid, permissions, target);
		create(symlink);
		return symlink;
	}

	@Override
	protected void delete(String name) {
		mtime = new Date();
		files.remove(name);
	}

	@Override
	protected VFSEntry getEntry(String name) {
		atime = new Date();
		return files.get(name);
	}

	@Override
	protected List<VFSEntry> list() {
		atime = new Date();
		return files.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}

	@Override
	public long size() {
		return files.size();
	}

	@Override
	public void atime(Date time) {
		this.atime = time;
	}

	@Override
	public Date atime() {
		return atime;
	}

	@Override
	public void mtime(Date time) {
		this.mtime = time;
	}

	@Override
	public Date mtime() {
		return mtime;
	}

	@Override
	public void ctime(Date time) {
		this.ctime = time;
	}

	@Override
	public Date ctime() {
		return ctime;
	}

	@Override
	public String toString() {
		return "TmpfsDirectory[" + files.entrySet().stream()
				.map(Map.Entry::getValue)
				.map(Object::toString)
				.collect(Collectors.joining(",")) + "]";
	}
}
