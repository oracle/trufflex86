package org.graalvm.vm.posix.api.io;

public class Poll {
	// @formatter:off
	/* These are specified by iBCS2 */
	public static final int POLLIN         = 0x0001;
	public static final int POLLPRI        = 0x0002;
	public static final int POLLOUT        = 0x0004;
	public static final int POLLERR        = 0x0008;
	public static final int POLLHUP        = 0x0010;
	public static final int POLLNVAL       = 0x0020;

	/* The rest seem to be more-or-less nonstandard. Check them! */
	public static final int POLLRDNORM     = 0x0040;
	public static final int POLLRDBAND     = 0x0080;
	public static final int POLLWRNORM     = 0x0100;
	public static final int POLLWRBAND     = 0x0200;
	public static final int POLLMSG        = 0x0400;
	public static final int POLLREMOVE     = 0x1000;
	public static final int POLLRDHUP      = 0x2000;

	public static final int POLLFREE       = 0x4000; /* currently only for epoll */

	public static final int POLL_BUSY_LOOP = 0x8000;
	// @formatter:on
}
