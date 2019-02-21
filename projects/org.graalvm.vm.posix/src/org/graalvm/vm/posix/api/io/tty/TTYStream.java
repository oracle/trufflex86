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
        switch ((int) request) {
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
