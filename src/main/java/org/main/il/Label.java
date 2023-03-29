package org.main.il;

public class Label extends Instruction {
    private String label;

    public Label(String label) {
        this.label = label;
    }

    public String toString() {
        return label + ":\n";
    }
}
