package org.graalvm.vm.posix.api.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.PosixPointer;
import org.graalvm.vm.posix.api.Struct;
import org.graalvm.vm.posix.api.Timespec;
import org.graalvm.vm.util.BitTest;

public class Stat implements Struct {
	// @formatter:off
	public static final int S_IFMT  = 00170000;
	public static final int S_IFSOCK = 0140000;
	public static final int S_IFLNK  = 0120000;
	public static final int S_IFREG  = 0100000;
	public static final int S_IFBLK  = 0060000;
	public static final int S_IFDIR  = 0040000;
	public static final int S_IFCHR  = 0020000;
	public static final int S_IFIFO  = 0010000;
	public static final int S_ISUID  = 0004000;
	public static final int S_ISGID  = 0002000;
	public static final int S_ISVTX  = 0001000;

	public static final int S_IRWXU = 00700;
	public static final int S_IRUSR = 00400;
	public static final int S_IWUSR = 00200;
	public static final int S_IXUSR = 00100;

	public static final int S_IRWXG = 00070;
	public static final int S_IRGRP = 00040;
	public static final int S_IWGRP = 00020;
	public static final int S_IXGRP = 00010;

	public static final int S_IRWXO = 00007;
	public static final int S_IROTH = 00004;
	public static final int S_IWOTH = 00002;
	public static final int S_IXOTH = 00001;
	// @formatter:on

