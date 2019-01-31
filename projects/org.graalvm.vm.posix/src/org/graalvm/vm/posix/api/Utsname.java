package org.graalvm.vm.posix.api;

public class Utsname implements Struct {
	public static final int SIZE = 6 * 65;

	public String sysname;
	public String nodename;
	public String release;
	public String version;
	public String machine;
	public String domainname;

	public PosixPointer write(PosixPointer p) {
		PosixPointer ptr = p;
		CString.strcpy(ptr, sysname);
		ptr = ptr.add(65);
		CString.strcpy(ptr, nodename);
		ptr = ptr.add(65);
		CString.strcpy(ptr, release);
		ptr = ptr.add(65);
		CString.strcpy(ptr, version);
		ptr = ptr.add(65);
		CString.strcpy(ptr, machine);
		ptr = ptr.add(65);
		CString.strcpy(ptr, domainname);
		return ptr.add(65);
	}
}
