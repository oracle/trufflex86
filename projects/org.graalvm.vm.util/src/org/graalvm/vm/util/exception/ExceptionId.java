package org.graalvm.vm.util.exception;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.util.log.Levels;

public class ExceptionId {
	private final String id;
	private final String message;

	public ExceptionId(String id, String message) {
		this.id = id;
		this.message = message;
		validateId();
		ExceptionIdRegistry.register(this);
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getMessageSubsystem() {
		if(id.length() == 9) {
			return id.substring(0, 4);
		} else if(id.length() == 10) {
			return id.substring(0, 5);
		} else {
			return null;
		}
	}

	public String getMessageId() {
		if(id.length() == 9) {
			return id.substring(4, 8);
		} else if(id.length() == 10) {
			return id.substring(5, 9);
		} else {
			return null;
		}
	}

	@Override
	public final String toString() {
		return getId() + ": " + getMessage();
	}

	public String formatEmbeddable(Throwable t) {
		String msg = t instanceof BaseException ? ((BaseException) t).formatEmbeddable() : t.getMessage();
		if(msg == null) {
			return getMessage();
		} else {
			return formatEmbeddable(msg);
		}
	}

	public String format(Throwable t) {
		String msg = t instanceof BaseException ? ((BaseException) t).formatEmbeddable() : t.getMessage();
		if(msg == null) {
			return format();
		} else {
			return format(msg);
		}
	}

	public String formatAlways(Throwable t) {
		String msg = t instanceof BaseException ? ((BaseException) t).formatEmbeddable() : t.getMessage();
		if(msg == null) {
			return format(t.toString());
		} else {
			return format(msg);
		}
	}

	public final String format() {
		return getId() + ": " + formatEmbeddable();
	}

	public final String formatEmbeddable() {
		return getMessage();
	}

	public final String format(Object... args) {
		return getId() + ": " + formatEmbeddable(args);
	}

	public final String formatEmbeddable(Object... args) {
		String msg = getMessage();
		if(args != null && args.length > 0) {
			if(msg.contains("{0}") || (msg.contains("{") && msg.contains("}"))) {
				return MessageFormat.format(msg, args);
			} else {
				if(args.length == 1 && args[0] == null) {
					return msg;
				} else {
					return msg + ": " + Stream.of(args).map(Object::toString)
							.collect(Collectors.joining(", "));
				}
			}
		} else {
			return msg;
		}
	}

	public Level getLevel() {
		char level = id.charAt(id.length() - 1);
		switch(level) {
		case 'I':
			return Levels.INFO;
		case 'W':
			return Levels.WARNING;
		case 'E':
			return Levels.ERROR;
		default:
			return Levels.ERROR;
		}
	}

	private void validateId() {
		if(id == null) {
			throw new NullPointerException();
		}
		if(id.length() != 9 && id.length() != 10) {
			throw new IllegalArgumentException("invalid id");
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, message);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		} else if(!(o instanceof ExceptionId)) {
			return false;
		}
		ExceptionId i = (ExceptionId) o;
		return id.equals(i.id) && message.equals(i.message);
	}
}
