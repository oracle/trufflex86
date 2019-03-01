package org.graalvm.vm.x86.substitution;

import java.util.ArrayList;
import java.util.List;

import org.graalvm.vm.x86.isa.CodeReader;
import org.graalvm.vm.x86.substitution.libc.Glibc228;

public class SubstitutionRegistry {
    @SuppressWarnings("unchecked") private final List<Substitution>[] signatures = new ArrayList[256];

    public SubstitutionRegistry() {
        for (int i = 0; i < signatures.length; i++) {
            signatures[i] = new ArrayList<>();
        }
        Glibc228.register(this);
    }

    public void register(Substitution substitution) {
        signatures[Byte.toUnsignedInt(substitution.getSignature().getFirstByte())].add(substitution);
    }

    public List<Substitution> getSubstitutions(byte initial) {
        return signatures[Byte.toUnsignedInt(initial)];
    }

    public Substitution getSubstitution(CodeReader reader) {
        List<Substitution> candidates = getSubstitutions(reader.peek8(0));
        for (Substitution candidate : candidates) {
            if (candidate.getSignature().match(reader)) {
                return candidate;
            }
        }
        return null;
    }
}
