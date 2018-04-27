package org.graalvm.vm.x86.node;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.flow.InterTraceDispatchNode;
import org.graalvm.vm.x86.node.init.InitializerNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterRootNode extends AMD64Node {
    @Child private InitializerNode initializer;
    @Child private InterTraceDispatchNode interpreter;

    public InterpreterRootNode(ArchitecturalState state, String programName) {
        initializer = new InitializerNode(state, programName);
        interpreter = new InterTraceDispatchNode(state);
    }

    public Object execute(VirtualFrame frame) {
        initializer.execute(frame);
        return interpreter.execute(frame);
    }
}
