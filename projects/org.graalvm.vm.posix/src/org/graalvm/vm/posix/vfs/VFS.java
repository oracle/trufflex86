/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    public String realpath(String path) throws PosixException {
        return realpath(path, cwd);
    }

    private String realpath(String path, String at) throws PosixException {
        return resolvePath(getPath(resolve(path, at)));
    }

    private String resolvePath(String path) throws PosixException {
        if (path.equals("") || path.equals(".")) {
            return cwd;
        }
        String[] parts = path.split("/");
        VFSDirectory dir = directory;

        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String part : parts) {
            i++;
            VFSEntry entry = dir.get(part);
            if (entry == null) {
                throw new PosixException(Errno.ENOENT);
            } else if (entry instanceof VFSDirectory) {
                dir = (VFSDirectory) entry;
                result.append('/').append(part);
            } else if (entry instanceof VFSSymlink) {
                VFSSymlink link = (VFSSymlink) entry;

                // resolve link
                String linkpath = link.readlink();
                if (linkpath.startsWith("/")) {
                    result = new StringBuilder(linkpath);
                    entry = get(getPath(realpath(linkpath, result.toString())));
                } else {
                    throw new AssertionError("relative symlinks not yet supported");
                }
                if (entry instanceof VFSDirectory) {
                    dir = (VFSDirectory) entry;
                } else { // VFSFile, VFSSpecialFile
                    if (i != parts.length) {
                        throw new PosixException(Errno.ENOTDIR);
                    } else {
                        dir = (VFSDirectory) entry;
                        return result.toString();
                    }
                }
            } else { // VFSFile, VFSSpecialFile
                if (i != parts.length) {
                    throw new PosixException(Errno.ENOTDIR);
                } else {
                    result.append('/').append(part);
                    return result.toString();
                }
            }
        }
        return result.toString();
    }

    private <T extends VFSEntry> T find(String path) throws PosixException {
        return find(path, true);
    }

    private <T extends VFSEntry> T find(String path, boolean resolve) throws PosixException {
        return find(path, resolve, directory);
    }

    @SuppressWarnings("unchecked")
    private static <T extends VFSEntry> T find(String path, boolean resolve, VFSDirectory root) throws PosixException {
        if (path.equals("")) {
            return (T) root;
        }
        String[] parts = path.split("/");
        VFSDirectory dir = root;
        int i = 0;
        for (String part : parts) {
            i++;
            VFSEntry entry = dir.get(part);
            if (entry == null) {
                throw new PosixException(Errno.ENOENT);
            } else if (entry instanceof VFSDirectory) {
                dir = (VFSDirectory) entry;
            } else if (entry instanceof VFSSymlink) {
                VFSSymlink link = (VFSSymlink) entry;
                if (!resolve && (i == parts.length) && !path.endsWith("/")) {
                    return (T) link;
                }
                // resolve link
                while (entry instanceof VFSSymlink) {
                    entry = link.getTarget();
                }
                if (entry instanceof VFSDirectory) {
                    dir = (VFSDirectory) entry;
                } else { // VFSFile, VFSSpecialFile
                    if (i != parts.length) {
                        throw new PosixException(Errno.ENOTDIR);
                    } else {
                        return (T) entry;
                    }
                }
            } else { // VFSFile, VFSSpecialFile
                if (i != parts.length) {
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
        String[] parts = Arrays.stream(path.split("/")).filter((x) -> x.length() > 0).filter((x) -> !x.equals(".")).toArray(String[]::new);
        List<String> normalized = new ArrayList<>();
        for (String part : parts) {
            if (part.equals("..")) {
                if (!normalized.isEmpty()) {
                    normalized.remove(normalized.size() - 1);
                }
            } else {
                normalized.add(part);
            }
        }
        String result = normalized.stream().collect(Collectors.joining("/"));
        // force symlink resolution in paths like symlink/, symlink/., and symlink/..
        if (path.endsWith("/") || path.endsWith("/.") || path.endsWith("/..")) {
            result += "/";
        }
        if (path.startsWith("/") && !result.equals("/")) {
            return "/" + result;
        } else {
            return result;
        }
    }

    public static String dirname(String path) {
        String normalized = normalize(path);
        String[] parts = normalized.split("/");
        if (parts.length == 1) {
            if (normalized.charAt(0) == '/') {
                return "/";
            } else {
                return ".";
            }
        }
        String result = Stream.of(parts).limit(parts.length - 1).filter((x) -> x.length() > 0).collect(Collectors.joining("/"));
        if (normalized.charAt(0) == '/') {
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
        if (path.startsWith("/")) {
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
        if (normalized.startsWith("/")) {
            return normalized.substring(1);
        } else {
            return normalized;
        }
    }

    public List<VFSEntry> list(String path) throws PosixException {
        VFSEntry entry = find(getPath(path));
        if (entry == null) {
            return Collections.emptyList();
        } else if (entry instanceof VFSDirectory) {
            return ((VFSDirectory) entry).readdir();
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

    public <T extends VFSEntry> T getat(@SuppressWarnings("unused") VFSEntry entry, String path) throws PosixException {
        if (path.startsWith("/")) {
            return find(getPath(path));
        } else {
            VFSDirectory dir = entry.getParent();
            return find(path, true, dir);
        }
    }

    public VFSDirectory getDirectory(String path) throws PosixException {
        VFSEntry entry = find(getPath(path));
        if (entry instanceof VFSDirectory) {
            return (VFSDirectory) entry;
        } else {
            throw new PosixException(Errno.ENOTDIR);
        }
    }

    public VFSFile getFile(String path) throws PosixException {
        VFSEntry entry = find(getPath(path));
        if (entry instanceof VFSFile) {
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

    public VFSSymlink symlink(String path, long uid, long gid, long permissions, String target) throws PosixException {
        String filename = basename(path);
        String dirname = dirname(path);
        VFSDirectory dir = getDirectory(dirname);
        return dir.symlink(filename, uid, gid, permissions, target);
    }

    public org.graalvm.vm.posix.api.io.Stream open(String path, int flags, int mode) throws PosixException {
        if (BitTest.test(flags, Fcntl.O_CREAT)) {
            try {
                VFSEntry entry = get(path);
                if (entry instanceof VFSFile) {
                    if (BitTest.test(flags, Fcntl.O_EXCL)) {
                        throw new PosixException(Errno.EEXIST);
                    } else {
                        return ((VFSFile) entry).open(flags, mode);
                    }
                } else if (entry instanceof VFSDirectory) {
                    throw new PosixException(Errno.EISDIR); // TODO
                } else {
                    throw new RuntimeException("This is a strange VFS entry: " + entry);
                }
            } catch (PosixException e) {
                if (e.getErrno() == Errno.ENOENT) {
                    VFSFile file = mkfile(path, 0, 0, mode);
                    return file.open(flags, mode);
                } else {
                    throw e;
                }
            }
        } else {
            VFSEntry entry = get(path);
            if (entry instanceof VFSFile) {
                return ((VFSFile) entry).open(flags, mode);
            } else if (entry instanceof VFSDirectory) {
                if (BitTest.test(flags, Fcntl.O_DIRECTORY)) {
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
        if (entry instanceof VFSSymlink) {
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
