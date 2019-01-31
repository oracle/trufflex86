package org.graalvm.vm.posix.libc;

public class CString {
	public static int strlen(byte[] bytes, int offset) {
		for(int i = offset; i < bytes.length; i++) {
			if(bytes[i] == 0) {
				return i - offset;
			}
		}
		return bytes.length - offset;
	}

	public static String str(byte[] bytes, int offset) {
		if(offset >= bytes.length) {
			return null;
		}
		int length = strlen(bytes, offset);
		return new String(bytes, offset, length);
	}
}
