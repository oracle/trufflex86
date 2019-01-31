package org.graalvm.vm.posix.test.api.linux;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.linux.Linux;
import org.graalvm.vm.posix.api.linux.Sysinfo;
import org.junit.Test;

public class SysinfoTest {
	@Test
	public void testLoadFp() {
		assertEquals(0.07, Sysinfo.fp(4800), 0.005);
		assertEquals(0.08, Sysinfo.fp(5472), 0.005);
		assertEquals(0.06, Sysinfo.fp(4192), 0.005);
	}

	@Test
	public void testLoadCompose() {
		assertEquals(0.09, Sysinfo.fp(Sysinfo.load(0, 9)), 0.005);
		assertEquals(1.23, Sysinfo.fp(Sysinfo.load(1, 23)), 0.005);
	}

	@Test
	public void testLoadDecompose() {
		long avenrun = Sysinfo.get_avenrun(4800, 0, Sysinfo.SI_LOAD_SHIFT - Sysinfo.FSHIFT);
		long avnrun = avenrun + (Sysinfo.FIXED_1 / 200);
		assertEquals(0, Sysinfo.LOAD_INT(avnrun));
		assertEquals(7, Sysinfo.LOAD_FRAC(avnrun));

		long alt = 0;
		assertEquals(0, Sysinfo.LOAD_INT(alt));
		assertEquals(0, Sysinfo.LOAD_FRAC(alt));

		alt = 7 << Sysinfo.FSHIFT;
		assertEquals(7, Sysinfo.LOAD_INT(alt));
		assertEquals(0, Sysinfo.LOAD_FRAC(alt));

		alt = (2 << Sysinfo.FSHIFT) / 100;
		assertEquals(0, Sysinfo.LOAD_INT(alt));
		assertEquals(1, Sysinfo.LOAD_FRAC(alt));
	}

	@Test
	public void testLoad() {
		assertEquals(1, Sysinfo.LOAD_INT(Sysinfo.avenrun(Sysinfo.load("1.95"))));
		assertEquals(95, Sysinfo.LOAD_FRAC(Sysinfo.avenrun(Sysinfo.load("1.95"))));

		assertEquals(1, Sysinfo.LOAD_INT(Sysinfo.avenrun(Sysinfo.load("1.98"))));
		assertEquals(98, Sysinfo.LOAD_FRAC(Sysinfo.avenrun(Sysinfo.load("1.98"))));

		for(int i = 0; i < 5; i++) {
			for(int f = 0; f < 100; f++) {
				long load = Sysinfo.load(String.format("%d.%02d", i, f));
				double ref = Double.parseDouble(String.format("%d.%02d", i, f));
				assertEquals(ref, Sysinfo.fp(load), 0.006);
			}
		}
	}

	@Test
	public void testSysinfo() throws PosixException, IOException {
		Sysinfo sysinfo = new Sysinfo();
		assertEquals(0, new Linux().sysinfo(sysinfo));

		List<String> lines = Files.readAllLines(Paths.get("/proc/meminfo"));
		long memTotal = Long.parseLong(lines.stream().filter((x) -> x.startsWith("MemTotal:")).findAny()
				.orElse("MemTotal: 0").split(":")[1].trim().split(" ")[0]) * 1024;
		assertEquals(memTotal, sysinfo.totalram);
	}
}
