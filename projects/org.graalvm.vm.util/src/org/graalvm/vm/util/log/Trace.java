package org.graalvm.vm.util.log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.graalvm.vm.util.exception.BaseException;
import org.graalvm.vm.util.exception.ExceptionId;

public class Trace {
	public static final boolean debug;
	public static final PrintStream stdout;
	public static final PrintStream stderr;
	public static final PrintStream log;

	private static WrappedOutputStream logout;

	private static boolean initialized = false;

	static {
		String type = System.getProperty("log.type");
		if(type != null) {
			if(type.equals("debug")) {
				debug = true;
			} else {
				debug = false;
			}
			setup();
		} else {
			debug = false;
		}
		String logPath = System.getProperty("log.path");
		if(logPath == null) {
			logout = new WrappedOutputStream(System.out);
		} else {
			logout = new WrappedOutputStream(getOutputStream(logPath));
		}
		log = new PrintStream(logout);
		stdout = System.out;
		stderr = System.err;
	}

	private static class WrappedOutputStream extends OutputStream {
		private OutputStream out;

		public WrappedOutputStream(OutputStream parent) {
			this.out = parent;
		}

		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		public OutputStream getOutputStream() {
			return out;
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			out.flush();
		}
	}

	private static OutputStream getOutputStream(String path) {
		try {
			return new FileOutputStream(path);
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private Trace() {
	}

	public static void setup() {
		if(initialized) {
			return;
		}

		Logger globalLogger = Logger.getLogger(""); // root logger
		if(debug) {
			globalLogger.setLevel(Levels.DEBUG);
		}
		Handler[] handlers = globalLogger.getHandlers();
		for(Handler handler : handlers) {
			globalLogger.removeHandler(handler);
		}

		System.setOut(new LoggingStream(Levels.STDOUT));
		System.setErr(new LoggingStream(Levels.STDERR, stderr));
		StreamHandler handler = new StreamHandler(log, new LogFormatter()) {
			@Override
			public synchronized void publish(LogRecord record) {
				super.publish(record);
				super.flush();
			}
		};
		if(debug) {
			handler.setLevel(Levels.DEBUG);
		}
		globalLogger.addHandler(handler);
		initialized = true;
	}

	public static void setupConsoleApplication() {
		setupConsoleApplication(Levels.WARNING);
	}

	public static void setupConsoleApplication(Level level) {
		OutputStream out = logout.getOutputStream();
		setupConsoleApplication(level, out == System.out ? System.err : out);
	}

	public static void setupConsoleApplication(Level level, OutputStream out) {
		if(initialized) {
			return;
		}
		if(out == logout) {
			throw new IllegalArgumentException("cannot log to this stream");
		}
		logout.setOutputStream(out);
		Logger globalLogger = Logger.getLogger(""); // root logger
		globalLogger.setLevel(level);
		Handler[] handlers = globalLogger.getHandlers();
		for(Handler handler : handlers) {
			globalLogger.removeHandler(handler);
		}
		StreamHandler handler = new StreamHandler(out, new LogFormatter()) {
			@Override
			public synchronized void publish(LogRecord record) {
				super.publish(record);
				super.flush();
			}
		};
		handler.setLevel(level);
		globalLogger.addHandler(handler);
		initialized = true;
	}

	public static void print(String s) {
		log.print(s);
	}

	public static void println(String s) {
		log.println(s);
	}

	public static void printf(String fmt, Object... args) {
		log.printf(fmt, args);
	}

	public static Logger create(Class<?> tracedClass) {
		return Logger.getLogger(tracedClass.getCanonicalName());
	}

	public static Logger create(String name) {
		return Logger.getLogger(name);
	}

	public static String getLoggable(Throwable t) {
		if(t == null) {
			return BaseException.DEFAULT_ID.format();
		} else if(t instanceof BaseException) {
			BaseException e = (BaseException) t;
			return e.format();
		} else {
			return BaseException.DEFAULT_ID.format(t);
		}
	}

	public static String getLoggable(Throwable t, ExceptionId id, Object... args) {
		if(t == null) {
			return id.format(args);
		} else if(t instanceof BaseException) {
			BaseException e = (BaseException) t;
			Object[] params = Arrays.copyOf(args, args.length + 1);
			params[args.length] = e.formatEmbeddable();
			return id.format(params);
		} else {
			Object[] params = Arrays.copyOf(args, args.length + 1);
			params[args.length] = t.getMessage();
			return id.format(params);
		}
	}
}
