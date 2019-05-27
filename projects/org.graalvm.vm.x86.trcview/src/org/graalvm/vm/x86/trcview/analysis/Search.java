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
package org.graalvm.vm.x86.trcview.analysis;

import org.graalvm.vm.x86.node.debug.trace.StepRecord;
import org.graalvm.vm.x86.trcview.io.BlockNode;
import org.graalvm.vm.x86.trcview.io.Node;
import org.graalvm.vm.x86.trcview.io.RecordNode;

public class Search {
    public static Node next(Node node) {
        if (node instanceof BlockNode) {
            BlockNode block = (BlockNode) node;
            return block.getFirstNode();
        } else if (node instanceof RecordNode && ((RecordNode) node).getRecord() instanceof StepRecord) {
            BlockNode block = node.getParent();
            boolean start = false;
            for (Node n : block.getNodes()) {
                if (n == node) {
                    start = true;
                } else if (start && (n instanceof BlockNode || ((RecordNode) n).getRecord() instanceof StepRecord)) {
                    return n;
                }
            }
            return null;
        } else {
            throw new IllegalArgumentException("Not a BlockNode/RecordNode");
        }
    }

    public static Node nextPC(Node startNode, long pc) {
        Node start = next(startNode);
        if (start == null) {
            return null;
        }
        Node c = nextPCChildren(start, pc);
        if (c == null) {
            // follow parents until reaching root
            c = start;
            BlockNode block = c.getParent();
            while (block != null) {
                boolean started = false;
                for (Node n : block.getNodes()) {
                    if (n == c) {
                        started = true;
                    } else if (started && (n instanceof BlockNode || (n instanceof RecordNode && ((RecordNode) n).getRecord() instanceof StepRecord))) {
                        Node ret = nextPCChildren(n, pc);
                        if (ret != null) {
                            return ret;
                        }
                        break;
                    }
                }
                c = block;
                block = c.getParent();
            }
            return null;
        } else {
            return c;
        }
    }

    private static Node nextPCChildren(Node start, long pc) {
        if (start instanceof BlockNode) {
            BlockNode block = (BlockNode) start;
            if (block.getHead().getLocation().getPC() == pc) {
                return block;
            }
            for (Node n : block.getNodes()) {
                if (n instanceof RecordNode && ((RecordNode) n).getRecord() instanceof StepRecord) {
                    StepRecord step = (StepRecord) ((RecordNode) n).getRecord();
                    if (step.getLocation().getPC() == pc) {
                        return n;
                    }
                } else if (n instanceof BlockNode) {
                    Node next = nextPCChildren(n, pc);
                    if (next != null) {
                        return next;
                    }
                }
            }
            return null;
        } else if (start instanceof RecordNode && ((RecordNode) start).getRecord() instanceof StepRecord) {
            if (((StepRecord) ((RecordNode) start).getRecord()).getLocation().getPC() == pc) {
                return start;
            }
            BlockNode parent = start.getParent();
            boolean started = false;
            for (Node n : parent.getNodes()) {
                if (started) {
                    if (n instanceof RecordNode && ((RecordNode) n).getRecord() instanceof StepRecord) {
                        StepRecord step = (StepRecord) ((RecordNode) n).getRecord();
                        if (step.getLocation().getPC() == pc) {
                            return n;
                        }
                    } else if (n instanceof BlockNode) {
                        Node next = nextPCChildren(n, pc);
                        if (next != null) {
                            return next;
                        }
                    }
                } else if (n == start) {
                    started = true;
                }
            }
            return null;
        } else {
            if (start instanceof RecordNode) {
                throw new IllegalArgumentException("invalid start node type: " + start.getClass().getCanonicalName() + " [" + ((RecordNode) start).getRecord().getClass() + "]");
            } else {
                throw new IllegalArgumentException("invalid start node type: " + start.getClass().getCanonicalName());
            }
        }
    }
}
