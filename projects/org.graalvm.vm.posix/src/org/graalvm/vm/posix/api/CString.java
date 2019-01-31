package org.graalvm.vm.posix.api;

public class CString {
	public static String cstr(PosixPointer s) {
		StringBuilder str = new StringBuilder();
		PosixPointer ptr = s;
		while(true) {
			byte b = ptr.getI8();
			if(b == 0) {
				break;
			} else {
				str.append((char) (b & 0xff));
				ptr = ptr.add(1);
			}
		}
		return str.toString();
	}

	public static PosixPointer strcpy(PosixPointer dst, String src) {
		PosixPointer ptr = dst;
		for(byte b : src.getBytes()) {
			ptr.setI8(b);
			ptr = ptr.add(1);
		}
		ptr.setI8((byte) 0);
		return ptr.add(1);
	}

	public static PosixPointer memcpy(PosixPointer dst, byte[] src, int length) {
		PosixPointer ptr = dst;
		for(int i = 0; i < length; i++) {
			ptr.setI8(src[i]);
			ptr = ptr.add(1);
		}
		return ptr;
	}
}
