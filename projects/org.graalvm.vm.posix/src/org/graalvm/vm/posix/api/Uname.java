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
package org.graalvm.vm.posix.api;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Uname {
    public String sysname;
    public String nodename;
    public String release;
    public String version;
    public String machine;
    public String domainname;

    public Uname() {
        sysname = System.getProperty("os.name");
        release = System.getProperty("os.version");
        machine = System.getProperty("os.arch");
        if (machine.equals("amd64")) {
            machine = "x86_64";
        }
        if (release == null) {
            release = getRelease("1.0");
        }

        nodename = getHostname("(none)");
        version = getVersion("1.0");
        domainname = getDomainName("localdomain");
    }

    private static String readFile(String name, String fallback) {
        try {
            Path path = Paths.get(name);
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path)).trim();
            }
        } catch (Exception e) {
        }
        return fallback;
    }

    private static String getHostname(String fallback) {
        String hostname = readFile("/proc/sys/kernel/hostname", null);
        if (hostname != null) {
            return hostname;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return fallback;
        }
    }

    private static String getVersion(String fallback) {
        return readFile("/proc/sys/kernel/version", fallback);
    }

    private static String getDomainName(String fallback) {
        return readFile("/proc/sys/kernel/domainname", fallback);
    }

    private static String getRelease(String fallback) {
        return readFile("/proc/sys/kernel/osrelease", fallback);
    }

    public void uname(Utsname buf) {
        buf.sysname = sysname;
        buf.nodename = nodename;
        buf.release = release;
        buf.version = version;
        buf.machine = machine;
        buf.domainname = domainname;
    }
}
