package org.graalvm.vm.posix.api.io;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.tty.TTYStream;

public class FileDescriptorManager {
	public static final int STDIN = 0;
	public static final int STDOUT = 1;
	public static final int STDERR = 2;

	private Map<Integer, FileDescriptor> fds;

	public FileDescriptorManager() {
		boolean tty = System.console() != null;
		fds = new HashMap<>();
		if(tty) {
			fds.put(STDIN, new FileDescriptor(new TTYStream(System.in)));
			fds.put(STDOUT, new FileDescriptor(new TTYStream(System.out)));
			fds.put(STDERR, new FileDescriptor(new TTYStream(System.err)));
		} else {
			fds.put(STDIN, new FileDescriptor(new PipeStream(System.in)));
			fds.put(STDOUT, new FileDescriptor(new PipeStream(System.out)));
			fds.put(STDERR, new FileDescriptor(new PipeStream(System.err)));
		}
	}

	public boolean used(int fd) {
		return fds.containsKey(fd);
	}

	public int next() {
		return next(0);
	}

	public int next(int low) {
		int fd;
		for(fd = low; used(fd); fd++) {
		}
		return fd;
	}

	public int allocate(Stream stream) {
		int fd = next();
		setStream(fd, stream);
		return fd;
	}

	public int allocate(Stream stream, int lowfd) {
		int fd = next(lowfd);
		setStream(fd, stream);
		return fd;
	}

	public void free(int fildes) {
		fds.remove(fildes);
	}

	public Stream getStream(int fildes) throws PosixException {
		FileDescriptor fd = fds.get(fildes);
		if(fd == null) {
			throw new PosixException(Errno.EBADF);
		}
		return fd.stream;
	}

	public FileDescriptor getFileDescriptor(int fildes) throws PosixException {
		FileDescriptor fd = fds.get(fildes);
		if(fd == null) {
			throw new PosixException(Errno.EBADF);
		}
		return fd;
	}

	public void setStream(int filedes, Stream stream) {
		fds.put(filedes, new FileDescriptor(stream));
	}

	public void setStream(int filedes, Stream stream, int flags) {
		fds.put(filedes, new FileDescriptor(stream, flags));
	}

	public int count() {
		return fds.size();
	}
}
