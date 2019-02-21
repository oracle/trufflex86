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

import java.io.IOException;
import java.io.OutputStream;

public abstract class WordOutputStream extends OutputStream {
    private OutputStream parent;
    private long offset;

    public WordOutputStream(OutputStream parent) {
        this.parent = parent;
        this.offset = 0;
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }

    @Override
    public void flush() throws IOException {
        parent.flush();
    }

    @Override
    public void write(int value) throws IOException {
        parent.write(value);
        offset++;
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        parent.write(buffer);
        offset += buffer.length;
    }

    @Override
    public void write(byte[] buffer, int off, int length) throws IOException {
        parent.write(buffer, off, length);
        offset += length;
    }

    public void pad(int boundary, byte filler) throws IOException {
        long mod = offset % boundary;
        long missing = boundary - mod;
        if (mod == 0)
            return;
        byte[] pad = new byte[(int) missing];
        for (int i = 0; i < pad.length; i++)
            pad[i] = filler;
        write(pad);
    }

    public void pad32() throws IOException {
        pad32(0);
    }

    public void pad32(int filler) throws IOException {
        pad(32, (byte) filler);
    }

    public long tell() {
        return offset;
    }

    public abstract void write8bit(byte value) throws IOException;

    public abstract void write16bit(short value) throws IOException;

    public abstract void write32bit(int value) throws IOException;

    public abstract void write32bit(float value) throws IOException;

    public abstract void write64bit(long value) throws IOException;

    public abstract void write64bit(double value) throws IOException;
}
