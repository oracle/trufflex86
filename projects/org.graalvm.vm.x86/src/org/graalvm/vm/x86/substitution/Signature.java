package org.graalvm.vm.x86.substitution;

import org.graalvm.vm.memory.exception.SegmentationViolation;
import org.graalvm.vm.x86.isa.CodeReader;

public class Signature {
    private final byte[] signature;
    private final boolean[] mask;

    public Signature(byte[] signature) {
        this(signature, null);
    }

    public Signature(byte[] signature, boolean[] mask) {
        if (signature.length < 1) {
            throw new IllegalArgumentException("need at least one byte");
        }
        if (mask != null && mask.length != signature.length) {
            throw new IllegalArgumentException("invalid mask length");
        }
        this.signature = signature;
        this.mask = mask;
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
            if (code[i] != signature[i] && (mask == null || !mask[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean match(CodeReader reader) {
        try {
            for (int i = 0; i < signature.length; i++) {
                if (reader.peek8(i) != signature[i] && (mask == null || !mask[i])) {
                    return false;
                }
            }
            return true;
        } catch (SegmentationViolation e) {
            return false;
        }
    }
}
