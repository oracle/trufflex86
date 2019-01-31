package org.graalvm.vm.util.log;

import java.util.logging.Level;

class LogLevel extends Level {
	private static final long serialVersionUID = 1L;
	private final char letter;

	protected LogLevel(String name, int value, char letter) {
		super(name, value);
		this.letter = letter;
	}

	public char getLetter() {
		return letter;
	}
}
