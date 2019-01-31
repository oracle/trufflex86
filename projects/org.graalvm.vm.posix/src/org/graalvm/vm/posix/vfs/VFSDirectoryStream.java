package org.graalvm.vm.posix.vfs;

import java.util.Iterator;

import org.graalvm.vm.posix.api.Dirent;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.io.DirectoryStream;
import org.graalvm.vm.posix.api.io.Stat;

public abstract class VFSDirectoryStream extends DirectoryStream {
	private VFSDirectory dir;
	private Iterator<Dirent> iterator;
	private Dirent last = null;

	public VFSDirectoryStream(VFSDirectory dir, Iterator<Dirent> iterator) {
		this.dir = dir;
		this.iterator = iterator;
	}

	private Dirent peek() {
		if(last != null) {
			return last;
		} else {
			last = iterator.next();
			return last;
		}
	}

	private void next() {
		assert last != null;
		last = null;
	}

	private boolean hasNext() {
		return last != null || iterator.hasNext();
	}

	@Override
	public long getdents(PosixPointer ptr, long count, int type) {
		long total = 0;
		PosixPointer p = ptr;
		while(hasNext()) {
			Dirent dirent = peek();
			if(type == Dirent.DIRENT_64) {
				int size = dirent.size64();
				if(total + size <= count) {
					p = dirent.write64(p);
					total += size;
					next();
				} else {
					break;
				}
			} else if(type == Dirent.DIRENT_32) {
				int size = dirent.size32();
				if(total + size <= count) {
					p = dirent.write32(p);
					total += size;
					next();
				} else {
					break;
				}
			} else if(type == Dirent.DIRENT64) {
				int size = dirent.size64();
				if(total + size <= count) {
					p = dirent.writeDirent64(p);
					total += size;
					next();
				} else {
					break;
				}
			} else {
				throw new IllegalArgumentException("unknown type");
			}
		}
		return total;
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		return 0;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		dir.stat(buf);
	}
}
