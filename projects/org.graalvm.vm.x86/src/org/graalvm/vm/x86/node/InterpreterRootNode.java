package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.init.InitializerNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterRootNode extends AMD64Node {
    @Child private InitializerNode initializer;

    public InterpreterRootNode(ArchitecturalState state, String programName) {
        initializer = new InitializerNode(state, programName);
    }

    public Object execute(VirtualFrame frame) {
        initializer.execute(frame);
        return 0;
    }
}
