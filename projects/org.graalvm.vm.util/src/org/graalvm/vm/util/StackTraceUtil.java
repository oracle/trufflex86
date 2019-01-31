package org.graalvm.vm.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
	public static String getStackTrace(Throwable e) {
		StringWriter s = new StringWriter();
		e.printStackTrace(new PrintWriter(s));
		return s.toString();
	}

	public static StackTraceElement[] getTrace(int ignore) {
		Throwable t = new Throwable();
		StackTraceElement[] trace = t.getStackTrace();
		StackTraceElement[] result = new StackTraceElement[trace.length - ignore - 1];
		System.arraycopy(trace, ignore + 1, result, 0, result.length);
		return result;
	}

	public static StackTraceElement getLocation(String[] prefixes,
			int ignore) {
		if(prefixes.length == 0) {
			return null;
		}
		Throwable t = new Throwable();
		StackTraceElement[] trace = t.getStackTrace();
		for(int i = ignore + 1; i < trace.length; i++) {
			StackTraceElement entry = trace[i];
			for(String prefix : prefixes) {
				if(entry.getClassName().startsWith(prefix)) {
					return entry;
				}
			}
		}
		return null;
	}

	public static String getStackTrace(int ignore, boolean indent) {
		StringBuilder result = new StringBuilder();
		Throwable t = new Throwable();
		StackTraceElement[] trace = t.getStackTrace();
		for(int i = ignore + 1; i < trace.length; i++) {
			StackTraceElement entry = trace[i];
			if(indent) {
				result.append('\t');
			}
			result.append("at ").append(entry.getClassName())
					.append(".")
					.append(entry.getMethodName());
			String file = entry.getFileName();
			if(file != null) {
				int line = entry.getLineNumber();
				result.append('(').append(file).append(':')
						.append(line).append(')');
			} else if(entry.isNativeMethod()) {
				result.append("(Native)");
			}
			result.append('\n');
		}
		if(indent) {
			return '\t' + result.toString().trim();
		} else {
			return result.toString().trim();
		}
	}
}
