package org.main.decl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MethodDecl extends Decl {
    private List<MethodDecl> methods = new ArrayList<>();
    private List<ParamDecl> parameters = new ArrayList<>();
    private List<VarDecl> variables = new ArrayList<>();
}
