package org.main.il;

public class Stabs extends Instruction {
    private String stabs;
    public Stabs(String s) {
        this.stabs = s;
    }

    public String toString() {
        return stabs;
    }
}
