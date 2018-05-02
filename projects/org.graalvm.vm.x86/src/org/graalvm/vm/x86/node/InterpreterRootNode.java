package org.graalvm.vm.x86.node;

import static org.graalvm.vm.x86.Options.getBoolean;

import org.graalvm.vm.x86.ArchitecturalState;
import org.graalvm.vm.x86.node.flow.AbstractDispatchNode;
import org.graalvm.vm.x86.node.flow.DispatchNode;
import org.graalvm.vm.x86.node.flow.InterTraceDispatchNode;
import org.graalvm.vm.x86.node.init.InitializerNode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;

public class InterpreterRootNode extends AMD64Node {
    @CompilationFinal private static boolean SIMPLE_DISPATCH = getBoolean("vmx86.debug.simpleDispatch", false) || getBoolean("vmx86.debug.exec", false);
    @CompilationFinal private static boolean DEBUG_STATE = getBoolean("vmx86.debug.state", false);

    @Child private InitializerNode initializer;
    @Child private AbstractDispatchNode interpreter;

    public InterpreterRootNode(ArchitecturalState state, String programName) {
        initializer = new InitializerNode(state, programName);
        if (DEBUG_STATE || SIMPLE_DISPATCH) {
            interpreter = new DispatchNode(state);
        } else {
            interpreter = new InterTraceDispatchNode(state);
        }
    }

    public Object execute(VirtualFrame frame) {
        initializer.execute(frame);
        return interpreter.execute(frame);
    }
}
