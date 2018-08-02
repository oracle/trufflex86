package org.graalvm.vm.memory.hardware;

import org.graalvm.vm.memory.Memory;

import com.oracle.truffle.api.CompilerDirectives;

public class NullMemory extends Memory {
    private final long size;

    public NullMemory(boolean isBE, long size) {
        super(isBE);
        this.size = size;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    protected byte i8(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected short i16B(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected short i16L(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected int i32B(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected int i32L(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected long i64B(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected long i64L(long pos) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i8(long pos, byte val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i16L(long pos, short val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i16B(long pos, short val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i32L(long pos, int val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i32B(long pos, int val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i64L(long pos, long val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }

    @Override
    protected void i64B(long pos, long val) {
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException();
    }
}
