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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.graalvm.vm.posix.api.Errno;
import org.graalvm.vm.posix.api.PosixException;
import org.graalvm.vm.posix.api.io.Stream;
import org.graalvm.vm.util.BitTest;
import org.graalvm.vm.util.log.Trace;

public class Socket {
    private static final Logger log = Trace.create(Socket.class);

    // @formatter:off
    // For setsockopt(2)
    public static final int SOL_SOCKET      = 1;

    public static final int SO_DEBUG        = 1;
    public static final int SO_REUSEADDR    = 2;
    public static final int SO_TYPE         = 3;
    public static final int SO_ERROR        = 4;
    public static final int SO_DONTROUTE    = 5;
    public static final int SO_BROADCAST    = 6;
    public static final int SO_SNDBUF       = 7;
    public static final int SO_RCVBUF       = 8;
    public static final int SO_SNDBUFFORCE  = 32;
    public static final int SO_RCVBUFFORCE  = 33;
    public static final int SO_KEEPALIVE    = 9;
    public static final int SO_OOBINLINE    = 10;
    public static final int SO_NO_CHECK     = 11;
    public static final int SO_PRIORITY     = 12;
    public static final int SO_LINGER       = 13;
    public static final int SO_BSDCOMPAT    = 14;
    public static final int SO_REUSEPORT    = 15;
    public static final int SO_RCVLOWAT     = 16;
    public static final int SO_SNDLOWAT     = 17;
    public static final int SO_RCVTIMEO     = 18;
    public static final int SO_SNDTIMEO     = 19;
    public static final int SO_PASSCRED     = 20;
    public static final int SO_PEERCRED     = 21;

    // Security levels - as per NRL IPv6 - don't actually do anything
    public static final int SO_SECURITY_AUTHENTICATION              = 22;
    public static final int SO_SECURITY_ENCRYPTION_TRANSPORT        = 23;
    public static final int SO_SECURITY_ENCRYPTION_NETWORK          = 24;
    public static final int SO_BINDTODEVICE = 25;

    // Socket filtering
    public static final int SO_ATTACH_FILTER        = 26;
    public static final int SO_DETACH_FILTER        = 27;
    public static final int SO_GET_FILTER           = SO_ATTACH_FILTER;
    public static final int SO_PEERNAME             = 28;
    public static final int SO_TIMESTAMP            = 29;
    public static final int SCM_TIMESTAMP           = SO_TIMESTAMP;
    public static final int SO_ACCEPTCONN           = 30;
    public static final int SO_PEERSEC              = 31;
    public static final int SO_PASSSEC              = 34;
    public static final int SO_TIMESTAMPNS          = 35;
    public static final int SCM_TIMESTAMPNS         = SO_TIMESTAMPNS;
    public static final int SO_MARK                 = 36;
    public static final int SO_TIMESTAMPING         = 37;
    public static final int SCM_TIMESTAMPING        = SO_TIMESTAMPING;
    public static final int SO_PROTOCOL             = 38;
    public static final int SO_DOMAIN               = 39;
    public static final int SO_RXQ_OVFL             = 40;
    public static final int SO_WIFI_STATUS          = 41;
    public static final int SCM_WIFI_STATUS         = SO_WIFI_STATUS;
    public static final int SO_PEEK_OFF             = 42;

    // Instruct lower device to use last 4-bytes of skb data as FCS
    public static final int SO_NOFCS                = 43;
    public static final int SO_LOCK_FILTER          = 44;
    public static final int SO_SELECT_ERR_QUEUE     = 45;
    public static final int SO_BUSY_POLL            = 46;
    public static final int SO_MAX_PACING_RATE      = 47;
    public static final int SO_BPF_EXTENSIONS       = 48;
    public static final int SO_INCOMING_CPU         = 49;
    public static final int SO_ATTACH_BPF           = 50;
    public static final int SO_DETACH_BPF           = SO_DETACH_FILTER;
    public static final int SO_ATTACH_REUSEPORT_CBPF        = 51;
    public static final int SO_ATTACH_REUSEPORT_EBPF        = 52;

