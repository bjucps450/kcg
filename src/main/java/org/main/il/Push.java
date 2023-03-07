package org.main.il;

import lombok.Data;

@Data
public class Push implements Instruction {
    private InstructionSuffix suffix = InstructionSuffix.QUAD;
    private String toBePushed;

    public Push(Integer toPush) {
        this.toBePushed = "$" + toPush;
    }

    public Push(Register toPush) {
        this.toBePushed = "%" + toPush.getName();
    }

    public Push(String toPush) {

    }

    public String toString() {
        return "\tpush" + suffix.getSuffix() + " " + toBePushed;
    }
}
