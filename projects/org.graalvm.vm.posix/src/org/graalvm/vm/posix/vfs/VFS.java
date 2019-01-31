package org.graalvm.vm.posix.vfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.Utimbuf;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.util.BitTest;

public class VFS {
	private VFSDirectory directory;
	private Map<String, FileSystem> mounts;

	private String cwd;

	public VFS() {
		directory = new TmpfsDirectory(this, "", 0, 0, 0755); // "/" folder
		mounts = new HashMap<>();
		cwd = "/";
	}

	private <T extends VFSEntry> T find(String path) throws PosixException {
		return find(path, true);
	}

	@SuppressWarnings("unchecked")
	private <T extends VFSEntry> T find(String path, boolean resolve) throws PosixException {
		if(path.equals("")) {
			return (T) directory;
		}
		String[] parts = path.split("/");
		VFSDirectory dir = directory;
		int i = 0;
		for(String part : parts) {
			i++;
			VFSEntry entry = dir.get(part);
			if(entry == null) {
				throw new PosixException(Errno.ENOENT);
			} else if(entry instanceof VFSDirectory) {
				dir = (VFSDirectory) entry;
			} else if(entry instanceof VFSSymlink) {
				VFSSymlink link = (VFSSymlink) entry;
				if(!resolve && (i == parts.length)) {
					return (T) link;
				}
				// resolve link
				while(entry instanceof VFSSymlink) {
					entry = link.getTarget();
				}
				if(entry instanceof VFSDirectory) {
					dir = (VFSDirectory) entry;
				} else { // VFSFile, VFSSpecialFile
					if(i != parts.length) {
						throw new PosixException(Errno.ENOTDIR);
					} else {
						return (T) entry;
					}
				}
			} else { // VFSFile, VFSSpecialFile
				if(i != parts.length) {
					throw new PosixException(Errno.ENOTDIR);
				} else {
					return (T) entry;
				}
			}
		}
		return (T) dir;
	}

	public void unlink(String path) throws PosixException {
		String dirname = dirname(path);
		String filename = basename(path);
		VFSDirectory dir = getDirectory(dirname);
		dir.unlink(filename);
	}

	public static String normalize(String path) {
		String[] parts = Arrays.stream(path.split("/"))
				.filter((x) -> x.length() > 0)
				.filter((x) -> !x.equals("."))
				.toArray(String[]::new);
		List<String> normalized = new ArrayList<>();
		for(String part : parts) {
			if(part.equals("..")) {
				if(!normalized.isEmpty()) {
					normalized.remove(normalized.size() - 1);
				}
			} else {
				normalized.add(part);
			}
		}
		String result = normalized.stream().collect(Collectors.joining("/"));
		if(path.startsWith("/")) {
			return "/" + result;
		} else {
			return result;
		}
	}

	public static String dirname(String path) {
		String normalized = normalize(path);
		String[] parts = normalized.split("/");
		if(parts.length == 1) {
			if(normalized.charAt(0) == '/') {
				return "/";
			} else {
				return ".";
			}
		}
		String result = Stream.of(parts)
				.limit(parts.length - 1)
				.filter((x) -> x.length() > 0)
				.collect(Collectors.joining("/"));
		if(normalized.charAt(0) == '/') {
			return "/" + result;
		} else {
			return result;
		}
	}

	public static String basename(String path) {
		String[] parts = normalize(path).split("/");
		return parts[parts.length - 1];
	}

	public static String resolve(String path, String at) {
		if(path.startsWith("/")) {
			return normalize(path);
		} else {
			return normalize(at + "/" + path);
		}
	}

	public String resolve(String path) {
		return resolve(path, cwd);
	}

	private String getPath(String path) {
		String normalized = resolve(path);
		if(normalized.startsWith("/")) {
			return normalized.substring(1);
		} else {
			return normalized;
		}
	}

	public List<VFSEntry> list(String path) throws PosixException {
		VFSEntry entry = find(getPath(path));
		if(entry == null) {
			return Collections.emptyList();
		} else if(entry instanceof VFSDirectory) {
			return ((VFSDirectory) entry).list();
		} else {
			return Arrays.asList(entry);
		}
	}

	public <T extends VFSEntry> T get(String path) throws PosixException {
		return find(getPath(path));
	}

