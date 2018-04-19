package org.graalvm.vm.x86.node.debug;

import org.graalvm.vm.x86.isa.CpuState;
import org.graalvm.vm.x86.node.AMD64Node;
import org.graalvm.vm.x86.node.init.CopyToCpuStateNode;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;

public class PrintStateNode extends AMD64Node {
    @Child private CopyToCpuStateNode toState;

    @TruffleBoundary
    private static void print(CpuState state) {
        System.out.println(state);
    }

    public void execute(VirtualFrame frame, long pc) {
        if (toState == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            toState = insert(new CopyToCpuStateNode());
        }
        CpuState state = toState.execute(frame, pc);
        print(state);
    }
}
