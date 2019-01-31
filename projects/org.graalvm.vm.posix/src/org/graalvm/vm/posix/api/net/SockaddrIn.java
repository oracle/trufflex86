package org.graalvm.vm.posix.api.net;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.util.io.Endianess;

public class SockaddrIn extends Sockaddr {
	public short sin_port; // Port number
	public int sin_addr; // Internet address

	@Override
	public PosixPointer read(PosixPointer ptr) {
		PosixPointer p = ptr;
		sa_family = p.getI16();
		p = p.add(2);
		sin_port = p.getI16();
		p = p.add(2);
		sin_addr = p.getI32();
		p = p.add(4);
		return p.add(8);
	}

	@Override
	public PosixPointer write(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI16(sa_family);
		p = p.add(2);
		p.setI16(sin_port);
		p = p.add(2);
		p.setI32(sin_addr);
		p = p.add(4);
		return p.add(8);
	}

	public String getAddressString() {
		byte[] bytes = new byte[4];
		Endianess.set32bitBE(bytes, 0, sin_addr);
		return Byte.toUnsignedInt(bytes[0]) + "." + Byte.toUnsignedInt(bytes[1]) + "." +
				Byte.toUnsignedInt(bytes[2]) + "." + Byte.toUnsignedInt(bytes[3]);
	}

	@Override
	public String toString() {
		return "{sa_family=" + Socket.addressFamily(sa_family) + ",sin_port=" + sin_port + ",sin_addr=\"" +
				getAddressString() + "\"}";
	}
}
