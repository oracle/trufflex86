package org.graalvm.vm.posix.vfs;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.util.log.Levels;
import org.graalvm.vm.util.log.Trace;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class NativeDirectory extends VFSDirectory {
	private static final Logger log = Trace.create(NativeDirectory.class);

	private final Path absolutePath;

	public NativeDirectory(VFS vfs, Path absolutePath) {
		super(vfs, getName(absolutePath), 0, 0, 0755);
		this.absolutePath = absolutePath;
	}

	public NativeDirectory(VFSDirectory parent, Path absolutePath) {
		super(parent, getName(absolutePath), 0, 0, 0755);
		this.absolutePath = absolutePath;
	}

	private static String getName(Path path) {
		if(path.getFileName() == null) {
			return "";
		} else {
			return path.getFileName().toString();
		}
	}

	@Override
	public void create(VFSEntry file) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public VFSDirectory createDirectory(String name, long uid, long gid, long permissions) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
		// TODO: touch
		return new NativeFile(this, absolutePath.resolve(name));
	}

	@Override
	public VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target)
			throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public void delete(String name) throws PosixException {
		Path path = absolutePath.resolve(name);
		log.log(Levels.WARNING, "Deleting file '" + path + "'");
		try {
			Files.delete(path);
		} catch(NoSuchFileException e) {
			throw new PosixException(Errno.ENOENT);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public VFSEntry getEntry(String name) throws PosixException {
		Path path = absolutePath.resolve(name);
		try {
			BasicFileAttributes info = Files
					.getFileAttributeView(path, BasicFileAttributeView.class, NOFOLLOW_LINKS)
					.readAttributes();
			if(info.isDirectory()) {
				return new NativeDirectory(this, path);
			} else if(info.isRegularFile()) {
				return new NativeFile(this, path);
			} else if(info.isSymbolicLink()) {
				return new NativeSymlink(this, path);
			} else {
				return new NativeSpecialFile(this, path);
			}
		} catch(NoSuchFileException e) {
			throw new PosixException(Errno.ENOENT);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public List<VFSEntry> list() throws PosixException {
		List<VFSEntry> result = new ArrayList<>();
		try(DirectoryStream<Path> entries = Files.newDirectoryStream(absolutePath)) {
			for(Path path : entries) {
				BasicFileAttributes info = Files.getFileAttributeView(path,
						BasicFileAttributeView.class, NOFOLLOW_LINKS).readAttributes();
				if(info.isDirectory()) {
					result.add(new NativeDirectory(this, path));
				} else if(info.isRegularFile()) {
					result.add(new NativeFile(this, path));
				} else if(info.isSymbolicLink()) {
					result.add(new NativeSymlink(this, path));
				} else {
					result.add(new NativeSpecialFile(this, path));
				}
			}
		} catch(IOException | DirectoryIteratorException e) {
			throw new PosixException(Errno.EIO);
		}
		return Collections.unmodifiableList(result);
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
	public void stat(Stat buf) throws PosixException {
		NativeFile.stat(absolutePath, buf);
	}
}
