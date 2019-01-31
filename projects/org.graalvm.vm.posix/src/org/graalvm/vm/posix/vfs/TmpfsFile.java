package org.graalvm.vm.posix.vfs;

import java.util.Arrays;
import java.util.Date;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stream;

public class TmpfsFile extends VFSFile {
	private byte[] data;
	private Date atime;
	private Date mtime;
	private Date ctime;

	public TmpfsFile(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		super(parent, path, uid, gid, permissions);
		atime = new Date();
		mtime = atime;
		ctime = atime;
		data = new byte[0];
	}

	public void setContent(byte[] data) {
		mtime = new Date();
		this.data = data;
	}

	public byte[] getContent() {
		return data;
	}

	int read(int pos, byte[] buf, int offset, int length) {
		int len = length;
		if(pos + length > data.length) {
			len = data.length - pos;
		}
		if(len < 0) {
			return 0;
		}
		System.arraycopy(data, pos, buf, offset, len);
		return len;
	}

	void append(byte[] buf, int offset, int length) {
		mtime = new Date();
		int pos = data.length;
		data = Arrays.copyOf(data, data.length + length);
		System.arraycopy(buf, offset, data, pos, length);
	}

	void insert(int pos, byte[] buf, int offset, int length) {
		mtime = new Date();
		byte[] oldData = data;
		data = Arrays.copyOf(data, data.length + length);
		System.arraycopy(buf, offset, data, pos, length);
		System.arraycopy(oldData, pos, data, pos + length, oldData.length - pos);
	}

	void truncate(int length) throws PosixException {
		if(length < 0) {
			throw new PosixException(Errno.EINVAL);
		}
		this.data = Arrays.copyOf(data, length);
	}

	@Override
	public Stream open(boolean read, boolean write) throws PosixException {
		atime = new Date();
		return new TmpfsFileStream(this, read, write);
	}

	@Override
	public long size() {
		return data.length;
	}

	@Override
	public void atime(Date time) {
		atime = time;
	}

	@Override
	public Date atime() {
		return atime;
	}

	@Override
	public void mtime(Date time) {
		mtime = time;
	}

	@Override
	public Date mtime() {
		return mtime;
	}

	@Override
	public void ctime(Date time) {
		ctime = time;
	}

	@Override
	public Date ctime() {
		return ctime;
	}
}
