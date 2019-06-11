package org.graalvm.vm.posix.vfs.proc;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Fcntl;
import org.graalvm.vm.posix.api.io.Stat;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.posix.vfs.VFSFile;

public class ProcfsReadOnlyStream extends Stream {
    private int pos;
    private byte[] content;
    private VFSFile file;

    public ProcfsReadOnlyStream(VFSFile file, byte[] content) {
        this.file = file;
        this.content = content;
        pos = 0;
        statusFlags = Fcntl.O_RDONLY;
    }

    private int read(int start, byte[] buf, int off, int len) {
        if (start < 0 || start > content.length) {
            return 0;
        }
        int length = len;
        if (start + length > content.length) {
            length = content.length - start;
        }
        System.arraycopy(content, start, buf, off, length);
        return length;
    }

    @Override
    public int read(byte[] buf, int offset, int length) throws PosixException {
        int bytes = read(pos, buf, offset, length);
        pos += bytes;
        return bytes;
    }

    @Override
    public int write(byte[] buf, int offset, int length) throws PosixException {
        throw new PosixException(Errno.EBADF);
    }

    @Override
    public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
        if ((int) fileOffset != fileOffset) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        int bytes = read((int) fileOffset, buf, offset, length);
        return bytes;
    }

    @Override
    public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
        throw new PosixException(Errno.EBADF);
    }

    @Override
    public int close() throws PosixException {
        return 0;
    }

    @Override
    public long lseek(long offset, int whence) throws PosixException {
        long newpos;
        switch (whence) {
            case SEEK_SET:
                newpos = offset;
                break;
            case SEEK_CUR:
                newpos = pos + offset;
                break;
            case SEEK_END:
                newpos = content.length + offset;
                break;
            default:
                throw new PosixException(Errno.EINVAL);
        }
        if (offset > 0 && newpos < 0) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        if (newpos < 0) {
            throw new PosixException(Errno.EINVAL);
        }
        // truncate to int
        if ((int) newpos != newpos) {
            throw new PosixException(Errno.EOVERFLOW);
        }
        pos = (int) newpos;
        return pos;
    }

    @Override
    public void stat(Stat buf) throws PosixException {
        file.stat(buf);
    }

    @Override
    public void ftruncate(long size) throws PosixException {
        throw new PosixException(Errno.EPERM);
    }
}
