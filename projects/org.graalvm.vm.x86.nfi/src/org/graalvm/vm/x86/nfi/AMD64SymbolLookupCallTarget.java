package org.graalvm.vm.x86.nfi;

import org.graalvm.vm.x86.AMD64Context;
import org.graalvm.vm.x86.node.AMD64RootNode;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;

public class AMD64SymbolLookupCallTarget extends AMD64RootNode {
    @Child private AMD64SymbolLookupNode lookup;

    public AMD64SymbolLookupCallTarget(TruffleLanguage<AMD64Context> language, FrameDescriptor fd) {
        super(language, fd);
        lookup = new AMD64SymbolLookupNode(AMD64NFILanguage.getCurrentContextReference().get().getState());
    }

    @Override
    public Object execute(VirtualFrame frame) {
        AMD64Library lib = (AMD64Library) frame.getArguments()[0];
        String name = (String) frame.getArguments()[1];
        return lookup.executeLookup(frame, lib, name);
    }
}