    // Protocol families.
    public static final int PF_UNSPEC       = 0;       /* Unspecified.  */
    public static final int PF_LOCAL        = 1;       /* Local to host (pipes and file-domain).  */
    public static final int PF_UNIX         = PF_LOCAL; /* POSIX name for PF_LOCAL.  */
    public static final int PF_FILE         = PF_LOCAL; /* Another non-standard name for PF_LOCAL.  */
    public static final int PF_INET         = 2;       /* IP protocol family.  */
    public static final int PF_AX25         = 3;       /* Amateur Radio AX.25.  */
    public static final int PF_IPX          = 4;       /* Novell Internet Protocol.  */
    public static final int PF_APPLETALK    = 5;       /* Appletalk DDP.  */
    public static final int PF_NETROM       = 6;       /* Amateur radio NetROM.  */
    public static final int PF_BRIDGE       = 7;       /* Multiprotocol bridge.  */
    public static final int PF_ATMPVC       = 8;       /* ATM PVCs.  */
    public static final int PF_X25          = 9;       /* Reserved for X.25 project.  */
    public static final int PF_INET6        = 10;      /* IP version 6.  */
    public static final int PF_ROSE         = 11;      /* Amateur Radio X.25 PLP.  */
    public static final int PF_DECnet       = 12;      /* Reserved for DECnet project.  */
    public static final int PF_NETBEUI      = 13;      /* Reserved for 802.2LLC project.  */
    public static final int PF_SECURITY     = 14;      /* Security callback pseudo AF.  */
    public static final int PF_KEY          = 15;      /* PF_KEY key management API.  */
    public static final int PF_NETLINK      = 16;
    public static final int PF_ROUTE        = PF_NETLINK; /* Alias to emulate 4.4BSD.  */
    public static final int PF_PACKET       = 17;      /* Packet family.  */
    public static final int PF_ASH          = 18;      /* Ash.  */
    public static final int PF_ECONET       = 19;      /* Acorn Econet.  */
    public static final int PF_ATMSVC       = 20;      /* ATM SVCs.  */
    public static final int PF_RDS          = 21;      /* RDS sockets.  */
    public static final int PF_SNA          = 22;      /* Linux SNA Project */
    public static final int PF_IRDA         = 23;      /* IRDA sockets.  */
    public static final int PF_PPPOX        = 24;      /* PPPoX sockets.  */
    public static final int PF_WANPIPE      = 25;      /* Wanpipe API sockets.  */
    public static final int PF_LLC          = 26;      /* Linux LLC.  */
    public static final int PF_IB           = 27;      /* Native InfiniBand address.  */
    public static final int PF_MPLS         = 28;      /* MPLS.  */
    public static final int PF_CAN          = 29;      /* Controller Area Network.  */
    public static final int PF_TIPC         = 30;      /* TIPC sockets.  */
    public static final int PF_BLUETOOTH    = 31;      /* Bluetooth sockets.  */
    public static final int PF_IUCV         = 32;      /* IUCV sockets.  */
    public static final int PF_RXRPC        = 33;      /* RxRPC sockets.  */
    public static final int PF_ISDN         = 34;      /* mISDN sockets.  */
    public static final int PF_PHONET       = 35;      /* Phonet sockets.  */
    public static final int PF_IEEE802154   = 36;      /* IEEE 802.15.4 sockets.  */
    public static final int PF_CAIF         = 37;      /* CAIF sockets.  */
    public static final int PF_ALG          = 38;      /* Algorithm sockets.  */
    public static final int PF_NFC          = 39;      /* NFC sockets.  */
    public static final int PF_VSOCK        = 40;      /* vSockets.  */
    public static final int PF_KCM          = 41;      /* Kernel Connection Multiplexor.  */
    public static final int PF_QIPCRTR      = 42;      /* Qualcomm IPC Router.  */
    public static final int PF_SMC          = 43;      /* SMC sockets.  */
    public static final int PF_MAX          = 44;      /* For now..  */

