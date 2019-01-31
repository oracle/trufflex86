package org.graalvm.vm.posix.api.io;

import org.graalvm.vm.posix.api.PosixException;

public class FileDescriptor {
	private static final boolean CLOSE_ON_EXIT;

	public Stream stream;
	public int flags;
	public String name;

	private boolean closed = false;

	static {
		String close = System.getProperty("posix.closeOnExit");
		CLOSE_ON_EXIT = close != null && (close.equals("1") || close.equals("true"));
	}

	public FileDescriptor(Stream stream) {
		this(stream, 0);
	}

	public FileDescriptor(Stream stream, int flags) {
		this.stream = stream;
		this.flags = flags;
		stream.addref();
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int close() throws PosixException {
		if(!closed) {
			stream.delref();
			closed = true;
		}
		return 0;
	}

	@Override
	protected void finalize() {
		if(CLOSE_ON_EXIT && !closed) {
			try {
				close();
			} catch(PosixException e) {
				e.printStackTrace();
			}
		}
	}
}
