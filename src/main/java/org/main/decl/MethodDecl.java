package org.main.decl;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.main.Type;

import java.util.ArrayList;
import java.util.List;

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
}
