package org.main.decl;

import lombok.Data;
import org.main.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class MethodDecl extends Decl {
    public MethodDecl(String name, Type type, Integer order) {
        this.name = name;
        this.type = type;
        this.order = order;
    }
    private List<MethodDecl> methods = new ArrayList<>();
    private List<ParamDecl> parameters = new ArrayList<>();
    private List<VarDecl> variables = new ArrayList<>();

    public void checkArg(Integer position, Type typeProvided) {
        if(position < parameters.size()) {
            Type exepected = parameters.get(position).getType();
            if(!exepected.equals(typeProvided)) {
                System.out.println(name + " expected " + exepected + " for " + position + "-th parameter but got " + typeProvided);
            }
        }
    }

    public <T extends Decl> Optional<T> lookup(String name, Class<T> clazz) {
        if(clazz.equals(MethodDecl.class)) {
            return (Optional<T>) this.methods.stream().filter(x -> x.getName().equals(name)).findFirst();
        } if(clazz.equals(VarDecl.class)) {
            return (Optional<T>) this.variables.stream().filter(x -> x.getName().equals(name)).findFirst();
        } if(clazz.equals(ParamDecl.class)) {
            return (Optional<T>) this.parameters.stream().filter(x -> x.getName().equals(name)).findFirst();
        }
        return Optional.empty();
    }
}
