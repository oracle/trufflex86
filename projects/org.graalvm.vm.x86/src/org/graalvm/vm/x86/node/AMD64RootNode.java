package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.AMD64Language;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextReference;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;

public abstract class AMD64RootNode extends RootNode {
    protected AMD64RootNode(TruffleLanguage<?> language, FrameDescriptor fd) {
        super(language, fd);
    }

    protected ContextReference<AMD64Context> getContextReference() {
        return getLanguage(AMD64Language.class).getContextReference();
    }
}
