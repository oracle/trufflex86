package org.graalvm.vm.posix;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.util.StringUtils;
import org.graalvm.vm.util.exception.ExceptionId;

public class Messages {
	private static final Map<Integer, ExceptionId> posixErrors;

	static {
		posixErrors = new HashMap<>();
		for(int err : Errno.getErrorNumbers()) {
			addErrno(err);
		}
	}

	private static ExceptionId addErrno(int errno) {
		String id = Integer.toString(errno);
		if(id.length() < 4) {
			id = StringUtils.repeat("0", 4 - id.length()) + id;
		}
		ExceptionId exc = new ExceptionId("POSIX" + id + "E", Errno.toString(errno));
		posixErrors.put(errno, exc);
		return exc;
	}

	public static ExceptionId errno(int errno) {
		ExceptionId err = posixErrors.get(errno);
		if(err == null) {
			return addErrno(errno);
		} else {
			return err;
		}
	}
}
