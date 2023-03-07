package org.main.il;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Register {
    RAX("rax"),
    RBX("rbx"),
    RCX("rcx"),
    RDX("rdx"),
    RSP("rsp"),
    RBP("rbp"),
    RDI("rdi"),
    RSI("rsi"),
    R8("r8"),
    R9("r9"),
    R10("r10"),
    R11("r11"),
    R12("r12"),
    R13("r13"),
    R14("r14"),
    R15("r15");

    @Getter
    private String name;
}