	public <T extends VFSEntry> T get(String path, boolean resolve) throws PosixException {
		return find(getPath(path), resolve);
	}

	public <T extends VFSEntry> T getat(@SuppressWarnings("unused") VFSEntry entry, String path)
			throws PosixException {
		if(path.startsWith("/")) {
			return find(getPath(path));
		} else {
			throw new AssertionError("getat not yet implemented");
		}
	}

	public VFSDirectory getDirectory(String path) throws PosixException {
		VFSEntry entry = find(getPath(path));
		if(entry instanceof VFSDirectory) {
			return (VFSDirectory) entry;
		} else {
			throw new PosixException(Errno.ENOTDIR);
		}
	}

	public VFSFile getFile(String path) throws PosixException {
		VFSEntry entry = find(getPath(path));
		if(entry instanceof VFSFile) {
			return (VFSFile) entry;
		} else {
			throw new PosixException(Errno.EISDIR);
		}
	}

	public VFSDirectory mkdir(String path, long uid, long gid, long permissions) throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname);
		return dir.mkdir(filename, uid, gid, permissions);
	}

	public VFSFile mkfile(String path, long uid, long gid, long permissions) throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname);
		return dir.mkfile(filename, uid, gid, permissions);
	}

	public VFSSymlink symlink(String path, long uid, long gid, long permissions, String target)
			throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname);
		return dir.symlink(filename, uid, gid, permissions, target);
	}

	public org.graalvm.vm.posix.api.io.Stream open(String path, int flags, int mode) throws PosixException {
		if(BitTest.test(flags, Fcntl.O_CREAT)) {
			try {
				VFSEntry entry = get(path);
				if(entry instanceof VFSFile) {
					if(BitTest.test(flags, Fcntl.O_EXCL)) {
						throw new PosixException(Errno.EEXIST);
					} else {
						return ((VFSFile) entry).open(flags, mode);
					}
				} else if(entry instanceof VFSDirectory) {
					throw new PosixException(Errno.EISDIR); // TODO
				} else {
					throw new RuntimeException("This is a strange VFS entry: " + entry);
				}
			} catch(PosixException e) {
				if(e.getErrno() == Errno.ENOENT) {
					VFSFile file = mkfile(path, 0, 0, mode);
					return file.open(flags, mode);
				} else {
					throw e;
				}
			}
		} else {
			VFSEntry entry = get(path);
			if(entry instanceof VFSFile) {
				return ((VFSFile) entry).open(flags, mode);
			} else if(entry instanceof VFSDirectory) {
				if(BitTest.test(flags, Fcntl.O_DIRECTORY)) {
					return ((VFSDirectory) entry).open(flags, mode);
				} else {
					throw new PosixException(Errno.EISDIR);
				}
			} else {
				throw new RuntimeException("This is a strange VFS entry: " + entry);
			}
		}
	}

	public String readlink(String path) throws PosixException {
		VFSEntry entry = get(path, false);
		if(entry instanceof VFSSymlink) {
			VFSSymlink link = (VFSSymlink) entry;
			return link.readlink();
		} else {
			throw new PosixException(Errno.EINVAL);
		}
	}

	public void stat(String path, Stat buf) throws PosixException {
		VFSEntry entry = get(path);
		entry.stat(buf);
	}

	public void chown(String path, long owner, long group) throws PosixException {
		VFSEntry entry = get(path);
		entry.chown(owner, group);
	}

	public void chmod(String path, int mode) throws PosixException {
		VFSEntry entry = get(path);
		entry.chmod(mode);
	}

	public void utime(String path, Utimbuf times) throws PosixException {
		VFSEntry entry = get(path);
		entry.utime(times);
	}

	public void chdir(String path) throws PosixException {
		getDirectory(path); // throw ENOENT/ENOTDIR if necessary
		cwd = path;
	}

	public String getcwd() {
		return cwd;
	}

	public void mount(String path, FileSystem fs) throws PosixException {
		VFSDirectory dir = getDirectory(path);
		dir.mount(fs.createMountPoint(this, path));
		mounts.put(path, fs);
	}

	@Override
	public String toString() {
		return "VFS[" + directory + "]";
	}
}
