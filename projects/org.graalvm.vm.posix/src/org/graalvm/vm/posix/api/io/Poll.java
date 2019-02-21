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
package org.graalvm.vm.posix.api.io;

public class Poll {
    // @formatter:off
    /* These are specified by iBCS2 */
    public static final int POLLIN         = 0x0001;
    public static final int POLLPRI        = 0x0002;
    public static final int POLLOUT        = 0x0004;
    public static final int POLLERR        = 0x0008;
    public static final int POLLHUP        = 0x0010;
    public static final int POLLNVAL       = 0x0020;

    /* The rest seem to be more-or-less nonstandard. Check them! */
    public static final int POLLRDNORM     = 0x0040;
    public static final int POLLRDBAND     = 0x0080;
    public static final int POLLWRNORM     = 0x0100;
    public static final int POLLWRBAND     = 0x0200;
    public static final int POLLMSG        = 0x0400;
    public static final int POLLREMOVE     = 0x1000;
    public static final int POLLRDHUP      = 0x2000;

    public static final int POLLFREE       = 0x4000; /* currently only for epoll */

    public static final int POLL_BUSY_LOOP = 0x8000;
    // @formatter:on
}