    // Address families.
    public static final int AF_UNSPEC       = PF_UNSPEC;
    public static final int AF_LOCAL        = PF_LOCAL;
    public static final int AF_UNIX         = PF_UNIX;
    public static final int AF_FILE         = PF_FILE;
    public static final int AF_INET         = PF_INET;
    public static final int AF_AX25         = PF_AX25;
    public static final int AF_IPX          = PF_IPX;
    public static final int AF_APPLETALK    = PF_APPLETALK;
    public static final int AF_NETROM       = PF_NETROM;
    public static final int AF_BRIDGE       = PF_BRIDGE;
    public static final int AF_ATMPVC       = PF_ATMPVC;
    public static final int AF_X25          = PF_X25;
    public static final int AF_INET6        = PF_INET6;
    public static final int AF_ROSE         = PF_ROSE;
    public static final int AF_DECnet       = PF_DECnet;
    public static final int AF_NETBEUI      = PF_NETBEUI;
    public static final int AF_SECURITY     = PF_SECURITY;
    public static final int AF_KEY          = PF_KEY;
    public static final int AF_NETLINK      = PF_NETLINK;
    public static final int AF_ROUTE        = PF_ROUTE;
    public static final int AF_PACKET       = PF_PACKET;
    public static final int AF_ASH          = PF_ASH;
    public static final int AF_ECONET       = PF_ECONET;
    public static final int AF_ATMSVC       = PF_ATMSVC;
    public static final int AF_RDS          = PF_RDS;
    public static final int AF_SNA          = PF_SNA;
    public static final int AF_IRDA         = PF_IRDA;
    public static final int AF_PPPOX        = PF_PPPOX;
    public static final int AF_WANPIPE      = PF_WANPIPE;
    public static final int AF_LLC          = PF_LLC;
    public static final int AF_IB           = PF_IB;
    public static final int AF_MPLS         = PF_MPLS;
    public static final int AF_CAN          = PF_CAN;
    public static final int AF_TIPC         = PF_TIPC;
    public static final int AF_BLUETOOTH    = PF_BLUETOOTH;
    public static final int AF_IUCV         = PF_IUCV;
    public static final int AF_RXRPC        = PF_RXRPC;
    public static final int AF_ISDN         = PF_ISDN;
    public static final int AF_PHONET       = PF_PHONET;
    public static final int AF_IEEE802154   = PF_IEEE802154;
    public static final int AF_CAIF         = PF_CAIF;
    public static final int AF_ALG          = PF_ALG;
    public static final int AF_NFC          = PF_NFC;
    public static final int AF_VSOCK        = PF_VSOCK;
    public static final int AF_KCM          = PF_KCM;
    public static final int AF_QIPCRTR      = PF_QIPCRTR;
    public static final int AF_SMC          = PF_SMC;
    public static final int AF_MAX          = PF_MAX;

    public static final int SOCK_STREAM = 1;    // Sequenced, reliable, connection-based byte streams.
    public static final int SOCK_DGRAM = 2;     // Connectionless, unreliable datagrams of fixed maximum length.
    public static final int SOCK_RAW = 3;       // Raw protocol interface.
    public static final int SOCK_RDM = 4;       // Reliably-delivered messages.
    public static final int SOCK_SEQPACKET = 5; // Sequenced, reliable, connection-based, datagrams of fixed maximum length.
    public static final int SOCK_DCCP = 6;      // Datagram Congestion Control Protocol.
    public static final int SOCK_PACKET = 10;   // Linux specific way of getting packets at the dev level. For writing rarp
                                                // and other similar things on the user level.

    // Flags to be ORed into the type parameter of socket and socketpair and used for the flags parameter of paccept.

    public static final int SOCK_CLOEXEC = 02000000;  // Atomically set close-on-exec flag for the new descriptor(s).
    public static final int SOCK_NONBLOCK = 00004000; // Atomically mark descriptor(s) as non-blocking.

