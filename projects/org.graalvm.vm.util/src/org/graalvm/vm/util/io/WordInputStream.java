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
package org.graalvm.vm.util.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public abstract class WordInputStream extends InputStream {
    private InputStream parent;
    private boolean eof = false;
    private long offset = 0;

    public WordInputStream(InputStream parent) {
        this.parent = parent;
    }

    public WordInputStream(InputStream parent, long offset) {
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    @Override
    public int available() throws IOException {
        return parent.available();
    }

    @Override
    public long skip(long n) throws IOException {
        offset += n;
        return parent.skip(n);
    }

    @Override
    public synchronized void mark(int readlimit) {
        parent.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        parent.reset();
    }

    @Override
    public boolean markSupported() {
        return parent.markSupported();
    }

    public boolean isEOF() {
        return eof;
    }

    @Override
    public int read() throws IOException {
        if (eof)
            throw new EOFException();
        int result = parent.read();
        if (result == -1) {
            eof = true;
            throw new EOFException();
        }
        offset++;
        return result;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        if (eof)
            throw new EOFException();
        int result = parent.read(buffer);
        if (result == -1) {
            eof = true;
            throw new EOFException();
        }
        offset += result;
        return result;
    }

    @Override
    public int read(byte[] buffer, int off, int length) throws IOException {
        if (eof)
            throw new EOFException();
        int result = parent.read(buffer, off, length);
        if (result == -1) {
            eof = true;
            throw new EOFException();
        }
        offset += result;
        return result;
    }

    public long tell() {
        return offset;
    }

    public abstract int read8bit() throws IOException;

    public abstract short read16bit() throws IOException;

    public abstract int read32bit() throws IOException;

    public abstract long read64bit() throws IOException;
}
