package org.main;

import com.ibm.icu.text.ArabicShaping;
import org.bju.KCG.KCGParser;
import org.main.decl.MethodDecl;
import org.main.il.*;

import java.util.*;

public class KCGCodeGenerator extends org.bju.KCG.KCGBaseVisitor<List<Instruction>> {
    private String filename;
    private SymbolTable symbolTable;
    public KCGCodeGenerator(SymbolTable symbolTable, String filename) {
        this.symbolTable = symbolTable;
        this.filename = filename;
    }

    @Override
    public List<Instruction> visitStart(KCGParser.StartContext ctx) {
        List<Instruction> instructions = new ArrayList<>();
        for(var statement : ctx.statements) {
            instructions.addAll(visit(statement));
        }
        for(var method : ctx.methods) {
            instructions.addAll(visit(method));
        }

        return instructions;
    }

    @Override
    public List<Instruction> visitStatementif(KCGParser.StatementifContext ctx) {
        return visit(ctx.statement_if());
    }

    @Override
    public List<Instruction> visitStatementwhile(KCGParser.StatementwhileContext ctx) {
        return visit(ctx.statement_while());
    }

    @Override
    public List<Instruction> visitStatementassignment(KCGParser.StatementassignmentContext ctx) {
        return visit(ctx.statement_assignment());
    }

    @Override
    public List<Instruction> visitStatementexpr(KCGParser.StatementexprContext ctx) {
        return visit(ctx.expr());
    }

    private Integer ifCounter = 0;

    @Override
    public List<Instruction> visitStatement_if(KCGParser.Statement_ifContext ctx) {
        List<Instruction> instructions = new ArrayList<>();
        instructions.addAll(visit(ctx.cond));
        instructions.add(new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX));

