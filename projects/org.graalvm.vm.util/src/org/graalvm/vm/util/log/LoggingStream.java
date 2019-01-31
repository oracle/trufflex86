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
