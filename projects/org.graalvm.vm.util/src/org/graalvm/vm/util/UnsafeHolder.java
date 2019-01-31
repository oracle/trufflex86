package org.graalvm.vm.util;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.graalvm.vm.util.exception.Messages;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

import sun.misc.Unsafe;

public class UnsafeHolder {
	private static final Logger log = Trace.create(UnsafeHolder.class);

	private static final Unsafe unsafe = init();

	private static Unsafe init() {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch(NoSuchFieldException e) {
			log.log(Levels.ERROR, Messages.GET_UNSAFE_FAIL.format(e.toString()), e);
			throw new RuntimeException(e);
		} catch(SecurityException e) {
			log.log(Levels.ERROR, Messages.GET_UNSAFE_FAIL.format(e.toString()), e);
			throw new RuntimeException(e);
		} catch(IllegalArgumentException e) {
			log.log(Levels.ERROR, Messages.GET_UNSAFE_FAIL.format(e.toString()), e);
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			log.log(Levels.ERROR, Messages.GET_UNSAFE_FAIL.format(e.toString()), e);
			throw new RuntimeException(e);
		}
	}

	public static Unsafe getUnsafe() {
		return unsafe;
	}
}