        return instructions;
    }

    @Override
    public List<Instruction> visitElse(KCGParser.ElseContext ctx) {
        return super.visitElse(ctx);
    }

    private Integer whileCounter = 0;

    @Override
    public List<Instruction> visitStatement_while(KCGParser.Statement_whileContext ctx) {
        return super.visitStatement_while(ctx);
    }

    private Set<String> visitedVariables = new HashSet<>();

    @Override
    public List<Instruction> visitStatement_assignment(KCGParser.Statement_assignmentContext ctx) {
        List<Instruction> instructions = new ArrayList<>();
        String name = "_var_" + ctx.id.getText();
        if(!visitedVariables.contains(name)) {
            instructions.add(new Stabs(".data"));
            instructions.add(new Stabs(".comm " + name + ", 8, 4"));
            instructions.add(new Stabs(".text"));
            visitedVariables.add(name);
        }
        instructions.addAll(visit(ctx.expr()));
        instructions.add(new Instruction(Operation.POP, InstructionSuffix.QUAD, "$" + name + "(%rip)"));
        return instructions;
    }

    private Stack<MethodDecl> methodDeclStack = new Stack<>();

    private MethodDecl lookupMethod(String name) {
        for(int i = methodDeclStack.size() - 1; i >= 0; --i) {
            var possibleMethod = methodDeclStack.get(i).lookup(name, MethodDecl.class);
            if(possibleMethod.isPresent()) {
                return possibleMethod.get();
            }
        }
        var possibleMethod = this.symbolTable.lookup(name, MethodDecl.class);
        return possibleMethod.orElse(null);
    }

    @Override
    public List<Instruction> visitMethod(KCGParser.MethodContext ctx) {
        MethodDecl methodDecl = lookupMethod(ctx.name.getText());
        methodDeclStack.push(methodDecl);
        List<Instruction> instructions = new ArrayList<>();
        if(ctx.guts.methods != null) {
            for(var entity : ctx.guts.methods) {
                instructions.addAll(visit(entity));
            }
        }
        String name = "_meth_" + ctx.name.getText();
        instructions.add(new Stabs(name + ":"));
        instructions.add(new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RBP));
        instructions.add(new Instruction(Operation.MOV, InstructionSuffix.QUAD, Register.RSP, Register.RBP));
        int numOfParams = methodDecl.getVariables().size() + (methodDecl.getVariables().size() % 2 == 1 ? 1 : 0);
        instructions.add(new Instruction(Operation.SUB, InstructionSuffix.QUAD, numOfParams * 8, Register.RSP));
        if(ctx.guts.statements != null) {
            for(var entity : ctx.guts.statements) {
                instructions.addAll(visit(entity));
            }
            instructions.add(new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX));
        } else {
            instructions.add(new Instruction(Operation.MOV, InstructionSuffix.QUAD, 0, Register.RAX));
        }
        instructions.add(new Instruction(Operation.LEAVE));
        instructions.add(new Instruction(Operation.RET));
        methodDeclStack.pop();
        return instructions;
    }

    @Override
    public List<Instruction> visitArgs(KCGParser.ArgsContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Instruction> visitArg(KCGParser.ArgContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Instruction> visitInttype(KCGParser.InttypeContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Instruction> visitStrtype(KCGParser.StrtypeContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Instruction> visitBooltype(KCGParser.BooltypeContext ctx) {
        return new ArrayList<>();
    }

    private Register getParamRegister(int i) {
        switch (i) {
            case 1: return Register.RSI;
            case 2: return Register.RDX;
            case 3: return Register.RCX;
            case 4: return Register.R8;
            case 5: return Register.R9;
        }
        return Register.RDI;
    }

    @Override
    public List<Instruction> visitExprs(KCGParser.ExprsContext ctx) {
        List<Instruction> instructions = new ArrayList<>();
        if(ctx.second != null) {
            for (int i = ctx.second.size(); i >= 0; --i) {
                instructions.addAll(visit(ctx.second.get(i)));
                if(i + 1 < 6) {
                    instructions.add(new Instruction(Operation.POP, InstructionSuffix.QUAD, getParamRegister(i + 1)));
                }
            }
        }
        instructions.addAll(visit(ctx.first));
        instructions.add(new Instruction(Operation.POP, InstructionSuffix.QUAD, getParamRegister(0)));
        return instructions;
    }

    @Override
    public List<Instruction> visitStr(KCGParser.StrContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Instruction> visitMethodcall(KCGParser.MethodcallContext ctx) {
        List<Instruction> instructions = new ArrayList<>();
        MethodDecl methodDecl = lookupMethod(ctx.name.getText());
        if(methodDecl.getParameters().size() > 6 && methodDecl.getParameters().size() % 2 != 0) {
            instructions.add(new Instruction(Operation.SUB, InstructionSuffix.QUAD, 8, Register.RSP));
        }
        if(ctx.exprs() != null) {
            instructions.addAll(visit(ctx.exprs()));
        }
        instructions.add(new Instruction(Operation.CALL, InstructionSuffix.QUAD, "_meth_" + ctx.name.getText()));
        if(methodDecl.getParameters().size() > 6) {
            int amountToAdd = (methodDecl.getParameters().size() - 6) + (methodDecl.getParameters().size() % 2 == 1 ? 1 : 0);
            instructions.add(new Instruction(Operation.ADD, InstructionSuffix.QUAD, amountToAdd * 8, Register.RSP));
        }
        return instructions;
    }

    @Override
    public List<Instruction> visitNot(KCGParser.NotContext ctx) {
        return List.of(
            new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
            new Instruction(Operation.NOT, InstructionSuffix.QUAD, Register.RAX),
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
        );
    }

    @Override
    public List<Instruction> visitOr(KCGParser.OrContext ctx) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.addAll(visit(ctx.second));
        instructions.addAll(visit(ctx.first));
        instructions.addAll(List.of(
            new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
            new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RBX),
            new Instruction(Operation.OR, InstructionSuffix.QUAD, Register.RBX, Register.RAX),
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
        ));
        return instructions;
    }

    @Override
    public List<Instruction> visitAnd(KCGParser.AndContext ctx) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.addAll(visit(ctx.second));
        instructions.addAll(visit(ctx.first));
        instructions.addAll(List.of(
            new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
            new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RBX),
            new Instruction(Operation.AND, InstructionSuffix.QUAD, Register.RBX, Register.RAX),
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
        ));
        return instructions;
    }

    @Override
    public List<Instruction> visitMultdiv(KCGParser.MultdivContext ctx) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.addAll(visit(ctx.second));
        instructions.addAll(visit(ctx.first));
        if(ctx.op.getText().equals("+")) {
            instructions.addAll(List.of(
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RBX),
                new Instruction(Operation.MUL, InstructionSuffix.QUAD, Register.RBX),
                new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
            ));
        } else {
            instructions.addAll(List.of(
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
                new Instruction(Operation.CQO),
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RBX),
                new Instruction(Operation.DIV, InstructionSuffix.QUAD, Register.RBX),
                new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
            ));
        }
        return instructions;
    }

    @Override
    public List<Instruction> visitTrue(KCGParser.TrueContext ctx) {
        return List.of(
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, 1)
        );
    }

    @Override
    public List<Instruction> visitFalse(KCGParser.FalseContext ctx) {
        return List.of(
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, 0)
        );
    }

    @Override
    public List<Instruction> visitAddsub(KCGParser.AddsubContext ctx) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.addAll(visit(ctx.second));
        instructions.addAll(visit(ctx.first));
        if(ctx.op.getText().equals("-")) {
            instructions.addAll(List.of(
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RBX),
                new Instruction(Operation.ADD, InstructionSuffix.QUAD, Register.RBX, Register.RAX),
                new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
            ));
        } else {
            instructions.addAll(List.of(
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RAX),
                new Instruction(Operation.POP, InstructionSuffix.QUAD, Register.RBX),
                new Instruction(Operation.SUB, InstructionSuffix.QUAD, Register.RBX, Register.RAX),
                new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Register.RAX)
            ));
        }
        return instructions;
    }

    @Override
    public List<Instruction> visitId(KCGParser.IdContext ctx) {
        return List.of(
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, "$_var_" + ctx.IDENTIFIER().getText() + "(%rip)"))
        };
    }

    @Override
    public List<Instruction> visitInt(KCGParser.IntContext ctx) {
        return List.of(
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Integer.parseInt(ctx.DIGIT().getText()))
        );
    }
}
