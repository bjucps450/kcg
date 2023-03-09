package org.main.il;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Operation {
    MUL("imul"),
    DIV("idiv"),
    ADD("add"),
    SUB("sub"),
    CQO("cqo"),
    PUSH("push"),
    POP("pop"),
    NOT("not");

    @Getter
    private String name;
}
