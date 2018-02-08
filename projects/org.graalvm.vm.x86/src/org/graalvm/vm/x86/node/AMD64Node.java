package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;

import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.nodes.Node;

public class AMD64Node extends Node {
    protected ContextReference<AMD64Context> getContextReference() {
        return getRootNode().getLanguage(AMD64Language.class).getContextReference();
    }
}
