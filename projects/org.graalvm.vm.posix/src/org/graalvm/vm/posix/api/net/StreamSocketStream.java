/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.posix.api.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Iovec;
import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.util.io.Endianess;
import org.graalvm.vm.util.log.Trace;

public class StreamSocketStream extends NetworkStream {
	private static final Logger log = Trace.create(StreamSocketStream.class);

	private SocketChannel socket;

	public StreamSocketStream() throws PosixException {
		try {
			socket = SocketChannel.open();
		} catch(IOException e) {
			log.log(Level.WARNING, "Cannot create socket: " + e.getMessage(), e);
			throw new PosixException(Errno.ENOMEM);
		}
	}

	@Override
	public void setFlags(int flags) throws PosixException {
		try {
			if(BitTest.test(flags, Fcntl.O_NONBLOCK) && socket.isBlocking()) {
				socket.configureBlocking(false);
			} else if(!BitTest.test(flags, Fcntl.O_NONBLOCK) && !socket.isBlocking()) {
				socket.configureBlocking(true);
			}
		} catch(IOException e) {
			log.log(Level.WARNING, "Error while changing stream flags: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
		super.setFlags(flags);
	}

	@Override
	public int setsockopt(int level, int option_name, int option_value) throws PosixException {
		try {
			switch(level) {
			case Socket.SOL_SOCKET:
				switch(option_name) {
				case Socket.SO_KEEPALIVE:
					socket.setOption(StandardSocketOptions.SO_KEEPALIVE, option_value != 0);
					return 0;
				case Socket.SO_REUSEADDR:
					socket.setOption(StandardSocketOptions.SO_REUSEADDR, option_value != 0);
					return 0;
				default:
					throw new PosixException(Errno.EINVAL);
				}
			case Tcp.SOL_TCP:
				switch(option_name) {
				case Tcp.TCP_NODELAY:
					socket.setOption(StandardSocketOptions.TCP_NODELAY, option_value != 0);
					return 0;
				default:
					throw new PosixException(Errno.EINVAL);
				}
			default:
				throw new PosixException(Errno.ENOPROTOOPT);
			}
		} catch(UnsupportedOperationException e) {
			throw new PosixException(Errno.ENOPROTOOPT);
		} catch(IllegalArgumentException e) {
			throw new PosixException(Errno.EINVAL);
		} catch(ClosedChannelException e) {
			throw new PosixException(Errno.EIO);
		} catch(IOException e) {
			log.log(Level.INFO, "I/O error while setting socket option: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int connect(PosixPointer address, int addressLen) throws PosixException {
		Sockaddr saddr = Sockaddr.get(address, addressLen);
		if(saddr instanceof SockaddrIn) {
			SockaddrIn addr = (SockaddrIn) saddr;
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
			SocketAddress remote = new InetSocketAddress(remoteAddr, port);
			try {
				socket.connect(remote);
				return 0;
			} catch(AlreadyConnectedException e) {
				throw new PosixException(Errno.EISCONN);
			} catch(ConnectionPendingException e) {
				throw new PosixException(Errno.EALREADY);
			} catch(UnsupportedAddressTypeException e) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			} catch(ClosedChannelException e) {
				throw new PosixException(Errno.EBADF);
			} catch(ConnectException e) {
				throw new PosixException(Errno.ECONNREFUSED);
			} catch(IOException e) {
				throw new PosixException(Errno.EIO);
			}
		} else {
			throw new PosixException(Errno.EAFNOSUPPORT);
		}
	}

	@Override
	public int bind(PosixPointer address, int addressLen) throws PosixException {
		Sockaddr saddr = new Sockaddr();
		saddr.read(address);
		if(saddr.sa_family == Socket.AF_INET) {
			SockaddrIn addr = new SockaddrIn();
			addr.read(address);
			if(addr.sa_family != Socket.AF_INET) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			}
			byte[] localAddrBytes = new byte[4];
			Endianess.set32bitBE(localAddrBytes, 0, addr.sin_addr);
			InetAddress localAddr = null;
			try {
				localAddr = InetAddress.getByAddress(localAddrBytes);
			} catch(UnknownHostException e) {
				throw new PosixException(Errno.EADDRNOTAVAIL);
			}
			int port = addr.sin_port;
			SocketAddress local = new InetSocketAddress(localAddr, port);
			try {
				socket.bind(local);
				return 0;
			} catch(AlreadyConnectedException e) {
				throw new PosixException(Errno.EISCONN);
			} catch(ConnectionPendingException e) {
				throw new PosixException(Errno.EALREADY);
			} catch(UnsupportedAddressTypeException e) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			} catch(ClosedChannelException e) {
				throw new PosixException(Errno.EBADF);
			} catch(IOException e) {
				throw new PosixException(Errno.EIO);
			}
		} else {
			throw new PosixException(Errno.EAFNOSUPPORT);
		}
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws PosixException {
		ByteBuffer b = ByteBuffer.wrap(buf, offset, length);
		try {
			int result = socket.read(b);
			if(result > 0) {
				return result;
			} else if(result == 0) {
				throw new PosixException(Errno.EAGAIN);
			} else {
				// -1 in Java = eof
				return 0;
			}
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int write(byte[] buf, int offset, int length) throws PosixException {
		ByteBuffer b = ByteBuffer.wrap(buf, offset, length);
		try {
			return socket.write(b);
		} catch(NotYetConnectedException e) {
			throw new PosixException(Errno.ECONNRESET);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public long send(PosixPointer buffer, long length, int flags) throws PosixException {
		return write(buffer, (int) length);
	}

	@Override
	public long recv(PosixPointer buffer, long length, int flags) throws PosixException {
		// TODO: use flags
		return read(buffer, (int) length);
	}

	@Override
	public long sendmsg(Msghdr message, int flags) throws PosixException {
		if(message.msg_name != null) {
			throw new PosixException(Errno.EISCONN);
		}
		throw new PosixException(Errno.EINVAL);
	}

	@Override
	public long sendto(PosixPointer message, long length, int flags, PosixPointer dest_addr, int dest_len)
			throws PosixException {
		if(message == null) {
			throw new PosixException(Errno.EFAULT);
		}

		if(dest_addr != null) {
			throw new PosixException(Errno.EISCONN);
		}

		return send(message, length, flags);
	}

	@Override
	public RecvResult recvfrom(PosixPointer buffer, long length, int flags) throws PosixException {
		// TODO: use the flags
		ByteBuffer buf = ByteBuffer.allocate((int) length);
		RecvResult result = new RecvResult();
		try {
			result.length = socket.read(buf);
			SocketAddress addr = socket.getRemoteAddress();

			// transfer buffer
			buf.flip();
			PosixPointer ptr = buffer;
			for(int i = 0; i < result.length; i++) {
				ptr.setI8(buf.get());
				ptr = ptr.add(1);
			}

			if(addr instanceof InetSocketAddress) {
				InetSocketAddress iaddr = (InetSocketAddress) addr;
				SockaddrIn sin = new SockaddrIn();
				sin.sa_family = Socket.AF_INET;
				sin.sin_port = (short) iaddr.getPort();
				sin.sin_addr = Endianess.get32bitBE(iaddr.getAddress().getAddress());
				result.sa = sin;
			}
		} catch(PortUnreachableException e) {
			throw new PosixException(Errno.ECONNREFUSED);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
		return result;
	}

	@Override
	public long recvmsg(Msghdr message, int flags) throws PosixException {
		if(flags != 0 && flags != Socket.MSG_TRUNC) {
			throw new PosixException(Errno.EOPNOTSUPP);
		}

		int length = 0;
		for(Iovec iov : message.msg_iov) {
			length += iov.iov_len;
		}

		ByteBuffer buf = ByteBuffer.allocate(length);

		length = 0;
		try {
			length = socket.read(buf);
			if(length == -1) {
				return 0;
			}
			if(BitTest.test(getFlags(), Fcntl.O_NONBLOCK) && length == 0) {
				throw new PosixException(Errno.EAGAIN);
			} else if(length == 0) {
				throw new AssertionError("zero length message cannot happen in blocking mode!");
			}
			SocketAddress addr = socket.getRemoteAddress();

			// transfer buffer
			buf.flip();
			int iovid = 0;
			int iovpos = 0;
			PosixPointer ptr = message.msg_iov[iovid].iov_base;
			for(int i = 0; i < length; i++) {
				ptr.setI8(buf.get());
				ptr = ptr.add(1);
				iovpos++;
				if(iovpos >= message.msg_iov[iovid].iov_len) {
					iovid++;
					iovpos = 0;
					// avoid out of bounds access to msg_iov[iovid]
					if(iovid >= message.msg_iov.length) {
						ptr = null;
					} else {
						ptr = message.msg_iov[iovid].iov_base;
					}
				}
			}

			message.msg_name = getSockaddr(addr);
			message.msg_control = null;
			message.msg_flags = 0;
			return length;
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public Sockaddr getsockname() throws PosixException {
		try {
			SocketAddress addr = socket.getLocalAddress();
			Sockaddr sa = getSockaddr(addr);
			if(sa == null) {
				throw new PosixException(Errno.EOPNOTSUPP);
			} else {
				return sa;
			}
		} catch(ClosedChannelException e) {
			throw new PosixException(Errno.EINVAL);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public Sockaddr getpeername() throws PosixException {
		try {
			SocketAddress addr = socket.getRemoteAddress();
			Sockaddr sa = getSockaddr(addr);
			if(sa == null) {
				throw new PosixException(Errno.EOPNOTSUPP);
			} else {
				return sa;
			}
		} catch(ClosedChannelException e) {
			throw new PosixException(Errno.EINVAL);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int shutdown(int how) throws PosixException {
		try {
			switch(how) {
			case Socket.SHUT_RD:
				socket.shutdownInput();
				return 0;
			case Socket.SHUT_WR:
				socket.shutdownOutput();
				return 0;
			case Socket.SHUT_RDWR:
				socket.shutdownInput();
				socket.shutdownOutput();
				return 0;
			default:
				throw new PosixException(Errno.EINVAL);
			}
		} catch(NotYetConnectedException | ClosedChannelException e) {
			throw new PosixException(Errno.ENOTCONN);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int close() throws PosixException {
		try {
			socket.close();
			return 0;
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public SocketChannel getChannel() {
		return socket;
	}
}
