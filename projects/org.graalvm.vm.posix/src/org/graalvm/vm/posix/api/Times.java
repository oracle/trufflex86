package org.graalvm.vm.posix.api;

public class Times {
	public static final long SC_CLK_TCK = 100;

	private static final long TIMES_FACTOR = 1000000 / Time.CLOCKS_PER_SEC;

	public long times(Tms buffer) throws PosixException {
		if(buffer == null) {
			throw new PosixException(Errno.EFAULT);
		}

		buffer.tms_utime = 0;
		buffer.tms_stime = 0;
		buffer.tms_cutime = 0;
		buffer.tms_cstime = 0;

		long t = System.currentTimeMillis();
		return t / TIMES_FACTOR;
	}
}
