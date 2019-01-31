package org.graalvm.vm.util.log;

import java.util.logging.Level;

public class Levels {
	public static final Level FATAL = create("FATAL", 1000, 'F');
	public static final Level ERROR = create("ERROR", 950, 'E');
	public static final Level WARNING = create("WARNING", 900, 'W');
	public static final Level AUDIT = create("AUDIT", 850, 'A');
	public static final Level INFO = create("INFO", 800, 'I');
	public static final Level DEBUG = create("DEBUG", 500, 'D');
	public static final Level STDOUT = create("STDOUT", 1000, 'O');
	public static final Level STDERR = create("STDERR", 1000, 'R');

	private static final Level[] LEVELS = { DEBUG, INFO, AUDIT, WARNING, ERROR, FATAL };

	static Level create(String name, int value, char letter) {
		return new LogLevel(name, value, letter);
	}

	public static Level get(Level level) {
		if(level instanceof LogLevel) {
			return level;
		}
		int value = level.intValue();
		for(int i = 0; i < LEVELS.length; i++) {
			int lvl = LEVELS[i].intValue();
			if(value <= lvl) {
				return LEVELS[i];
			}
		}
		return level;
	}
}
