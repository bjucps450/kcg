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
    NOT("not"),
    OR("or"),
    AND("and"),
    LEAVE("leave"),
    RET("ret"),
    CALL("call"),
    MOV("mov");

    @Getter
    private String name;
}