    public static final int MSG_OOB             = 0x01; /* Process out-of-band data.  */
    public static final int MSG_PEEK            = 0x02; /* Peek at incoming messages.  */
    public static final int MSG_DONTROUTE       = 0x04; /* Don't use local routing.  */
        /* DECnet uses a different name.  */
    public static final int MSG_TRYHARD         = MSG_DONTROUTE;
    public static final int MSG_CTRUNC          = 0x08; /* Control data lost before delivery.  */
    public static final int MSG_PROXY           = 0x10; /* Supply or ask second address.  */
    public static final int MSG_TRUNC           = 0x20;
    public static final int MSG_DONTWAIT        = 0x40; /* Nonblocking IO.  */
    public static final int MSG_EOR             = 0x80; /* End of record.  */
    public static final int MSG_WAITALL         = 0x100; /* Wait for a full request.  */
    public static final int MSG_FIN             = 0x200;
    public static final int MSG_SYN             = 0x400;
    public static final int MSG_CONFIRM         = 0x800; /* Confirm path validity.  */
    public static final int MSG_RST             = 0x1000;
    public static final int MSG_ERRQUEUE        = 0x2000; /* Fetch message from error queue.  */
    public static final int MSG_NOSIGNAL        = 0x4000; /* Do not generate SIGPIPE.  */
    public static final int MSG_MORE            = 0x8000;  /* Sender will send more.  */
    public static final int MSG_WAITFORONE      = 0x10000; /* Wait for at least one packet to return.*/
    public static final int MSG_BATCH           = 0x40000; /* sendmmsg: more messages coming.  */
    public static final int MSG_FASTOPEN        = 0x20000000; /* Send data in TCP SYN.  */
    public static final int MSG_CMSG_CLOEXEC    = 0x40000000; /* Set close_on_exit for file descriptor received through SCM_RIGHTS.  */

    public static final int SHUT_RD = 0;          /* No more receptions.  */
    public static final int SHUT_WR = 1;          /* No more transmissions.  */
    public static final int SHUT_RDWR = 2;        /* No more receptions or transmissions.  */
    // @formatter:on

    private static final int SOCK_MASK = 0xFF;

    private static Map<Integer, String> pf;
    private static Map<Integer, String> af;
    private static Map<Integer, String> sock;
    private static Map<Integer, String> sockoptLevel;
    private static Map<Integer, String> sockoptSoOption;

