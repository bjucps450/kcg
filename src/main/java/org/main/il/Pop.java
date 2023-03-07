package org.main.il;

import lombok.Data;

@Data
public class Pop implements Instruction {
    private InstructionSuffix suffix = InstructionSuffix.QUAD;
    private String toBePoppedInto;

    public Pop(Register register) {
        this.toBePoppedInto = "%" + register.getName();
    }

    public Pop(String toPush) {

    }

    public String toString() {
        return "\tpop" + suffix.getSuffix() + " " + toBePoppedInto;
    }
}
