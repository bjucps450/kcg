package org.main;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Type {
    public static final Type INTEGER = new Type("int");
    public static final Type STRING = new Type("str");
    public static final Type BOOL = new Type("bool");
    public static final Type ERROR = new Type("error");
    private String name;
}
