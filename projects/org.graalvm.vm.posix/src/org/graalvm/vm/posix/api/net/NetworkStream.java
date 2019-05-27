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

    public abstract int listen(int backlog) throws PosixException;

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

    public abstract long sendto(PosixPointer message, long length, int flags, PosixPointer dest_addr, int dest_len) throws PosixException;

    public abstract RecvResult recvfrom(PosixPointer buffer, long length, int flags) throws PosixException;

    public abstract long sendmsg(Msghdr message, int flags) throws PosixException;

    public abstract long recvmsg(Msghdr message, int flags) throws PosixException;

    public int sendmmsg(Mmsghdr[] msgvec, int vlen, int flags) throws PosixException {
        for (int i = 0; i < vlen; i++) {
            try {
                msgvec[i].msg_len = (int) sendmsg(msgvec[i].msg_hdr, flags);
            } catch (PosixException e) {
                if (i == 0) {
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
        if (addr instanceof InetSocketAddress) {
            InetSocketAddress iaddr = (InetSocketAddress) addr;
            byte[] ipaddr = iaddr.getAddress().getAddress();
            if (ipaddr.length == 4) {
                SockaddrIn sin = new SockaddrIn();
                sin.sa_family = Socket.AF_INET;
                sin.sin_addr = Endianess.get32bitBE(iaddr.getAddress().getAddress());
                sin.sin_port = (short) iaddr.getPort();
                return sin;
            } else if (ipaddr.length == 16) {
                SockaddrIn6 sin6 = new SockaddrIn6();
                sin6.sa_family = Socket.AF_INET;
                sin6.sin6_addr = ipaddr;
                sin6.sin6_port = (short) iaddr.getPort();
                return sin6;
            } else {
                throw new AssertionError("invalid ip address length");
            }
        } else {
            return null;
        }
    }

    protected SocketAddress getSocketAddress(PosixPointer address) throws PosixException {
        Sockaddr saddr = new Sockaddr();
        saddr.read(address);
        if (saddr.sa_family == Socket.AF_INET) {
            SockaddrIn addr = new SockaddrIn();
            addr.read(address);
            if (addr.sa_family != Socket.AF_INET) {
                throw new PosixException(Errno.EAFNOSUPPORT);
            }
            byte[] remoteAddrBytes = new byte[4];
            Endianess.set32bitBE(remoteAddrBytes, 0, addr.sin_addr);
            InetAddress remoteAddr = null;
            try {
                remoteAddr = InetAddress.getByAddress(remoteAddrBytes);
            } catch (UnknownHostException e) {
                throw new PosixException(Errno.EADDRNOTAVAIL);
            }
            int port = addr.sin_port;
            return new InetSocketAddress(remoteAddr, port);
        } else if (saddr.sa_family == Socket.AF_INET6) {
            SockaddrIn6 addr = new SockaddrIn6();
            addr.read(address);
            if (addr.sa_family != Socket.AF_INET6) {
                throw new PosixException(Errno.EAFNOSUPPORT);
            }
            InetAddress remoteAddr = null;
            try {
                remoteAddr = InetAddress.getByAddress(addr.sin6_addr);
            } catch (UnknownHostException e) {
                throw new PosixException(Errno.EADDRNOTAVAIL);
            }
            int port = addr.sin6_port;
            return new InetSocketAddress(remoteAddr, port);
        } else {
            throw new PosixException(Errno.EAFNOSUPPORT);
        }
    }
}
