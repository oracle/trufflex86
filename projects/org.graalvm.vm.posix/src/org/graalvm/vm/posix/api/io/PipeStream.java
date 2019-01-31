package org.graalvm.vm.posix.api.io;

import static org.graalvm.vm.posix.api.io.Stat.S_IFIFO;
import static org.graalvm.vm.posix.api.io.Stat.S_IRUSR;
import static org.graalvm.vm.posix.api.io.Stat.S_IWGRP;
import static org.graalvm.vm.posix.api.io.Stat.S_IWUSR;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.Timespec;

public class PipeStream extends Stream {
	private final InputStream in;
	private final OutputStream out;

	public PipeStream(InputStream in) {
		this.in = in;
		this.out = null;
	}

	public PipeStream(OutputStream out) {
		this.in = null;
		this.out = out;
	}

	protected PipeStream(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws PosixException {
		if(in != null) {
			try {
				int n = in.read(buf, offset, length);
				if(n == -1) {
					return 0;
				} else {
					return n;
				}
			} catch(IOException e) {
				throw new PosixException(Errno.EBADF); // TODO
			}
		} else {
			throw new PosixException(Errno.EBADF);
		}
	}

	@Override
	public int write(byte[] buf, int offset, int length) throws PosixException {
		if(out != null) {
			try {
				out.write(buf, offset, length);
				return length;
			} catch(IOException e) {
				throw new PosixException(Errno.EBADF); // TODO
			}
		} else {
			throw new PosixException(Errno.EBADF);
		}
	}

	@Override
	public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public int close() throws PosixException {
		boolean err = false;
		if(in != null) {
			try {
				in.close();
			} catch(IOException e) {
				err = true;
			}
		}
		if(out != null) {
			try {
				out.close();
			} catch(IOException e) {
				err = true;
			}
		}
		if(err) {
			throw new PosixException(Errno.EIO);
		}
		return 0;
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		buf.st_dev = 0; // TODO
		buf.st_ino = 0; // TODO
		buf.st_mode = S_IFIFO | S_IRUSR | S_IWUSR | S_IWGRP;
		buf.st_nlink = 0; // TODO
		buf.st_uid = 0; // TODO
		buf.st_gid = 0; // TODO
		buf.st_rdev = 0; // TODO
		buf.st_size = 0; // TODO
		buf.st_blksize = 0; // TODO
		buf.st_blocks = 0; // TODO
		buf.st_atim = new Timespec(); // TODO
		buf.st_mtim = new Timespec(); // TODO
		buf.st_ctim = new Timespec(); // TODO
	}

	@Override
	public void ftruncate(long length) throws PosixException {
		throw new PosixException(Errno.EINVAL);
	}
}