    static {
        pf = new HashMap<>();
        pf.put(PF_UNSPEC, "PF_UNSPEC");
        // pf.put(PF_LOCAL, "PF_LOCAL");
        pf.put(PF_UNIX, "PF_UNIX");
        // pf.put(PF_FILE, "PF_FILE");
        pf.put(PF_INET, "PF_INET");
        pf.put(PF_AX25, "PF_AX25");
        pf.put(PF_IPX, "PF_IPX");
        pf.put(PF_APPLETALK, "PF_APPLETALK");
        pf.put(PF_NETROM, "PF_NETROM");
        pf.put(PF_BRIDGE, "PF_BRIDGE");
        pf.put(PF_ATMPVC, "PF_ATMPVC");
        pf.put(PF_X25, "PF_X25");
        pf.put(PF_INET6, "PF_INET6");
        pf.put(PF_ROSE, "PF_ROSE");
        pf.put(PF_DECnet, "PF_DECnet");
        pf.put(PF_NETBEUI, "PF_NETBEUI");
        pf.put(PF_SECURITY, "PF_SECURITY");
        pf.put(PF_KEY, "PF_KEY");
        pf.put(PF_NETLINK, "PF_NETLINK");
        pf.put(PF_ROUTE, "PF_ROUTE");
        pf.put(PF_PACKET, "PF_PACKET");
        pf.put(PF_ASH, "PF_ASH");
        pf.put(PF_ECONET, "PF_ECONET");
        pf.put(PF_ATMSVC, "PF_ATMSVC");
        pf.put(PF_RDS, "PF_RDS");
        pf.put(PF_SNA, "PF_SNA");
        pf.put(PF_IRDA, "PF_IRDA");
        pf.put(PF_PPPOX, "PF_PPPOX");
        pf.put(PF_WANPIPE, "PF_WANPIPE");
        pf.put(PF_LLC, "PF_LLC");
        pf.put(PF_IB, "PF_IB");
        pf.put(PF_MPLS, "PF_MPLS");
        pf.put(PF_CAN, "PF_CAN");
        pf.put(PF_TIPC, "PF_TIPC");
        pf.put(PF_BLUETOOTH, "PF_BLUETOOTH");
        pf.put(PF_IUCV, "PF_IUCV");
        pf.put(PF_RXRPC, "PF_RXRPC");
        pf.put(PF_ISDN, "PF_ISDN");
        pf.put(PF_PHONET, "PF_PHONET");
        pf.put(PF_IEEE802154, "PF_IEEE802154");
        pf.put(PF_CAIF, "PF_CAIF");
        pf.put(PF_ALG, "PF_ALG");
        pf.put(PF_NFC, "PF_NFC");
        pf.put(PF_VSOCK, "PF_VSOCK");
        pf.put(PF_KCM, "PF_KCM");
        pf.put(PF_QIPCRTR, "PF_QIPCRTR");
        pf.put(PF_SMC, "PF_SMC");

        af = new HashMap<>();
        af.put(AF_UNSPEC, "AF_UNSPEC");
        // af.put(AF_LOCAL, "AF_LOCAL");
        af.put(AF_UNIX, "AF_UNIX");
        // af.put(AF_FILE, "AF_FILE");
        af.put(AF_INET, "AF_INET");
        af.put(AF_AX25, "AF_AX25");
        af.put(AF_IPX, "AF_IPX");
        af.put(AF_APPLETALK, "AF_APPLETALK");
        af.put(AF_NETROM, "AF_NETROM");
        af.put(AF_BRIDGE, "AF_BRIDGE");
        af.put(AF_ATMPVC, "AF_ATMPVC");
        af.put(AF_X25, "AF_X25");
        af.put(AF_INET6, "AF_INET6");
        af.put(AF_ROSE, "AF_ROSE");
        af.put(AF_DECnet, "AF_DECnet");
        af.put(AF_NETBEUI, "AF_NETBEUI");
        af.put(AF_SECURITY, "AF_SECURITY");
        af.put(AF_KEY, "AF_KEY");
        af.put(AF_NETLINK, "AF_NETLINK");
        af.put(AF_ROUTE, "AF_ROUTE");
        af.put(AF_PACKET, "AF_PACKET");
        af.put(AF_ASH, "AF_ASH");
        af.put(AF_ECONET, "AF_ECONET");
        af.put(AF_ATMSVC, "AF_ATMSVC");
        af.put(AF_RDS, "AF_RDS");
        af.put(AF_SNA, "AF_SNA");
        af.put(AF_IRDA, "AF_IRDA");
        af.put(AF_PPPOX, "AF_PPPOX");
        af.put(AF_WANPIPE, "AF_WANPIPE");
        af.put(AF_LLC, "AF_LLC");
        af.put(AF_IB, "AF_IB");
        af.put(AF_MPLS, "AF_MPLS");
        af.put(AF_CAN, "AF_CAN");
        af.put(AF_TIPC, "AF_TIPC");
        af.put(AF_BLUETOOTH, "AF_BLUETOOTH");
        af.put(AF_IUCV, "AF_IUCV");
        af.put(AF_RXRPC, "AF_RXRPC");
        af.put(AF_ISDN, "AF_ISDN");
        af.put(AF_PHONET, "AF_PHONET");
        af.put(AF_IEEE802154, "AF_IEEE802154");
        af.put(AF_CAIF, "AF_CAIF");
        af.put(AF_ALG, "AF_ALG");
        af.put(AF_NFC, "AF_NFC");
        af.put(AF_VSOCK, "AF_VSOCK");
        af.put(AF_KCM, "AF_KCM");
        af.put(AF_QIPCRTR, "AF_QIPCRTR");
        af.put(AF_SMC, "AF_SMC");

        sock = new HashMap<>();
        sock.put(SOCK_STREAM, "SOCK_STREAM");
        sock.put(SOCK_DGRAM, "SOCK_DGRAM");
        sock.put(SOCK_RAW, "SOCK_RAW");
        sock.put(SOCK_RDM, "SOCK_RDM");
        sock.put(SOCK_SEQPACKET, "SOCK_SEQPACKET");
        sock.put(SOCK_DCCP, "SOCK_DCCP");
        sock.put(SOCK_PACKET, "SOCK_PACKET");

        sockoptLevel = new HashMap<>();
        sockoptLevel.put(SOL_SOCKET, "SOL_SOCKET");
        sockoptLevel.put(Tcp.SOL_TCP, "SOL_TCP");

        sockoptSoOption = new HashMap<>();
        sockoptSoOption.put(SO_DEBUG, "SO_DEBUG");
        sockoptSoOption.put(SO_REUSEADDR, "SO_REUSEADDR");
        sockoptSoOption.put(SO_TYPE, "SO_TYPE");
        sockoptSoOption.put(SO_ERROR, "SO_ERROR");
        sockoptSoOption.put(SO_DONTROUTE, "SO_DONTROUTE");
        sockoptSoOption.put(SO_BROADCAST, "SO_BROADCAST");
        sockoptSoOption.put(SO_SNDBUF, "SO_SNDBUF");
        sockoptSoOption.put(SO_RCVBUF, "SO_RCVBUF");
        sockoptSoOption.put(SO_SNDBUFFORCE, "SO_SNDBUFFORCE");
        sockoptSoOption.put(SO_RCVBUFFORCE, "SO_RCVBUFFORCE");
        sockoptSoOption.put(SO_KEEPALIVE, "SO_KEEPALIVE");
        sockoptSoOption.put(SO_OOBINLINE, "SO_OOBINLINE");
        sockoptSoOption.put(SO_NO_CHECK, "SO_NO_CHECK");
        sockoptSoOption.put(SO_PRIORITY, "SO_PRIORITY");
        sockoptSoOption.put(SO_LINGER, "SO_LINGER");
        sockoptSoOption.put(SO_BSDCOMPAT, "SO_BSDCOMPAT");
        sockoptSoOption.put(SO_REUSEPORT, "SO_REUSEPORT");
        sockoptSoOption.put(SO_RCVLOWAT, "SO_RCVLOWAT");
        sockoptSoOption.put(SO_SNDLOWAT, "SO_SNDLOWAT");
        sockoptSoOption.put(SO_RCVTIMEO, "SO_RCVTIMEO");
        sockoptSoOption.put(SO_SNDTIMEO, "SO_SNDTIMEO");
        sockoptSoOption.put(SO_PASSCRED, "SO_PASSCRED");
        sockoptSoOption.put(SO_PEERCRED, "SO_PEERCRED");
        sockoptSoOption.put(SO_SECURITY_AUTHENTICATION, "SO_SECURITY_AUTHENTICATION");
        sockoptSoOption.put(SO_SECURITY_ENCRYPTION_TRANSPORT, "SO_SECURITY_ENCRYPTION_TRANSPORT");
        sockoptSoOption.put(SO_SECURITY_ENCRYPTION_NETWORK, "SO_SECURITY_ENCRYPTION_NETWORK");
        sockoptSoOption.put(SO_BINDTODEVICE, "SO_BINDTODEVICE");
        sockoptSoOption.put(SO_ATTACH_FILTER, "SO_ATTACH_FILTER");
        sockoptSoOption.put(SO_DETACH_FILTER, "SO_DETACH_FILTER");
        sockoptSoOption.put(SO_PEERNAME, "SO_PEERNAME");
        sockoptSoOption.put(SO_TIMESTAMP, "SO_TIMESTAMP");
        sockoptSoOption.put(SO_ACCEPTCONN, "SO_ACCEPTCONN");
        sockoptSoOption.put(SO_PEERSEC, "SO_PEERSEC");
        sockoptSoOption.put(SO_PASSSEC, "SO_PASSSEC");
        sockoptSoOption.put(SO_TIMESTAMPNS, "SO_TIMESTAMPNS");
        sockoptSoOption.put(SO_MARK, "SO_MARK");
        sockoptSoOption.put(SO_TIMESTAMPING, "SO_TIMESTAMPING");
        sockoptSoOption.put(SO_PROTOCOL, "SO_PROTOCOL");
        sockoptSoOption.put(SO_DOMAIN, "SO_DOMAIN");
        sockoptSoOption.put(SO_RXQ_OVFL, "SO_RXQ_OVFL");
        sockoptSoOption.put(SO_WIFI_STATUS, "SO_WIFI_STATUS");
        sockoptSoOption.put(SO_PEEK_OFF, "SO_PEEK_OFF");
        sockoptSoOption.put(SO_NOFCS, "SO_NOFCS");
        sockoptSoOption.put(SO_LOCK_FILTER, "SO_LOCK_FILTER");
        sockoptSoOption.put(SO_SELECT_ERR_QUEUE, "SO_SELECT_ERR_QUEUE");
        sockoptSoOption.put(SO_BUSY_POLL, "SO_BUSY_POLL");
        sockoptSoOption.put(SO_MAX_PACING_RATE, "SO_MAX_PACING_RATE");
        sockoptSoOption.put(SO_BPF_EXTENSIONS, "SO_BPF_EXTENSIONS");
        sockoptSoOption.put(SO_INCOMING_CPU, "SO_INCOMING_CPU");
        sockoptSoOption.put(SO_ATTACH_BPF, "SO_ATTACH_BPF");
        sockoptSoOption.put(SO_ATTACH_REUSEPORT_CBPF, "SO_ATTACH_REUSEPORT_CBPF");
        sockoptSoOption.put(SO_ATTACH_REUSEPORT_EBPF, "SO_ATTACH_REUSEPORT_EBPF");
    }

