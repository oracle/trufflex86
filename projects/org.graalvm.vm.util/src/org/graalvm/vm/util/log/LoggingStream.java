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
package org.graalvm.vm.util.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.vm.util.StackTraceUtil;

public class LoggingStream extends PrintStream {
	public LoggingStream(Level level) {
		this(level, null);
	}

	public LoggingStream(Level level, OutputStream additional) {
		super(new LoggingStreamImpl(level, additional), true);
	}

	private static class LoggingStreamImpl extends OutputStream {
		private static final Logger log = Trace.create("System");

		private final Level level;
		private final OutputStream additional;

		private StringBuilder buf = new StringBuilder();
		private Object lock = new Object();

		public LoggingStreamImpl(Level level, OutputStream additional) {
			this.level = level;
			this.additional = additional;
		}

		@Override
		public void write(int b) throws IOException {
			synchronized(lock) {
				buf.append(b);
				if(additional != null) {
					additional.write(b);
				}
			}
		}

		@Override
		public void write(byte[] b) throws IOException {
			synchronized(lock) {
				buf.append(new String(b));
				if(additional != null) {
					additional.write(b);
				}
			}
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			synchronized(lock) {
				buf.append(new String(b, off, len));
				if(additional != null) {
					additional.write(b, off, len);
				}
			}
		}

		@Override
		public void flush() throws IOException {
			synchronized(lock) {
				while(buf.indexOf("\n") != -1) {
					if(buf.length() == 0) {
						return;
					}
					if(buf.toString().trim().length() == 0) {
						buf = new StringBuilder();
						return;
					}
					int end = buf.indexOf("\n");
					if(end == 0) {
						buf.deleteCharAt(0);
					} else {
						String msg = buf.substring(0, end);
						if(msg.endsWith("\r"))
							msg = msg.substring(0, msg.length() - 2);
						print(msg);
					}
					buf.delete(0, end);
				}
			}
		}

		private void print(String buffer) {
			StackTraceElement[] trace = StackTraceUtil.getTrace(2);
			StackTraceElement caller = null;
			for(int i = 0; i < trace.length; i++) {
				String clazz = trace[i].getClassName();
				if(clazz.startsWith("java.io.") || clazz.startsWith("java.util.")
						|| clazz.startsWith("sun.nio.")
						|| clazz.startsWith(LoggingStream.class.getCanonicalName())) {
					continue;
				}
				caller = trace[i];
				break;
			}
			String sourceClass = null;
			String sourceMethod = null;
			if(caller != null) {
				sourceClass = caller.getClassName();
				sourceMethod = caller.getMethodName();
			}
			log.logp(level, sourceClass, sourceMethod, buffer);
		}
	}
}
