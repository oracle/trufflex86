package org.graalvm.vm.util.exception;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExceptionIdRegistry {
	private static final Map<String, ExceptionId> messages = new HashMap<>();

	private ExceptionIdRegistry() {
	}

	public static synchronized void register(ExceptionId id) {
		ExceptionId old = messages.get(id.getId());
		if(old != null && !old.equals(id)) {
			throw new IllegalArgumentException("ExceptionId " + id.getId() + " is already used!");
		} else {
			messages.put(id.getId(), id);
		}
	}

	public static Set<ExceptionId> getExceptionIds() {
		return new HashSet<>(messages.values());
	}
}
