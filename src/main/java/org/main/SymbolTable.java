package org.main;

import org.main.decl.Decl;

import java.util.*;

public class SymbolTable {
    private Stack<List<Decl>> symbolTableStack = new Stack<>();

    public SymbolTable() {
        symbolTableStack.push(new ArrayList<>());
    }

    public void push() {
        symbolTableStack.push(new ArrayList<>());
    }

    public void add(Decl decl) {
        symbolTableStack.peek().add(decl);
    }

    public <T extends Decl> Optional<T> lookup(String name, Class<T> clazz) {
        for(int i = symbolTableStack.size() - 1; i >= 0; --i) {
            for (Decl decl : symbolTableStack.get(i)) {
                if (decl.getName().equals(name) && decl.getClass().equals(clazz)) {
                    return Optional.of((T) decl);
                }
            }
        }
        return Optional.empty();
    }

    public void pop() {
        var toCompress = symbolTableStack.pop();
    }
}
