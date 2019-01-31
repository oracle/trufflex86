package org.graalvm.vm.posix.api.net;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

public class Sockaddr implements Struct {
	public short sa_family;
	public byte[] sa_data = new byte[14];

	@Override
	public PosixPointer read(PosixPointer ptr) {
		PosixPointer p = ptr;
		sa_family = p.getI16();
		p.add(2);
		for(int i = 0; i < sa_data.length; i++) {
			sa_data[i] = p.getI8();
			p = p.add(1);
		}
		return p;
	}

	@Override
	public PosixPointer write(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI16(sa_family);
		p = p.add(2);
		for(int i = 0; i < sa_data.length; i++) {
			p.setI8(sa_data[i]);
			p = p.add(1);
		}
		return p;
	}

	public static Sockaddr get(PosixPointer ptr, int len) {
		short family = ptr.getI16();
		switch(family) {
		case Socket.AF_INET: {
			assert len == 16;
			SockaddrIn sin = new SockaddrIn();
			sin.read(ptr);
			return sin;
		}
		default: {
			Sockaddr sa = new Sockaddr();
			sa.read(ptr);
			return sa;
		}
		}
	}

	public int getSize() {
		return 16;
	}

	@Override
	public String toString() {
		return "{sa_family=" + Socket.addressFamily(sa_family) + "}";
	}
}
