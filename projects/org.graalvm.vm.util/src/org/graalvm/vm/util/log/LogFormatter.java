package org.graalvm.vm.util.log;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.graalvm.vm.util.StackTraceUtil;
import org.graalvm.vm.util.StringUtils;

public class LogFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		String src = StringUtils.rpad(record.getSourceClassName() + "#" + record.getSourceMethodName(), 60);
		if(record.getSourceClassName() == null || record.getSourceMethodName() == null) {
			if(record.getLoggerName() != null) {
				src = StringUtils.rpad("<" + record.getLoggerName() + ">", 60);
			} else {
				src = StringUtils.rpad("unknown source", 60);
			}
		}
		Level lvl = Levels.get(record.getLevel());
		char level = 'Z';
		if(lvl instanceof LogLevel) {
			level = ((LogLevel) lvl).getLetter();
		}
		String error = "";
		if(record.getThrown() != null) {
			error = "\n" + StackTraceUtil.getStackTrace(record.getThrown());
		}
		String threadID = String.format("%08x", record.getThreadID());
		String line = String.format("[%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS:%1$tL] %2$s %3$s %4$c %5$s%6$s%n",
				new Date(record.getMillis()), threadID, src, level, record.getMessage(), error);
		return line;
	}
}
