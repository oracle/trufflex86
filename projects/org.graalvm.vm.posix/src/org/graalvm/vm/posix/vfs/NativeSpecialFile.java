package org.graalvm.vm.posix.vfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class NativeSpecialFile extends VFSSpecialFile {
	private Path absolutePath;

	public NativeSpecialFile(VFSDirectory parent, Path absolutePath) {
		super(parent, absolutePath.getFileName().toString(), 0, 0, 0755, 0);
		this.absolutePath = absolutePath;
	}

	@Override
	public long getUID() throws PosixException {
		try {
			return (int) Files.getAttribute(absolutePath, "unix:uid", NOFOLLOW_LINKS);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		} catch(UnsupportedOperationException e) {
			return 0;
		}
	}

	@Override
	public long getGID() throws PosixException {
		try {
			return (int) Files.getAttribute(absolutePath, "unix:gid", NOFOLLOW_LINKS);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		} catch(UnsupportedOperationException e) {
			return 0;
		}
	}

	@Override
	public Stream open(boolean read, boolean write) throws PosixException {
		int flags = 0;
		if(read && !write) {
			flags |= Fcntl.O_RDONLY;
		} else {
			throw new PosixException(Errno.EPERM);
		}
		return open(flags, 0);
	}

	@Override
	public Stream open(int flags, int mode) throws PosixException {
		return new NativeFileStream(absolutePath, flags);
	}

	@Override
	public long size() throws PosixException {
		try {
			return Files.getFileAttributeView(absolutePath, BasicFileAttributeView.class, NOFOLLOW_LINKS)
					.readAttributes().size();
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public void atime(Date time) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public Date atime() throws PosixException {
		try {
			FileTime atime = Files.getFileAttributeView(absolutePath, BasicFileAttributeView.class,
					NOFOLLOW_LINKS).readAttributes().lastAccessTime();
			return new Date(atime.toMillis());
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public void mtime(Date time) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public Date mtime() throws PosixException {
		try {
			FileTime mtime = Files.getFileAttributeView(absolutePath, BasicFileAttributeView.class,
					NOFOLLOW_LINKS).readAttributes().lastModifiedTime();
			return new Date(mtime.toMillis());
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public void ctime(Date time) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public Date ctime() throws PosixException {
		try {
			FileTime ctime = (FileTime) Files.getAttribute(absolutePath, "unix:ctime", NOFOLLOW_LINKS);
			return new Date(ctime.toMillis());
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		} catch(UnsupportedOperationException e) {
			return mtime();
		}
	}

	@Override
	public void chown(long owner, long group) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public void chmod(int mode) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		NativeFile.stat(absolutePath, buf);
	}
}