    public Stream socket(int domain, int type, int protocol) throws PosixException {
        NetworkStream socket;
        switch (domain) {
            case AF_INET:
            case AF_INET6:
                switch (type & SOCK_MASK) {
                    case SOCK_STREAM:
                        if (protocol == In.IPPROTO_IP || protocol == In.IPPROTO_TCP) {
                            socket = new StreamSocketStream();
                        } else {
                            throw new PosixException(Errno.EPROTONOSUPPORT);
                        }
                        break;
                    case SOCK_DGRAM:
                        if (protocol == In.IPPROTO_IP || protocol == In.IPPROTO_UDP) {
                            socket = new DatagramSocketStream();
                        } else {
                            throw new PosixException(Errno.EPROTONOSUPPORT);
                        }
                        break;
                    default:
                        throw new PosixException(Errno.EPROTOTYPE);
                }
                break;
            default:
                throw new PosixException(Errno.EAFNOSUPPORT);
        }

        if (BitTest.test(type, SOCK_NONBLOCK)) {
            try {
                socket.getChannel().configureBlocking(true);
            } catch (IOException e) {
                log.log(Level.WARNING, "Cannot set socket to non-blocking: " + e.getMessage(), e);
                socket.close();
                throw new PosixException(Errno.EIO);
            }
        }
        return socket;
    }

