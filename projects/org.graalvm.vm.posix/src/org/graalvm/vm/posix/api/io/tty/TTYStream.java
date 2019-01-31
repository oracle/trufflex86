package org.graalvm.vm.posix.api.io.tty;

import static org.graalvm.vm.posix.api.io.Stat.S_IFCHR;
import static org.graalvm.vm.posix.api.io.Stat.S_IRUSR;
import static org.graalvm.vm.posix.api.io.Stat.S_IWGRP;
import static org.graalvm.vm.posix.api.io.Stat.S_IWUSR;

import java.io.InputStream;
import java.io.OutputStream;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Ioctls;
import org.graalvm.vm.posix.api.io.PipeStream;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.termios.Termios;

public class TTYStream extends PipeStream {
	private final Termios termios;
	private final Winsize winsize;

	public TTYStream(InputStream in) {
		super(in);
		termios = Termios.getDefaultTerminal();
		winsize = new Winsize();
		statusFlags = Fcntl.O_RDONLY;
	}

	public TTYStream(OutputStream out) {
		super(out);
		termios = Termios.getDefaultTerminal();
		winsize = new Winsize();
		statusFlags = Fcntl.O_WRONLY;
	}

	public TTYStream(InputStream in, OutputStream out) {
		super(in, out);
		termios = Termios.getDefaultTerminal();
		winsize = new Winsize();
		statusFlags = Fcntl.O_RDWR;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		buf.st_mode = S_IFCHR | S_IRUSR | S_IWUSR | S_IWGRP;
	}

	@Override
	public int ioctl(long request, PosixPointer argp) throws PosixException {
		switch((int) request) {
		case Ioctls.TCGETS:
			termios.write(argp);
			return 0;
		case Ioctls.TIOCGWINSZ:
			winsize.write(argp);
			return 0;
		case Ioctls.TIOCSWINSZ:
			winsize.read(argp);
			return 0;
		default:
			return super.ioctl(request, argp);
		}
	}
}
