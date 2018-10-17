package org.graalvm.vm.x86.trcview.ui.event;

import org.graalvm.vm.x86.trcview.io.BlockNode;

public interface LevelUpListener {
    void levelUp(BlockNode block);
}
