package org.graalvm.vm.x86.node.flow;

import org.graalvm.vm.x86.node.AMD64Node;

import com.oracle.truffle.api.frame.VirtualFrame;

public abstract class AbstractDispatchNode extends AMD64Node {
    public abstract long execute(VirtualFrame frame);
}