    public static String addressFamily(int domain) {
        String result = af.get(domain);
        if (result != null) {
            return result;
        } else {
            return Integer.toString(domain);
        }
    }

    public static String protocolFamily(int protocol) {
        String result = pf.get(protocol);
        if (result != null) {
            return result;
        } else {
            return Integer.toString(protocol);
        }
    }

    public static String protocol(int domain, int protocol) {
        switch (domain) {
            case AF_INET:
                return In.ipproto(protocol);
            default:
                return Integer.toString(protocol);
        }
    }

    public static String type(int type) {
        String result = sock.get(type & SOCK_MASK);
        if (result != null) {
            if ((type & SOCK_MASK) != type) {
                StringBuilder buf = new StringBuilder(result);
                if (BitTest.test(type, SOCK_CLOEXEC)) {
                    buf.append("|SOCK_CLOEXEC");
                }
                if (BitTest.test(type, SOCK_NONBLOCK)) {
                    buf.append("|SOCK_NONBLOCK");
                }
                return buf.toString();
            } else {
                return result;
            }
        } else {
            return Integer.toString(type);
        }
    }

    private static int processFlag(List<String> result, int flags, int flag, String name) {
        if (BitTest.test(flags, flag)) {
            result.add(name);
            return flags - flag;
        } else {
            return flags;
        }
    }

