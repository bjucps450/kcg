package org.main.decl;

import lombok.Data;
import org.main.Type;

@Data
public class Decl {
    protected String name;
    protected Type type;
    protected Integer order;
}
