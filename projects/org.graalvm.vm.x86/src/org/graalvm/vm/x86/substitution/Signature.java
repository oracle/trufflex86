package org.graalvm.vm.x86.substitution;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.x86.isa.CodeReader;

public class Signature {
    private final byte[] signature;

    public Signature(byte[] signature) {
        if (signature.length < 1) {
            throw new IllegalArgumentException("need at least one byte");
        }
        this.signature = signature;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte getFirstByte() {
        return signature[0];
    }

    public boolean match(byte[] code) {
        if (code.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (code[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean match(CodeReader reader) {
        try {
            for (int i = 0; i < signature.length; i++) {
                if (reader.peek8(i) != signature[i]) {
                    return false;
                }
            }
            return true;
        } catch (SegmentationViolation e) {
            return false;
        }
    }
}
