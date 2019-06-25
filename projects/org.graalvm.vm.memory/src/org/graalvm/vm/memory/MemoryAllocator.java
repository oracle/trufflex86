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
package org.graalvm.vm.memory;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public class MemoryAllocator {
    private static final boolean debug = true;

    private Block memory;
    private Block free;

    private long usedMemory;

    private final long memoryBase;
    private final long memorySize;

    private static class Block {
        public long base;
        public long size;
        public boolean free;
        public Block prev; // link to previous free block
        public Block next; // link to next free block
        public Block prevBlock; // link to previous block according to memory layout
        public Block nextBlock; // link to next block according to memory layout

        Block(long base, long size) {
            this.base = base;
            this.size = size;
            free = true;
        }

        boolean contains(long address) {
            if (size == 0xffffffffffffffffL) { // avoid overflow
                return Long.compareUnsigned(address, base) >= 0;
            } else {
                return Long.compareUnsigned(address, base) >= 0 && Long.compareUnsigned(address, base + size) < 0;
            }
        }

        @Override
        public String toString() {
            return String.format("Block[0x%016x, 0x%016x, free=%s]", base, size, free);
        }
    }

    public MemoryAllocator(long size) {
        this(0, size);
    }

    public MemoryAllocator(long base, long size) {
        memoryBase = base;
        memorySize = size;
        memory = new Block(base, size);
        free = memory;
        usedMemory = 0;
    }

    private static void check(long addr) {
        if ((addr & ~VirtualMemory.PAGE_MASK) != 0) {
            throw new IllegalArgumentException(String.format("0x%016x is not aligned", addr));
        }
    }

    public synchronized long alloc(long size) {
        check(size);
        Block b = free;
        if (b == null) {
            throw new OutOfMemoryError();
        }
        while (Long.compareUnsigned(b.size, size) < 0) {
            if (b.next == null) {
                throw new OutOfMemoryError();
            }
            b = b.next;
        }
        assert b.free == true;
        check(b);
        if (b.size == size) {
            if (b.prev != null) {
                b.prev.next = b.next;
            } else {
                assert free == b;
                free = b.next;
                check(free);
            }
            if (b.next != null) {
                b.next.prev = b.prev;
            }
            check(b.prev);
            check(b.next);
        } else {
            Block split = new Block(b.base + size, b.size - size);
            split.prevBlock = b;
            split.nextBlock = b.nextBlock;
            b.nextBlock = split;
            if (split.nextBlock != null) {
                split.nextBlock.prevBlock = split;
            }
            split.next = b.next;
            split.prev = b.prev;
            if (split.next != null) {
                split.next.prev = split;
            }
            if (split.prev != null) {
                split.prev.next = split;
            }
            check(split);
            if (free == b) {
                free = split;
            }
        }
        b.size = size;
        b.free = false;
        usedMemory += size;

        if (debug) {
            check();
        }

        return b.base;
    }

    private Block find(long addr) {
        for (Block b = memory; b != null; b = b.nextBlock) {
            assert (b.nextBlock == null) || (b.nextBlock.base == b.base + b.size) : String.format("0x%x vs 0x%x", b.base + b.size, b.nextBlock.base);
            if (b.base == addr || b.contains(addr)) {
                return b;
            }
        }
        assert false : String.format("BUG! 0x%x [%x-%x]", addr, memoryBase, memoryBase + memorySize);
        return null;
    }

    public synchronized long allocat(long addr, long size) {
        if (Long.compareUnsigned(addr, memoryBase) < 0) {
            return addr;
        }
        if (Long.compareUnsigned(addr, memoryBase + memorySize) >= 0) {
            return addr;
        }

        check(addr);
        check(size);
        Block blk = find(addr);
        if (blk == null) {
            throw new OutOfMemoryError();
        }
        if (blk.base == addr && blk.size == size) {
            if (!blk.free) {
                if (debug) {
                    check();
                }

                return addr;
            }

            blk.free = false;
            usedMemory += size;
            if (blk.prev != null) {
                blk.prev.next = blk.next;
            } else {
                assert free == blk;
                free = blk.next;
            }
            if (blk.next != null) {
                blk.next.prev = blk.prev;
            }
            check(blk.prev);
            check(blk.next);

            if (debug) {
                check();
            }

            return addr;
        } else if (blk.base == addr && Long.compareUnsigned(blk.size, size) > 0) {
            if (!blk.free) {
                if (debug) {
                    check();
                }

                return addr;
            }

            blk.free = false;
            usedMemory += size;
            Block split = new Block(blk.base + size, blk.size - size);
            if (blk.prev != null) {
                blk.prev.next = split;
            } else {
                assert free == blk;
                free = split;
            }
            split.prev = blk.prev;
            split.next = blk.next;
            if (split.next != null) {
                split.next.prev = split;
            }
            split.prevBlock = blk;
            split.nextBlock = blk.nextBlock;
            blk.nextBlock = split;
            if (split.nextBlock != null) {
                split.nextBlock.prevBlock = split;
            }
            check(split);
            blk.size = size;

            if (debug) {
                check();
            }

            return addr;
        } else {
            Block start = blk;
            long off = addr - start.base;
            long todo = size;
            if (start.free && off != 0) { // unsigned version of "off > 0"
                start.free = true;
                Block split = new Block(blk.base + off, blk.size - off);
                split.prevBlock = blk;
                split.nextBlock = blk.nextBlock;
                blk.nextBlock = split;
                if (split.nextBlock != null) {
                    split.nextBlock.prevBlock = split;
                }
                split.next = blk.next;
                split.prev = blk;
                blk.next = split;
                if (split.next != null) {
                    split.next.prev = split;
                }
                check(split);
                blk.size = off;
                start = split;
            } else if (off != 0) {
                if (Long.compareUnsigned(blk.size - off, todo) >= 0) {
                    if (debug) {
                        check();
                    }

                    return addr;
                } else {
                    todo -= blk.size - off;
                    blk = blk.nextBlock;
                }
            }
            blk = start;
            assert start.base == addr;
            while (todo != 0) {
                if (blk.free) {
                    if (blk.size == todo) {
                        blk.free = false;
                        usedMemory += blk.size;
                        if (blk.prev != null) {
                            blk.prev.next = blk.next;
                        } else {
                            assert free == blk;
                            free = blk.next;
                        }
                        if (blk.next != null) {
                            blk.next.prev = blk.prev;
                        }
                        check(blk);

                        if (debug) {
                            check();
                        }

                        return addr;
                    }
                    // block is larger than todo
                    if (Long.compareUnsigned(blk.size, todo) > 0) {
                        Block split = new Block(blk.base + todo, blk.size - todo);
                        blk.free = false;
                        blk.size = todo;
                        split.next = blk.next;
                        split.prev = blk.prev;
                        split.nextBlock = blk.nextBlock;
                        split.prevBlock = blk;
                        blk.nextBlock = split;
                        if (split.nextBlock != null) {
                            split.nextBlock.prevBlock = split;
                        }
                        if (blk.prev != null) {
                            blk.prev.next = split;
                        } else {
                            assert free == blk;
                            free = split;
                        }
                        if (split.next != null) {
                            split.next.prev = split;
                        }
                        check(split);
                        usedMemory += todo;

                        if (debug) {
                            check();
                        }

                        return addr;
                    } else { // block is too small
                        todo -= size;
                        blk.free = false;
                        if (blk.prev != null) {
                            blk.prev.next = blk.next;
                        } else {
                            assert free == blk;
                            free = blk.next;
                        }
                        if (blk.next != null) {
                            blk.next.prev = blk.prev;
                        }
                        check(blk);
                        blk = blk.nextBlock;
                        usedMemory += size;
                        continue;
                    }
                }
                blk.free = false;
                if (Long.compareUnsigned(blk.size, todo) >= 0) {

                    if (debug) {
                        check();
                    }

                    return addr;
                } else {
                    todo -= blk.size;
                    blk = blk.nextBlock;
                }
            }

            if (debug) {
                check();
            }

            return addr;
        }
    }

    public synchronized void free(long addr) {
        if (Long.compareUnsigned(addr, memoryBase) < 0) {
            return;
        }
        if (Long.compareUnsigned(addr, memoryBase + memorySize) >= 0) {
            return;
        }

        check(addr);
        Block blk = find(addr);
        assert blk.base == addr;
        assert !blk.free;
        blk.free = true;
        usedMemory -= blk.size;

        // find previous free block
        Block b = blk.prevBlock;
        while (b != null) {
            if (b.free) {
                blk.next = b.next;
                b.next = blk;
                if (blk.next != null) {
                    blk.next.prev = blk;
                }
                blk.prev = b;
                check(b);
                check(blk);

                if (debug) {
                    check();
                }

                return;
            } else {
                b = b.prevBlock;
            }
        }

        // no previous free block
        assert free == null || Long.compareUnsigned(blk.base, free.base) < 0;
        assert blk.free;
        blk.next = free;
        blk.prev = null;
        if (free != null) {
            free.prev = blk;
        }
        free = blk;
        check(free);

        if (debug) {
            check();
        }

        compact(blk);

        if (debug) {
            check();
        }
    }

    public synchronized void free(long addr, long size) {
        if (Long.compareUnsigned(addr, memoryBase) < 0) {
            return;
        }
        if (Long.compareUnsigned(addr, memoryBase + memorySize) >= 0) {
            return;
        }

        // TODO: fix all the bugs
        check(addr);
        check(size);
        long p = addr;
        long remaining = size;
        Block start = null;
        Block lastFree = null;
        for (Block b = memory; remaining > 0 && b != null; b = b.nextBlock) {
            if (b.free) {
                lastFree = b;
            }
            if (b.base == p) {
                start = b;
                if (Long.compareUnsigned(b.size, remaining) > 0) {
                    if (b.free) {
                        if (debug) {
                            check();
                        }
                        return;
                    }

                    Block split = new Block(b.base + remaining, b.size - remaining);
                    split.prevBlock = b;
                    split.nextBlock = b.nextBlock;
                    split.nextBlock.prevBlock = split;
                    split.free = false;
                    b.nextBlock = split;
                    b.size = remaining;
                    b.free = true;
                    if (lastFree != null) {
                        b.next = lastFree.next;
                        b.prev = lastFree;
                        lastFree.next = b;
                        if (b.next != null) {
                            b.next.prev = b;
                        }
                        lastFree = b;
                    } else {
                        b.prev = null;
                        b.next = free;
                        if (free != null) {
                            free.prev = b;
                        }
                        free = b;
                        lastFree = b;
                    }

                    usedMemory -= remaining;

                    check(b);
                    check(split);
                    compact(b);

                    if (debug) {
                        check();
                    }

                    return;
                } else if (b.size == size) {
                    b.free = true;
                    usedMemory -= b.size;
                    for (Block blk = b.prevBlock; blk != null; blk = blk.prevBlock) {
                        if (blk.free) {
                            b.next = blk.next;
                            blk.next = b;
                            b.prev = blk;
                            if (b.next != null) {
                                b.next.prev = b;
                            }
                            if (b.prev != null) {
                                b.prev.next = b;
                            }

                            check(b);
                            check(blk);

                            if (debug) {
                                check();
                            }

                            return;
                        }
                    }

                    // no previous free block
                    assert free == null || Long.compareUnsigned(b.base, free.base) < 0;
                    assert b.free;
                    b.next = free;
                    b.prev = null;
                    if (free != null) {
                        free.prev = b;
                    }
                    free = b;
                    check(free);

                    if (debug) {
                        check();
                    }

                    compact(b);

                    if (debug) {
                        check();
                    }
                    return;
                } else {
                    throw new AssertionError();
                }
            } else if (b.contains(p)) {
                // split block, mark center as free
                start = b;
                Block head = new Block(start.base, p - start.base);
                Block tail = new Block(p, start.size - head.size);
                b = head;
                if (start == memory) {
                    memory = head;
                }
                if (Long.compareUnsigned(tail.size, remaining) > 0) {
                    Block center = new Block(tail.base, remaining);
                    tail = new Block(tail.base + remaining, tail.size - remaining);

                    head.free = false;
                    head.prevBlock = start.prevBlock;
                    if (head.prevBlock != null) {
                        head.prevBlock.nextBlock = head;
                    }
                    head.nextBlock = center;
                    head.nextBlock.prevBlock = head;
                    center.nextBlock = tail;
                    center.nextBlock.prevBlock = center;
                    center.free = true;
                    tail.nextBlock = start.nextBlock;
                    tail.nextBlock.prevBlock = tail;
                    tail.free = false;

                    if (lastFree != null) {
                        center.next = lastFree.next;
                        center.prev = lastFree;
                        lastFree.next = center;
                        if (center.next != null) {
                            center.next.prev = center;
                        }
                        lastFree = center;
                    } else {
                        center.prev = null;
                        center.next = free;
                        if (free != null) {
                            free.prev = center;
                        }
                        free = center;
                        lastFree = center;
                    }

                    usedMemory -= remaining;

                    check(free);

                    if (debug) {
                        check();
                    }

                    return;
                } else if (tail.size == remaining) { // mark tail as free
                    head.free = false;
                    head.prevBlock = start.prevBlock;
                    if (head.prevBlock != null) {
                        head.prevBlock.nextBlock = head;
                    }
                    head.nextBlock = tail;
                    head.nextBlock.prevBlock = head;
                    tail.nextBlock = start.nextBlock;
                    tail.nextBlock.prevBlock = tail;
                    tail.free = true;

                    if (lastFree != null) {
                        tail.next = lastFree.next;
                        tail.prev = lastFree;
                        lastFree.next = tail;
                        if (tail.next != null) {
                            tail.next.prev = tail;
                        }
                        lastFree = tail;
                    } else {
                        tail.prev = null;
                        tail.next = free;
                        if (free != null) {
                            free.prev = tail;
                        }
                        free = tail;
                        lastFree = tail;
                    }

                    usedMemory -= remaining;

                    check(free);

                    if (debug) {
                        check();
                    }

                    compact(tail);

                    if (debug) {
                        check();
                    }

                    return;
                } else {
                    assert false;
                }

                check(free);

                if (debug) {
                    check();
                }

                throw new AssertionError();
            }
        }
        // TODO: unreachable?
        assert false : String.format("free(0x%x, %d)", addr, size);
        compact(start);
        usedMemory -= size - remaining;

        if (debug) {
            check();
        }
    }

    private void compact(Block block) {
        Block b = block;
        assert b.free;
        while (b.prevBlock != null && b.prevBlock.free) {
            b = b.prevBlock;
        }
        assert b.prev != null || free == b;
        assert b.free;
        while (b.nextBlock != null && b.nextBlock.free) {
            Block blk = b.nextBlock;
            b.size += blk.size;
            b.nextBlock = blk.nextBlock;
            if (b.nextBlock != null) {
                b.nextBlock.prevBlock = b;
            }
            b.next = blk.next;
            if (b.next != null) {
                b.next.prev = b;
            }
            check(b);
        }
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    @TruffleBoundary
    public synchronized String dump() {
        StringBuilder buf = new StringBuilder();
        for (Block b = memory; b != null; b = b.nextBlock) {
            buf.append(b).append("\n");
        }
        return buf.toString().trim();
    }

    @TruffleBoundary
    private static void check(Block b) {
        if (b == null) {
            return;
        }
        assert !b.free || b.prev == null || Long.compareUnsigned(b.prev.base, b.base) < 0 : String.format("prev=0x%016x, this=0x%016x", b.prev.base, b.base);
        assert !b.free || b.next == null || Long.compareUnsigned(b.base, b.next.base) < 0 : String.format("this=0x%016x, next=0x%016x", b.base, b.next.base);
        assert !b.free || b.prev == null || b.prev.next == b : String.format("this=0x%016x, this.prev.next=0x%016x", b.base, b.prev.next.base);
        assert !b.free || b.next == null || b.next.prev == b : String.format("this=0x%016x, this.next.prev=0x%016x", b.base, b.next.prev.base);
        assert b.prevBlock == null || Long.compareUnsigned(b.prevBlock.base, b.base) < 0 : String.format("prev=0x%016x, this=0x%016x", b.prevBlock.base, b.base);
        assert b.nextBlock == null || Long.compareUnsigned(b.base, b.nextBlock.base) < 0 : String.format("this=0x%016x, next=0x%016x", b.base, b.nextBlock.base);
        assert b.prevBlock == null || b.prevBlock.nextBlock == b : String.format("this=0x%016x, this.prevBlock.nextBlock=0x%016x", b.base, b.prevBlock.nextBlock.base);
        assert b.nextBlock == null || b.nextBlock.prevBlock == b : String.format("this=0x%016x, this.nextBlock.prevBlock=0x%016x", b.base, b.nextBlock.prevBlock.base);
    }

    private static void __assert(boolean b) {
        if (!b) {
            throw new AssertionError();
        }
    }

    private static void __assert(boolean b, String msg) {
        if (!b) {
            throw new AssertionError(msg);
        }
    }

    // consistency checks which *don't* rely on -ea
    @TruffleBoundary
    public synchronized void check() {
        long usedmem = 0;
        long freemem = 0;
        long nextaddr = memoryBase;
        Block lastblock = null;
        __assert(memory.prevBlock == null);
        __assert(free == null || free.prev == null);
        __assert(free == null || free.free);
        for (Block b = memory; b != null; b = b.nextBlock) {
            __assert(b.base == nextaddr, String.format("0x%016x vs 0x%016x", b.base, nextaddr));
            __assert(b.prevBlock == lastblock);
            __assert(!b.free || b.prev != null || free == b);
            lastblock = b;
            nextaddr += b.size;
            if (b.free) {
                freemem += b.size;
            } else {
                usedmem += b.size;
            }
            check(b);
        }
        long lastaddr = free.base;
        for (Block b = free.next; b != null; b = b.next) {
            check(b);
            __assert(Long.compareUnsigned(b.base, lastaddr) > 0,
                            String.format("last: 0x%016x, cur: 0x%016x", lastaddr, b.base));
            lastaddr = b.base;
        }
        long freeMemory = 0;
        for (Block b = free; b != null; b = b.next) {
            freeMemory += b.size;
        }
        __assert(usedmem == usedMemory, String.format("used memory: 0x%x vs 0x%x", usedmem, usedMemory));
        __assert(freemem == freeMemory, String.format("free memory: 0x%x vs 0x%x", freemem, freeMemory));
        __assert((usedmem + freemem) == memorySize, String.format("0x%dx bytes in blocks vs 0x%dx memory region", usedmem + freemem, memorySize));
    }
}
