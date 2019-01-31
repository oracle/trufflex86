package org.graalvm.vm.posix.vfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class NativeSymlink extends VFSSymlink {
	private Path path;

	public NativeSymlink(VFSDirectory parent, Path path) {
		super(parent, getName(path), 0, 0, 0755);
		this.path = path;
	}

	private static String getName(Path path) {
		if(path.getFileName() == null) {
			return "";
		} else {
			return path.getFileName().toString();
		}
	}

	@Override
	public VFSEntry getTarget() throws PosixException {
		try {
			Path p = path.toRealPath().toAbsolutePath();
			BasicFileAttributes info = Files
					.getFileAttributeView(p, BasicFileAttributeView.class, NOFOLLOW_LINKS)
					.readAttributes();
			if(info.isDirectory()) {
				return new NativeDirectory(getParent(), p);
			} else if(info.isRegularFile()) {
				return new NativeFile(getParent(), p);
			} else if(info.isSymbolicLink()) {
				return new NativeSymlink(getParent(), p);
			} else {
				return new NativeSpecialFile(getParent(), p);
			}
		} catch(NoSuchFileException e) {
			throw new PosixException(Errno.ENOENT);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public String readlink() throws PosixException {
		try {
			return Files.readSymbolicLink(path).toString();
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public long size() throws PosixException {
		try {
			return Files.getFileAttributeView(path, BasicFileAttributeView.class, NOFOLLOW_LINKS)
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
			FileTime atime = Files.getFileAttributeView(path, BasicFileAttributeView.class,
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
			FileTime mtime = Files.getFileAttributeView(path, BasicFileAttributeView.class,
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
			FileTime ctime = (FileTime) Files.getAttribute(path, "unix:ctime", NOFOLLOW_LINKS);
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
		NativeFile.stat(path, buf);
	}
}
