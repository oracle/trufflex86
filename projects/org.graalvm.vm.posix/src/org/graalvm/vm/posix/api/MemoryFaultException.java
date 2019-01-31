package org.graalvm.vm.posix.api;

@SuppressWarnings("serial")
public class MemoryFaultException extends RuntimeException {
	public MemoryFaultException() {
		// nothing
	}

	public MemoryFaultException(String message) {
		super(message);
	}

	public MemoryFaultException(Throwable cause) {
		super(cause);
	}

	public MemoryFaultException(String message, Throwable cause) {
		super(message, cause);
	}
}
