package org.graalvm.vm.posix.api.io.tty;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;

public class Winsize implements Struct {
	public short ws_row;
	public short ws_col;
	public short ws_xpixel;
	public short ws_ypixel;

	public Winsize() {
		// default window size
		ws_col = 80;
		ws_row = 24;
		ws_xpixel = 0;
		ws_ypixel = 0;
	}

	@Override
	public PosixPointer read(PosixPointer ptr) {
		PosixPointer p = ptr;
		ws_row = p.getI16();
		p = p.add(2);
		ws_col = p.getI16();
		p = p.add(2);
		ws_xpixel = p.getI16();
		p = p.add(2);
		ws_ypixel = p.getI16();
		p = p.add(2);
		return p.add(2);
	}

	public PosixPointer write(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI16(ws_row);
		p = p.add(2);
		p.setI16(ws_col);
		p = p.add(2);
		p.setI16(ws_xpixel);
		p = p.add(2);
		p.setI16(ws_ypixel);
		return p.add(2);
	}
}
