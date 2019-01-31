package org.graalvm.vm.util.exception;

import java.util.Arrays;
import java.util.logging.Level;

public abstract class BaseException extends Exception {
	private static final long serialVersionUID = -1390276163104555006L;

	public static ExceptionId DEFAULT_ID = Messages.UNKNOWN;

	private ExceptionId id;

	public BaseException(ExceptionId id) {
		this.id = id;
	}

	public BaseException(ExceptionId id, String s) {
		super(s);
		this.id = id;
	}

	public BaseException(ExceptionId id, Throwable throwable) {
		super(throwable);
		this.id = id;
	}

	public BaseException(ExceptionId id, String s, Throwable throwable) {
		super(s, throwable);
		this.id = id;
	}

	public ExceptionId getId() {
		return id;
	}

	public Level getLevel() {
		return id.getLevel();
	}

	public Object[] getArguments() {
		return null;
	}

	private Object[] getFormatArgs() {
		String msg = getExceptionMessage();
		if(getCause() != null) {
			Throwable cause = getCause();
			if(cause instanceof BaseException && cause.toString().equals(msg)) {
				msg = ((BaseException) cause).formatEmbeddable();
			}
		}
		Object[] args = getArguments();
		if(msg == null) {
			return args;
		} else {
			if(args == null) {
				return new Object[] { msg };
			} else {
				Object[] params = Arrays.copyOf(args, args.length + 1);
				params[args.length] = msg;
				return params;
			}
		}
	}

	public String format() {
		return id.format(getFormatArgs());
	}

	public String formatEmbeddable() {
		return id.formatEmbeddable(getFormatArgs());
	}

	public String getExceptionMessage() {
		return super.getMessage();
	}

	@Override
	public String getMessage() {
		return format();
	}
}