    public static String sendrecvFlags(int flags) {
        List<String> result = new ArrayList<>();
        int fl = flags;
        fl = processFlag(result, fl, MSG_OOB, "MSG_OOB");
        fl = processFlag(result, fl, MSG_PEEK, "MSG_PEEK");
        fl = processFlag(result, fl, MSG_DONTROUTE, "MSG_DONTROUTE");
        fl = processFlag(result, fl, MSG_TRYHARD, "MSG_TRYHARD");
        fl = processFlag(result, fl, MSG_CTRUNC, "MSG_CTRUNC");
        fl = processFlag(result, fl, MSG_PROXY, "MSG_PROXY");
        fl = processFlag(result, fl, MSG_TRUNC, "MSG_TRUNC");
        fl = processFlag(result, fl, MSG_DONTWAIT, "MSG_DONTWAIT");
        fl = processFlag(result, fl, MSG_EOR, "MSG_EOR");
        fl = processFlag(result, fl, MSG_FIN, "MSG_FIN");
        fl = processFlag(result, fl, MSG_SYN, "MSG_SYN");
        fl = processFlag(result, fl, MSG_CONFIRM, "MSG_CONFIRM");
        fl = processFlag(result, fl, MSG_RST, "MSG_RST");
        fl = processFlag(result, fl, MSG_ERRQUEUE, "MSG_ERRQUEUE");
        fl = processFlag(result, fl, MSG_NOSIGNAL, "MSG_NOSIGNAL");
        fl = processFlag(result, fl, MSG_MORE, "MSG_MORE");
        fl = processFlag(result, fl, MSG_WAITFORONE, "MSG_WAITFORONE");
        fl = processFlag(result, fl, MSG_BATCH, "MSG_BATCH");
        fl = processFlag(result, fl, MSG_FASTOPEN, "MSG_FASTOPEN");
        fl = processFlag(result, fl, MSG_CMSG_CLOEXEC, "MSG_CMSG_CLOEXEC");
        if (fl != 0) {
            result.add(String.format("0x%x", fl));
        }
        if (result.isEmpty()) {
            return "0";
        } else {
            return result.stream().collect(Collectors.joining("|"));
        }
    }

    public static String shutdownHow(int how) {
        switch (how) {
            case SHUT_RD:
                return "SHUT_RD";
            case SHUT_WR:
                return "SHUT_WR";
            case SHUT_RDWR:
                return "SHUT_RDWR";
            default:
                return Integer.toString(how);
        }
    }

    public static String sockoptLevel(int level) {
        String result = sockoptLevel.get(level);
        if (result != null) {
            return result;
        } else {
            return Integer.toString(level);
        }
    }

    public static String sockoptOption(int level, int option) {
        String result;
        switch (level) {
            case SOL_SOCKET:
                result = sockoptSoOption.get(option);
                if (result != null) {
                    return result;
                } else {
                    return Integer.toString(option);
                }
            case Tcp.SOL_TCP:
                return Tcp.option(option);
            default:
                return Integer.toString(option);
        }
    }
}