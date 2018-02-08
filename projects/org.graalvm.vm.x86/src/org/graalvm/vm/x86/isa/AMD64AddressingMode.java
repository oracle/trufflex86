package org.graalvm.vm.x86.isa;

public class AMD64AddressingMode {
    // manual: p65 for ModR/M
    // manual: p130 for +rb/+rw/+rd/+ro

    public static final String[] REG_RB = {"AL", "CL", "DL", "BL", "AH", "CH", "DH", "BH"};
    public static final String[] REG_RW = {"AX", "CX", "DX", "BX", "SP", "BP", "SI", "DI"};
    public static final String[] REG_RD = {"EAX", "ECX", "EDX", "EBX", "ESP", "EBP", "ESI", "EDI"};
    public static final String[] REG_RO = {"RAX", "RCX", "RDX", "RBX", "RSP", "RBP", "RSI", "RDI"};
}
