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
package org.graalvm.vm.x86.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.graalvm.vm.posix.api.ProcessExitException;
import org.graalvm.vm.x86.AMD64Register;
import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.RegisterAccessFactory;
import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.isa.Register;
import org.graalvm.vm.x86.isa.RegisterOperand;
import org.graalvm.vm.x86.node.RegisterWriteNode;
import org.graalvm.vm.x86.substitution.Signature;
import org.graalvm.vm.x86.substitution.Substitution;
import org.graalvm.vm.x86.substitution.SubstitutionRegistry;
import org.junit.Test;

import com.oracle.truffle.api.frame.VirtualFrame;

public class SubstitutionTest {
    private static final byte[] ASM_STRCMP_IFUNC = {(byte) 0xf3, 0x0f, 0x1e, (byte) 0xfa, 0x48, (byte) 0x8b, 0x0d, (byte) 0x8d, 0x4b, 0x13, 0x00, 0x48, (byte) 0x8d, 0x05, 0x5e, 0x19, 0x0d, 0x00,
                    (byte) 0x8b, (byte) 0x91, (byte) 0xb4, 0x00, 0x00, 0x00, (byte) 0x89, (byte) 0xd6, (byte) 0x81, (byte) 0xe6, 0x00, 0x0c, 0x02, 0x00, (byte) 0x81, (byte) 0xfe, 0x00, 0x0c, 0x00,
                    0x00, 0x74, 0x22, (byte) 0x83, (byte) 0xe2, 0x10, 0x48, (byte) 0x8d, 0x05, 0x3e, (byte) 0x90, 0x00, 0x00, 0x75, 0x16, (byte) 0xf6, 0x41, 0x79, 0x02, 0x48, (byte) 0x8d, 0x05,
                    (byte) 0xe1, 0x7b, 0x00, 0x00, 0x48, (byte) 0x8d, 0x15, (byte) 0x9a, 0x11, 0x0b, 0x00, 0x48, 0x0f, 0x45, (byte) 0xc2, (byte) 0xc3, 0x0f, 0x1f, 0x44, 0x00, 0x00};

    private static final byte[] ASM_EXIT0 = {(byte) 0xb8, 0x3c, 0x00, 0x00, 0x00, 0x31, (byte) 0xff, 0x0f, 0x05};
    private static final byte[] ASM_MOV_1_EAX = {0x31, (byte) 0xc0, (byte) 0xff, (byte) 0xc0};

    private static final Signature SIG_STRCMP_IFUNC = new Signature(ASM_STRCMP_IFUNC);

    private static final Substitution SUBSTITUTION_EXIT_0 = new Substitution(ASM_EXIT0) {
        @Override
        public AMD64Instruction createNode(long pc, CodeReader reader) {
            reader.check(ASM_EXIT0);
            return new AMD64Instruction(pc, ASM_EXIT0) {
                @Override
                public long executeInstruction(VirtualFrame frame) {
                    throw new ProcessExitException(0);
                }

                @Override
                protected String[] disassemble() {
                    return new String[]{"exit", "0"};
                }
            };
        }
    };

    private static final Substitution SUBSTITUTION_MOV_1_EAX = new Substitution(ASM_MOV_1_EAX) {
        @Override
        public AMD64Instruction createNode(long pc, CodeReader reader) {
            reader.check(ASM_MOV_1_EAX);
            return new AMD64Instruction(pc, ASM_MOV_1_EAX) {
                @Child private RegisterWriteNode writeRAX;

                {
                    setGPRWriteOperands(new RegisterOperand(Register.RAX));
                }

                @Override
                protected void createChildNodes() {
                    ArchitecturalState state = getState();
                    RegisterAccessFactory regs = state.getRegisters();
                    AMD64Register rax = regs.getRegister(Register.RAX);
                    writeRAX = rax.createWrite();
                }

                @Override
                public long executeInstruction(VirtualFrame frame) {
                    writeRAX.executeI64(frame, 1);
                    return next();
                }

                @Override
                protected String[] disassemble() {
                    return new String[]{"mov", "eax", "1"};
                }
            };
        }
    };

    @Test
    public void testMatchSelf() {
        assertTrue(SIG_STRCMP_IFUNC.match(ASM_STRCMP_IFUNC));
    }

    @Test
    public void checkSignature1() throws Exception {
        byte[] code = TestDataLoader.getCode("bin/helloworld.elf");
        CodeReader reader = new CodeArrayReader(code, 0);
        assertFalse(SUBSTITUTION_EXIT_0.getSignature().match(reader));
        for (int i = 0; i < 20; i++) {
            reader.read8();
        }
        assertTrue(SUBSTITUTION_EXIT_0.getSignature().match(reader));
    }

    @Test
    public void checkSignature2() throws Exception {
        SubstitutionRegistry registry = new SubstitutionRegistry();
        registry.register(SUBSTITUTION_EXIT_0);
        registry.register(SUBSTITUTION_MOV_1_EAX);

        byte[] code = TestDataLoader.getCode("bin/helloworld.elf");
        CodeReader reader = new CodeArrayReader(code, 0);
        Substitution mov1eax = registry.getSubstitution(reader);
        assertSame(SUBSTITUTION_MOV_1_EAX, mov1eax);
        AMD64Instruction insn = mov1eax.createNode(reader.getPC(), reader);
        assertEquals("mov\teax,1", insn.getDisassembly());
        for (int i = 0; i < 16; i++) {
            reader.read8();
        }
        Substitution exit0 = registry.getSubstitution(reader);
        assertSame(SUBSTITUTION_EXIT_0, exit0);
        insn = exit0.createNode(reader.getPC(), reader);
        assertEquals("exit\t0", insn.getDisassembly());
    }
}
