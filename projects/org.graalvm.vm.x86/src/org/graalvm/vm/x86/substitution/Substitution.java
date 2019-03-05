package org.graalvm.vm.x86.substitution;

import org.graalvm.vm.x86.isa.AMD64Instruction;
import org.graalvm.vm.x86.isa.CodeReader;

public abstract class Substitution {
    private final Signature signature;

    protected Substitution(byte[] signature) {
        this(new Signature(signature));
    }

    protected Substitution(byte[] signature, boolean[] mask) {
        this(new Signature(signature, mask));
    }

    protected Substitution(Signature signature) {
        this.signature = signature;
    }

    public Signature getSignature() {
        return signature;
    }

    public abstract AMD64Instruction createNode(long pc, CodeReader reader);
}
