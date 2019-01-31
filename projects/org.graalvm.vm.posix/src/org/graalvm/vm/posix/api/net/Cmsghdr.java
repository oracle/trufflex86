package org.graalvm.vm.posix.api.net;

public class Cmsghdr {
	public long cmsg_len;
	public int cmsg_level;
	public int cmsg_type;
	public byte[] cmsg_data;
}
