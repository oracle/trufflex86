package org.graalvm.vm.x86.trcview.ui.event;

import org.graalvm.vm.x86.trcview.io.BlockNode;
import org.graalvm.vm.x86.trcview.io.RecordNode;

public interface CallListener {
    void call(BlockNode call);

    void ret(RecordNode ret);
}
