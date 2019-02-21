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
package org.graalvm.vm.posix.api.net;

import java.util.HashMap;
import java.util.Map;

public class Tcp {
    // @formatter:off
    public static final int SOL_TCP                  = 6;  /* TCP level */

    public static final int TCP_NODELAY              = 1;  /* Don't delay send to coalesce packets  */
    public static final int TCP_MAXSEG               = 2;  /* Set maximum segment size  */
    public static final int TCP_CORK                 = 3;  /* Control sending of partial frames  */
    public static final int TCP_KEEPIDLE             = 4;  /* Start keeplives after this period */
    public static final int TCP_KEEPINTVL            = 5;  /* Interval between keepalives */
    public static final int TCP_KEEPCNT              = 6;  /* Number of keepalives before death */
    public static final int TCP_SYNCNT               = 7;  /* Number of SYN retransmits */
    public static final int TCP_LINGER2              = 8;  /* Life time of orphaned FIN-WAIT-2 state */
    public static final int TCP_DEFER_ACCEPT         = 9;  /* Wake up listener only when data arrive */
    public static final int TCP_WINDOW_CLAMP         = 10; /* Bound advertised window */
    public static final int TCP_INFO                 = 11; /* Information about this connection. */
    public static final int TCP_QUICKACK             = 12; /* Bock/reenable quick ACKs.  */
    public static final int TCP_CONGESTION           = 13; /* Congestion control algorithm.  */
    public static final int TCP_MD5SIG               = 14; /* TCP MD5 Signature (RFC2385) */
    public static final int TCP_COOKIE_TRANSACTIONS  = 15; /* TCP Cookie Transactions */
    public static final int TCP_THIN_LINEAR_TIMEOUTS = 16; /* Use linear timeouts for thin streams*/
    public static final int TCP_THIN_DUPACK          = 17; /* Fast retrans. after 1 dupack */
    public static final int TCP_USER_TIMEOUT         = 18; /* How long for loss retry before timeout */
    public static final int TCP_REPAIR               = 19; /* TCP sock is under repair right now */
    public static final int TCP_REPAIR_QUEUE         = 20; /* Set TCP queue to repair */
    public static final int TCP_QUEUE_SEQ            = 21; /* Set sequence number of repaired queue. */
    public static final int TCP_REPAIR_OPTIONS       = 22; /* Repair TCP connection options */
    public static final int TCP_FASTOPEN             = 23; /* Enable FastOpen on listeners */
    public static final int TCP_TIMESTAMP            = 24; /* TCP time stamp */
    public static final int TCP_NOTSENT_LOWAT        = 25; /* Limit number of unsent bytes in write queue.  */
    public static final int TCP_CC_INFO              = 26; /* Get Congestion Control (optional) info.  */
    public static final int TCP_SAVE_SYN             = 27; /* Record SYN headers for new connections.  */
    public static final int TCP_SAVED_SYN            = 28; /* Get SYN headers recorded for connection.  */
    public static final int TCP_REPAIR_WINDOW        = 29; /* Get/set window parameters.  */
    public static final int TCP_FASTOPEN_CONNECT     = 30; /* Attempt FastOpen with connect.  */
    // @formatter:on

    private static final Map<Integer, String> options;

    static {
        options = new HashMap<>();
        options.put(TCP_NODELAY, "TCP_NODELAY");
        options.put(TCP_MAXSEG, "TCP_MAXSEG");
        options.put(TCP_CORK, "TCP_CORK");
        options.put(TCP_KEEPIDLE, "TCP_KEEPIDLE");
        options.put(TCP_KEEPINTVL, "TCP_KEEPINTVL");
        options.put(TCP_KEEPCNT, "TCP_KEEPCNT");
        options.put(TCP_SYNCNT, "TCP_SYNCNT");
        options.put(TCP_LINGER2, "TCP_LINGER2");
        options.put(TCP_DEFER_ACCEPT, "TCP_DEFER_ACCEPT");
        options.put(TCP_WINDOW_CLAMP, "TCP_WINDOW_CLAMP");
        options.put(TCP_INFO, "TCP_INFO");
        options.put(TCP_QUICKACK, "TCP_QUICKACK");
        options.put(TCP_CONGESTION, "TCP_CONGESTION");
        options.put(TCP_MD5SIG, "TCP_MD5SIG");
        options.put(TCP_COOKIE_TRANSACTIONS, "TCP_COOKIE_TRANSACTIONS");
        options.put(TCP_THIN_LINEAR_TIMEOUTS, "TCP_THIN_LINEAR_TIMEOUTS");
        options.put(TCP_THIN_DUPACK, "TCP_THIN_DUPACK");
        options.put(TCP_USER_TIMEOUT, "TCP_USER_TIMEOUT");
        options.put(TCP_REPAIR, "TCP_REPAIR");
        options.put(TCP_REPAIR_QUEUE, "TCP_REPAIR_QUEUE");
        options.put(TCP_QUEUE_SEQ, "TCP_QUEUE_SEQ");
        options.put(TCP_REPAIR_OPTIONS, "TCP_REPAIR_OPTIONS");
        options.put(TCP_FASTOPEN, "TCP_FASTOPEN");
        options.put(TCP_TIMESTAMP, "TCP_TIMESTAMP");
        options.put(TCP_NOTSENT_LOWAT, "TCP_NOTSENT_LOWAT");
        options.put(TCP_CC_INFO, "TCP_CC_INFO");
        options.put(TCP_SAVE_SYN, "TCP_SAVE_SYN");
        options.put(TCP_SAVED_SYN, "TCP_SAVED_SYN");
        options.put(TCP_REPAIR_WINDOW, "TCP_REPAIR_WINDOW");
        options.put(TCP_FASTOPEN_CONNECT, "TCP_FASTOPEN_CONNECT");
    }

    public static String option(int option) {
        String result = options.get(option);
        if (result != null) {
            return result;
        } else {
            return Integer.toString(option);
        }
    }
}
