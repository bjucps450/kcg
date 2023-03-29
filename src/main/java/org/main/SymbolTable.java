package org.main;

import org.main.decl.Decl;
import org.main.decl.MethodDecl;
import org.main.decl.ParamDecl;
import org.main.decl.VarDecl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

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

    public <T extends Decl> Integer getOrder(Class<T> clazz) {
        Integer count = 0;
        for(int i = symbolTableStack.size() - 1; i >= 0; --i) {
            for (Decl decl : symbolTableStack.get(i)) {
                if (decl.getClass().equals(clazz)) {
                    ++count;
                }
            }
        }
        return count;
    }

    public void pop() {
        var toCompress = symbolTableStack.pop();
        if(!toCompress.isEmpty() && toCompress.get(0) instanceof MethodDecl) {
            MethodDecl method = (MethodDecl) toCompress.get(0);
            for(int i = 1; i < toCompress.size(); ++i) {
                if(toCompress.get(i) instanceof MethodDecl) {
                    method.getMethods().add((MethodDecl) toCompress.get(i));
                } else if(toCompress.get(i) instanceof ParamDecl) {
                    method.getParameters().add((ParamDecl) toCompress.get(i));
                } else if(toCompress.get(i) instanceof VarDecl) {
                    method.getVariables().add((VarDecl) toCompress.get(i));
                }
            }
            add(method);
        } else if(!toCompress.isEmpty()) {
            add(toCompress.get(0));
        } else {
            throw new RuntimeException("should not have gotten empty level");
        }
    }
}
