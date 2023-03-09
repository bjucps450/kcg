package org.main.il;

public class Label extends Instruction {
    private String label;
    private Boolean isMethod = false;

    public Label(String label, Boolean isMethod) {
        this.label = label;
        this.isMethod = isMethod;
    }

    public String toString() {
        return (isMethod ? "" : ".") + label + ":";
    }
}
