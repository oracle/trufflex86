package org.graalvm.vm.posix.api;

public class Sigset implements Struct {
	public final long[] sig = new long[Signal._NSIG_WORDS];

	public void block(Sigset set) {
		for(int i = 0; i < sig.length; i++) {
			sig[i] |= set.sig[i];
		}
	}

	public void unblock(Sigset set) {
		for(int i = 0; i < sig.length; i++) {
			sig[i] &= ~set.sig[i];
		}
	}

	public void setmask(Sigset other) {
		for(int i = 0; i < sig.length; i++) {
			sig[i] = other.sig[i];
		}
	}

	public boolean isBlocked(int signo) {
		int no = signo - 1;
		int i = no / 64;
		long bit = 1L << (no % 64);
		return (sig[i] & bit) != 0;
	}

	@Override
	public PosixPointer read32(PosixPointer ptr) {
		PosixPointer p = ptr;
		for(int i = 0; i < sig.length; i++) {
			sig[i] = Integer.toUnsignedLong(p.getI32());
			p = p.add(4);
			sig[i] |= Integer.toUnsignedLong(p.getI32()) << 32;
			p = p.add(4);
		}
		return p;
	}

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		for(int i = 0; i < sig.length; i++) {
			p.setI32((int) sig[i]);
			p = p.add(4);
			p.setI32((int) (sig[i] >> 32));
			p = p.add(4);
		}
		return p;
	}

	@Override
	public PosixPointer read64(PosixPointer ptr) {
		PosixPointer p = ptr;
		for(int i = 0; i < sig.length; i++) {
			sig[i] = p.getI64();
			p = p.add(8);
		}
		return p;
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		for(int i = 0; i < sig.length; i++) {
			p.setI64(sig[i]);
			p = p.add(8);
		}
		return p;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder("Sigset[");
		boolean sep = false;
		for(int i = 0; i < Signal._NSIG; i++) {
			if(isBlocked(i)) {
				if(sep) {
					buf.append(",");
				} else {
					sep = true;
				}
				buf.append(i);
			}
		}
		return buf.append("]").toString();
	}
}
