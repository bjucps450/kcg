package org.main.il;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Instruction {
    private String op = null;
    private String suffix = null;
    private String operand1 = null;
    private String operand2 = null;

    public Instruction(Operation op) {
        this.op = op.getName();
    }

    public Instruction(Operation op, InstructionSuffix suffix) {
        this.op = op.getName();
        this.suffix = suffix.getSuffix();
    }

    public Instruction(Operation op, InstructionSuffix suffix, Register register) {
        this.op = op.getName();
        this.suffix = suffix.getSuffix();
        this.operand1 = "%" + register.getName();
    }

    public Instruction(Operation op, InstructionSuffix suffix, Integer num) {
        this.op = op.getName();
        this.suffix = suffix.getSuffix();
        this.operand1 = "$" + num;
    }

    public Instruction(Operation op, InstructionSuffix suffix, Register src, Register dest) {
        this.op = op.getName();
        this.suffix = suffix.getSuffix();
        this.operand1 = "%" + src.getName();
        this.operand2 = "%" + dest.getName();
    }

    public String toString() {
        String instruction = "\t" + this.op;
        if(suffix != null) {
            instruction += suffix;
        }
        if(operand1 != null) {
            instruction += " " + operand1;
        }
        if(operand2 != null) {
            instruction += ", " + operand2;
        }
        return instruction;
    }
}
