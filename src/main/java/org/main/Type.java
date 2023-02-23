package org.main;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Type {
    public static final Type INTEGER = new Type("int");
    public static final Type STRING = new Type("str");
    private String name;
}
