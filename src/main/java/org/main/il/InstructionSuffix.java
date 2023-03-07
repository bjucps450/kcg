package org.main.il;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InstructionSuffix {
    QUAD("q"),
    DOUBLE("d"),
    WORD("w"),
    BYTE("b");

    @Getter
    private String suffix;
}
