package org.graalvm.vm.util.io;

import java.io.IOException;
import java.io.InputStream;

public class LEInputStream extends WordInputStream {
	private boolean debug = false;

	public LEInputStream(InputStream parent) {
		super(parent);
	}

	public LEInputStream(InputStream parent, long offset) {
		super(parent, offset);
	}

	@Override
	public int read8bit() throws IOException {
		if(debug) {
			int r = read();
			System.out.println("u8: " + r + " (s8: " + r + "; bin: " + Integer.toString(r, 2) + ")");
			return r;
		}
		return read();
	}

	@Override
	public short read16bit() throws IOException {
		byte[] buf = new byte[2];
		read(buf);
		if(debug) {
			short r = Endianess.get16bitLE(buf);
			System.out.println(
					"u16: " + Short.toUnsignedInt(r) + " (s16: " + r + "; bin: " +
							Integer.toString(Short.toUnsignedInt(r), 2) + ")");
			return r;
		}
		return Endianess.get16bitLE(buf);
	}

	@Override
	public int read32bit() throws IOException {
		byte[] buf = new byte[4];
		read(buf);
		if(debug) {
			int r = Endianess.get32bitLE(buf);
			System.out.println("u32: " + Integer.toUnsignedString(r) + " (s32: " + r + "; bin: " +
					Integer.toUnsignedString(r, 2) + ")");
			return r;
		}
		return Endianess.get32bitLE(buf);
	}

	@Override
	public long read64bit() throws IOException {
		byte[] buf = new byte[8];
		read(buf);
		if(debug) {
			long r = Endianess.get64bitLE(buf);
			System.out.println("u64: " + Long.toUnsignedString(r) + " (s64: " + r + "; bin: " +
					Long.toUnsignedString(r, 2) + ")");
			return r;
		}
		return Endianess.get64bitLE(buf);
	}

	public void debug() {
		this.debug = true;
	}

	public boolean isDebug() {
		return debug;
	}
}
