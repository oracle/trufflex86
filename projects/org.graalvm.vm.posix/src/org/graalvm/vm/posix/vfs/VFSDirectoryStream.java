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
package org.graalvm.vm.posix.vfs;

import java.util.Iterator;

import org.graalvm.vm.posix.api.Dirent;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.DirectoryStream;
import org.graalvm.vm.posix.api.io.Stat;

public abstract class VFSDirectoryStream extends DirectoryStream {
	private VFSDirectory dir;
	private Iterator<Dirent> iterator;
	private Dirent last = null;

	public VFSDirectoryStream(VFSDirectory dir, Iterator<Dirent> iterator) {
		this.dir = dir;
		this.iterator = iterator;
	}

	private Dirent peek() {
		if(last != null) {
			return last;
		} else {
			last = iterator.next();
			return last;
		}
	}

	private void next() {
		assert last != null;
		last = null;
	}

	private boolean hasNext() {
		return last != null || iterator.hasNext();
	}

	@Override
	public long getdents(PosixPointer ptr, long count, int type) {
		long total = 0;
		PosixPointer p = ptr;
		while(hasNext()) {
			Dirent dirent = peek();
			if(type == Dirent.DIRENT_64) {
				int size = dirent.size64();
				if(total + size <= count) {
					p = dirent.write64(p);
					total += size;
					next();
				} else {
					break;
				}
			} else if(type == Dirent.DIRENT_32) {
				int size = dirent.size32();
				if(total + size <= count) {
					p = dirent.write32(p);
					total += size;
					next();
				} else {
					break;
				}
			} else if(type == Dirent.DIRENT64) {
				int size = dirent.size64();
				if(total + size <= count) {
					p = dirent.writeDirent64(p);
					total += size;
					next();
				} else {
					break;
				}
			} else {
				throw new IllegalArgumentException("unknown type");
			}
		}
		return total;
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		return 0;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		dir.stat(buf);
	}
}
