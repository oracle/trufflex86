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

        for (int i = 0; i < 5; i++) {
            for (int f = 0; f < 100; f++) {
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
        long memTotal = Long.parseLong(lines.stream().filter((x) -> x.startsWith("MemTotal:")).findAny().orElse("MemTotal: 0").split(":")[1].trim().split(" ")[0]) * 1024;
        assertEquals(memTotal, sysinfo.totalram);
    }
}
