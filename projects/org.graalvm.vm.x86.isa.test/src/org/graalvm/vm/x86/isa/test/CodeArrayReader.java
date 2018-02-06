package org.graalvm.vm.x86.isa.test;

import org.graalvm.vm.x86.isa.CodeReader;

public class CodeArrayReader extends CodeReader {
    private int offset;
    private byte[] code;

    public CodeArrayReader(byte[] code, int offset) {
        this.offset = offset;
        this.code = code;
    }

    @Override
    public byte read8() {
        return code[offset++];
    }

    public int available() {
        return code.length - offset;
    }

    @Override
    public boolean isAvailable() {
        return available() > 0;
    }
}
