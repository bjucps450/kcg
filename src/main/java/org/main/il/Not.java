package org.main.il;

import lombok.Data;

@Data
public class Not implements Instruction {
    private InstructionSuffix suffix = InstructionSuffix.QUAD;
    private String toBeNotted;

    public Not(Register register) {
        this.toBeNotted = "%" + register.getName();
    }

    public String toString() {
        return "\tnot" + suffix.getSuffix() + " " + toBeNotted;
    }
}
