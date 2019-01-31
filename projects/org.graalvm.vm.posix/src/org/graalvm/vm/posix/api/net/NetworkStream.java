package org.graalvm.vm.posix.api.net;

import static org.graalvm.vm.posix.api.io.Stat.S_IFSOCK;
import static org.graalvm.vm.posix.api.io.Stat.S_IRUSR;
import static org.graalvm.vm.posix.api.io.Stat.S_IWGRP;
import static org.graalvm.vm.posix.api.io.Stat.S_IWUSR;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectableChannel;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Timespec;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.util.io.Endianess;

public abstract class NetworkStream extends Stream {
	public abstract int setsockopt(int level, int option_name, int option_value) throws PosixException;

	public abstract int connect(PosixPointer address, int addressLen) throws PosixException;

	public abstract int bind(PosixPointer address, int addressLen) throws PosixException;

	@Override
	public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		buf.st_dev = 0; // TODO
		buf.st_ino = 0; // TODO
		buf.st_mode = S_IFSOCK | S_IRUSR | S_IWUSR | S_IWGRP;
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
	public void ftruncate(long size) throws PosixException {
		throw new PosixException(Errno.EINVAL);
	}

	public abstract SelectableChannel getChannel();

	public abstract long send(PosixPointer buffer, long length, int flags) throws PosixException;

	public abstract long recv(PosixPointer buffer, long length, int flags) throws PosixException;

	public abstract long sendto(PosixPointer message, long length, int flags, PosixPointer dest_addr, int dest_len)
			throws PosixException;

	public abstract RecvResult recvfrom(PosixPointer buffer, long length, int flags) throws PosixException;

	public abstract long sendmsg(Msghdr message, int flags) throws PosixException;

	public abstract long recvmsg(Msghdr message, int flags) throws PosixException;

	public int sendmmsg(Mmsghdr[] msgvec, int vlen, int flags) throws PosixException {
		for(int i = 0; i < vlen; i++) {
			try {
				msgvec[i].msg_len = (int) sendmsg(msgvec[i].msg_hdr, flags);
			} catch(PosixException e) {
				if(i == 0) {
					throw e;
				} else {
					return i;
				}
			}
		}
		return vlen;
	}

	public abstract Sockaddr getsockname() throws PosixException;

	public abstract Sockaddr getpeername() throws PosixException;

	public abstract int shutdown(int how) throws PosixException;

	protected Sockaddr getSockaddr(SocketAddress addr) {
		if(addr instanceof InetSocketAddress) {
			InetSocketAddress iaddr = (InetSocketAddress) addr;
			SockaddrIn sin = new SockaddrIn();
			sin.sa_family = Socket.AF_INET;
			sin.sin_addr = Endianess.get32bitBE(iaddr.getAddress().getAddress());
			sin.sin_port = (short) iaddr.getPort();
			return sin;
		} else {
			return null;
		}
	}

	protected SocketAddress getSocketAddress(PosixPointer address) throws PosixException {
		Sockaddr saddr = new Sockaddr();
		saddr.read(address);
		if(saddr.sa_family == Socket.AF_INET) {
			SockaddrIn addr = new SockaddrIn();
			addr.read(address);
			if(addr.sa_family != Socket.AF_INET) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			}
			byte[] remoteAddrBytes = new byte[4];
			Endianess.set32bitBE(remoteAddrBytes, 0, addr.sin_addr);
			InetAddress remoteAddr = null;
			try {
				remoteAddr = InetAddress.getByAddress(remoteAddrBytes);
			} catch(UnknownHostException e) {
				throw new PosixException(Errno.EADDRNOTAVAIL);
			}
			int port = addr.sin_port;
			return new InetSocketAddress(remoteAddr, port);
		} else {
			throw new PosixException(Errno.EAFNOSUPPORT);
		}
	}
}
