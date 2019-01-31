package org.graalvm.vm.posix.api.linux;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.PosixPointer;

public class Linux {
	private Futex futex = new Futex();

	class Line {
		public final String name;
		public final long value;

		Line(String line) {
			String[] tmp = line.split(":");
			long scale = 1024;
			name = tmp[0].trim();
			tmp = tmp[1].trim().split(" ");
			value = Long.parseLong(tmp[0].trim()) * scale;
			if(tmp.length > 1 && !tmp[1].trim().equals("kB")) {
				throw new AssertionError("unknown unit");
			}
		}

		String name() {
			return name;
		}

		long value() {
			return value;
		}
	}

	public int sysinfo(Sysinfo info) throws PosixException {
		if(info == null) {
			throw new PosixException(Errno.EFAULT);
		}

		try {
			String uptime = new String(Files.readAllBytes(Paths.get("/proc/uptime")));
			info.uptime = Long.parseUnsignedLong(uptime.substring(0, uptime.indexOf('.')));
		} catch(IOException e) {
			info.uptime = ManagementFactory.getRuntimeMXBean().getUptime();
		}

		info.loads = new long[3];
		String loadavg;
		try {
			loadavg = new String(Files.readAllBytes(Paths.get("/proc/loadavg")));
		} catch(IOException e) {
			loadavg = "0.00 0.00 0.00 0/0 0";
		}

		String[] parts = loadavg.split(" ");
		info.loads[0] = Sysinfo.load(parts[0]);
		info.loads[1] = Sysinfo.load(parts[1]);
		info.loads[2] = Sysinfo.load(parts[2]);

		info.procs = (short) Integer.parseInt(parts[3].split("/")[1]);
		info.totalhigh = 0;
		info.freehigh = 0;
		info.mem_unit = 1;

		Map<String, Long> memory;
		try {
			memory = Files.readAllLines(Paths.get("/proc/meminfo")).stream()
					.map(Line::new)
					.collect(Collectors.toMap(Line::name, Line::value));
		} catch(IOException e) {
			memory = new HashMap<>();
			memory.put("MemTotal", 0L);
			memory.put("MemFree", 0L);
			memory.put("MemAvailable", 0L);
			memory.put("Buffers", 0L);
			memory.put("Cached", 0L);
			memory.put("SwapTotal", 0L);
			memory.put("SwapFree", 0L);
			memory.put("Shmem", 0L);
		}

		info.totalram = memory.get("MemTotal");
		info.freeram = memory.get("MemFree");
		info.sharedram = memory.get("Shmem");
		info.bufferram = memory.get("Buffers");
		info.totalswap = memory.get("SwapTotal");
		info.freeswap = memory.get("SwapFree");

		return 0;
	}

	public int futex(PosixPointer uaddr, int futex_op, int val, PosixPointer timeout, PosixPointer uaddr2, int val3)
			throws PosixException {
		return futex.futex(uaddr, futex_op, val, timeout, uaddr2, val3);
	}
}
