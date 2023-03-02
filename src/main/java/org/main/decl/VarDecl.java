package org.main.decl;

import lombok.Data;
import org.main.Type;

@Data
public class VarDecl extends Decl {
    public VarDecl(String name, Type type, Integer order) {
        this.name = name;
        this.type = type;
        this.order = order;
    }
}
