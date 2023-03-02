package org.main.decl;

import lombok.Data;
import org.main.Type;

@Data
public class ParamDecl extends Decl {
    public ParamDecl(String name, Type type, Integer order) {
        this.name = name;
        this.type = type;
        this.order = order;
    }
}