	public long st_dev;
	public long st_ino;
	public int st_mode;
	public int st_nlink;
	public int st_uid;
	public int st_gid;
	public long st_rdev;
	public long st_size;
	public int st_blksize;
	public long st_blocks;
	public Timespec st_atim = new Timespec();
	public Timespec st_mtim = new Timespec();
	public Timespec st_ctim = new Timespec();

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI32((int) st_dev);
		p = p.add(4);
		p.setI32((int) st_ino);
		p = p.add(4);
		p.setI32(st_mode);
		p = p.add(4);
		p.setI32(st_nlink);
		p = p.add(4);
		p.setI32(st_uid);
		p = p.add(4);
		p.setI32(st_gid);
		p = p.add(4);
		p.setI32((int) st_rdev);
		p = p.add(4);
		// unsigned long __pad1
		p = p.add(4);
		p.setI32((int) st_size);
		p = p.add(4);
		p.setI32(st_blksize);
		p = p.add(4);
		// int __pad2
		p = p.add(4);
		p.setI32((int) st_blocks);
		p = p.add(4);
		p.setI32((int) st_atim.tv_sec);
		p = p.add(4);
		p.setI32((int) st_atim.tv_nsec);
		p = p.add(4);
		p.setI32((int) st_mtim.tv_sec);
		p = p.add(4);
		p.setI32((int) st_mtim.tv_nsec);
		p = p.add(4);
		p.setI32((int) st_ctim.tv_sec);
		p = p.add(4);
		p.setI32((int) st_ctim.tv_nsec);
		return p.add(12); // 2x unused
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(st_dev);
		p = p.add(8);
		p.setI64(st_ino);
		p = p.add(8);
		p.setI64(st_nlink);
		p = p.add(8);
		p.setI32(st_mode);
		p = p.add(4);
		p.setI32(st_uid);
		p = p.add(4);
		p.setI32(st_gid);
		p = p.add(4);
		p = p.add(4); // int __pad0
		p.setI64(st_rdev);
		p = p.add(8);
		p.setI64(st_size);
		p = p.add(8);
		p.setI64(st_blksize);
		p = p.add(8);
		p.setI64(st_blocks);
		p = p.add(8);
		p.setI64(st_atim.tv_sec);
		p = p.add(8);
		p.setI64(st_atim.tv_nsec);
		p = p.add(8);
		p.setI64(st_mtim.tv_sec);
		p = p.add(8);
		p.setI64(st_mtim.tv_nsec);
		p = p.add(8);
		p.setI64(st_ctim.tv_sec);
		p = p.add(8);
		p.setI64(st_ctim.tv_nsec);
		return p.add(32);
	}

	public PosixPointer write3264(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(st_dev);
		p = p.add(8);
		p.setI64(st_ino);
		p = p.add(8);
		p.setI32(st_mode);
		p = p.add(4);
		p.setI32(st_nlink);
		p = p.add(4);
		p.setI32(st_uid);
		p = p.add(4);
		p.setI32(st_gid);
		p = p.add(4);
		p.setI64(st_rdev);
		p = p.add(8);
		// unsigned long long __pad1
		p = p.add(8);
		p.setI64(st_size);
		p = p.add(8);
		p.setI32(st_blksize);
		p = p.add(4);
		// int __pad2
		p = p.add(4);
		p.setI64(st_blocks);
		p = p.add(8);
		p.setI32((int) st_atim.tv_sec);
		p = p.add(4);
		p.setI32((int) st_atim.tv_nsec);
		p = p.add(4);
		p.setI32((int) st_mtim.tv_sec);
		p = p.add(4);
		p.setI32((int) st_mtim.tv_nsec);
		p = p.add(4);
		p.setI32((int) st_ctim.tv_sec);
		p = p.add(4);
		p.setI32((int) st_ctim.tv_nsec);
		return p.add(12); // 2x unused
	}

	public static String mode(int mode) {
		List<String> flags = new ArrayList<>();
		int fmt = mode & S_IFMT;
		switch(fmt) {
		case S_IFSOCK:
			flags.add("S_IFSOCK");
			break;
		case S_IFLNK:
			flags.add("S_IFLNK");
			break;
		case S_IFREG:
			flags.add("S_IFREG");
			break;
		case S_IFBLK:
			flags.add("S_IFBLK");
			break;
		case S_IFDIR:
			flags.add("S_IFDIR");
			break;
		case S_IFCHR:
			flags.add("S_IFCHR");
			break;
		case S_IFIFO:
			flags.add("S_IFIFO");
			break;
		case 0:
			break;
		default:
			flags.add(String.format("0x%x", fmt));
		}

		if(BitTest.test(mode, S_ISUID)) {
			flags.add("S_ISUID");
		}
		if(BitTest.test(mode, S_ISGID)) {
			flags.add("S_ISGID");
		}
		if(BitTest.test(mode, S_ISVTX)) {
			flags.add("S_ISVTX");
		}

		if(BitTest.test(mode, S_IRWXU)) {
			flags.add("S_IRWXU");
		} else {
			if(BitTest.test(mode, S_IRUSR)) {
				flags.add("S_IRUSR");
			}
			if(BitTest.test(mode, S_IWUSR)) {
				flags.add("S_IWUSR");
			}
			if(BitTest.test(mode, S_IXUSR)) {
				flags.add("S_IXUSR");
			}
		}

		if(BitTest.test(mode, S_IRWXG)) {
			flags.add("S_IRWXG");
		} else {
			if(BitTest.test(mode, S_IRGRP)) {
				flags.add("S_IRGRP");
			}
			if(BitTest.test(mode, S_IWGRP)) {
				flags.add("S_IWGRP");
			}
			if(BitTest.test(mode, S_IXGRP)) {
				flags.add("S_IXGRP");
			}
		}

		if(BitTest.test(mode, S_IRWXO)) {
			flags.add("S_IRWXO");
		} else {
			if(BitTest.test(mode, S_IROTH)) {
				flags.add("S_IROTH");
			}
			if(BitTest.test(mode, S_IWOTH)) {
				flags.add("S_IWOTH");
			}
			if(BitTest.test(mode, S_IXOTH)) {
				flags.add("S_IXOTH");
			}
		}

		if(flags.size() == 0) {
			return "0";
		} else {
			return flags.stream().collect(Collectors.joining("|"));
		}
	}
}
