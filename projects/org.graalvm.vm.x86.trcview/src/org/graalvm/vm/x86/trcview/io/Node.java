package org.graalvm.vm.x86.trcview.io;

public abstract class Node {
    protected BlockNode parent;

    protected void setParent(BlockNode parent) {
        this.parent = parent;
    }

    public BlockNode getParent() {
        return parent;
    }
}
