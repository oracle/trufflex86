package org.graalvm.vm.posix.api;

public class Info {
	// sysconf
	public long page_size = 4096;

	public long getPageMask() {
		return ~(page_size - 1);
	}

	// rlimit
	public long rlimit_cpu = Resource.RLIM_INFINITY;
	public long rlimit_fsize = Resource.RLIM_INFINITY;
	public long rlimit_data = Resource.RLIM_INFINITY;
	public long rlimit_stack = Resource.RLIM_INFINITY;
	public long rlimit_core = Resource.RLIM_INFINITY;
	public long rlimit_rss = Resource.RLIM_INFINITY;
	public long rlimit_nproc = Resource.RLIM_INFINITY;
	public long rlimit_nofile = Resource.RLIM_INFINITY;
	public long rlimit_memlock = Resource.RLIM_INFINITY;
	public long rlimit_as = Resource.RLIM_INFINITY;
	public long rlimit_locks = Resource.RLIM_INFINITY;
	public long rlimit_sigpending = Resource.RLIM_INFINITY;
	public long rlimit_msgqueue = Resource.RLIM_INFINITY;
	public long rlimit_nice = Resource.RLIM_INFINITY;
	public long rlimit_rtprio = Resource.RLIM_INFINITY;
	public long rlimit_rttime = Resource.RLIM_INFINITY;

	// signal handlers
	public Sigaction[] signal_handlers = new Sigaction[Signal._NSIG];

	public Info() {
		for(int i = 0; i < signal_handlers.length; i++) {
			signal_handlers[i] = new Sigaction();
		}
	}
}
