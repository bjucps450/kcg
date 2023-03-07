package org.main;

import lombok.Data;
import org.bju.KCG.KCGParser;
import org.main.decl.MethodDecl;
import org.main.decl.ParamDecl;
import org.main.decl.VarDecl;

@Data
public class KCGSemanticAnalyzer extends org.bju.KCG.KCGBaseVisitor<Type> {

    private SymbolTable symbolTable = new SymbolTable();

    @Override
    public Type visitStart(KCGParser.StartContext ctx) {
        if(ctx.methods != null) {
            ctx.methods.forEach(this::visit);
        }
        Type lastStatementType = Type.ERROR;
        if(ctx.statements != null) {
            for(var statement : ctx.statements) {
                lastStatementType = visit(statement);
            }
        } else {
            System.out.println("at least one statement is required in methods");
        }
        return lastStatementType;
    }

    @Override
    public Type visitStatementif(KCGParser.StatementifContext ctx) {
        return visit(ctx.statement_if());
    }

    @Override
    public Type visitStatementwhile(KCGParser.StatementwhileContext ctx) {
        return visit(ctx.statement_while());
    }

    @Override
    public Type visitStatementassignment(KCGParser.StatementassignmentContext ctx) {
        return visit(ctx.statement_assignment());
    }

    @Override
    public Type visitStatementexpr(KCGParser.StatementexprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Type visitStatement_if(KCGParser.Statement_ifContext ctx) {
        Type type = visit(ctx.cond);
        if(!type.equals(Type.BOOL)) {
            System.out.println("if condition is not a boolean");
        }
        if(ctx.true_ != null) {
            ctx.true_.forEach(this::visit);
        }
        if(ctx.false_ != null) {
            visit(ctx.false_);
        }
        return Type.BOOL;
    }

    @Override
    public Type visitElse(KCGParser.ElseContext ctx) {
        if(ctx.false_ != null) {
            ctx.false_.forEach(this::visit);
        }
        return Type.BOOL;
    }

    @Override
    public Type visitStatement_while(KCGParser.Statement_whileContext ctx) {
        Type type = visit(ctx.cond);
        if(!type.equals(Type.BOOL)) {
            System.out.println("while condition is not a boolean");
        }
        if(ctx.true_ != null) {
            ctx.true_.forEach(this::visit);
        }
        return Type.BOOL;
    }

    @Override
    public Type visitStatement_assignment(KCGParser.Statement_assignmentContext ctx) {
        Type exprType = visit(ctx.expr());
        var decl1 = symbolTable.lookup(ctx.IDENTIFIER().getText(), VarDecl.class);
        var decl2 = symbolTable.lookup(ctx.IDENTIFIER().getText(), ParamDecl.class);
        if(decl1.isPresent()) {
            if(!exprType.equals(decl1.get().getType())) {
                System.out.println("could not assign type " + exprType + " to type " + decl1.get().getType());
                return Type.ERROR;
            }
        } else if(decl2.isPresent()) {
            if(!exprType.equals(decl2.get().getType())) {
                System.out.println("could not assign type " + exprType + " to type " + decl2.get().getType());
                return Type.ERROR;
            }
        } else {
            symbolTable.push();
            symbolTable.add(new VarDecl(ctx.IDENTIFIER().getText(), exprType, symbolTable.getOrder(VarDecl.class)));
            symbolTable.pop();
        }
        return exprType;
    }

    @Override
    public Type visitMethod(KCGParser.MethodContext ctx) {
        var existingMethod = symbolTable.lookup(ctx.IDENTIFIER().getText(), MethodDecl.class);
        if(existingMethod.isPresent()) {
            System.out.println("a method with name " + ctx.IDENTIFIER().getText() + " already exists");
            return Type.ERROR;
        }
        Type methodType = visit(ctx.datatype);
        symbolTable.push();
        symbolTable.add(new MethodDecl(ctx.IDENTIFIER().getText(), methodType, symbolTable.getOrder(MethodDecl.class)));
        if(ctx.arguments != null) {
            visit(ctx.arguments);
        }
        Type lastStatementType = visit(ctx.guts);
        if(!lastStatementType.equals(methodType)) {
            System.out.println("method expected return of " + methodType + " but got " + lastStatementType);
        }
        symbolTable.pop();
        return methodType;
    }

    @Override
    public Type visitArgs(KCGParser.ArgsContext ctx) {
        visit(ctx.first);
        if(ctx.second != null) {
            for(var arg : ctx.second) {
                visit(arg);
            }
        }
        return Type.VOID;
    }

    @Override
    public Type visitArg(KCGParser.ArgContext ctx) {
        Type paramType = visit(ctx.type());
        symbolTable.push();
        symbolTable.add(new ParamDecl(ctx.IDENTIFIER().getText(), paramType, symbolTable.getOrder(ParamDecl.class)));
        symbolTable.pop();
        return paramType;
    }

    @Override
    public Type visitInttype(KCGParser.InttypeContext ctx) {
        return Type.INTEGER;
    }

    @Override
    public Type visitStrtype(KCGParser.StrtypeContext ctx) {
        return Type.STRING;
    }

    @Override
    public Type visitBooltype(KCGParser.BooltypeContext ctx) {
        return Type.BOOL;
    }

    private MethodDecl methodBeingCalled = null;

    @Override
    public Type visitExprs(KCGParser.ExprsContext ctx) {
        Type firstArg = visit(ctx.first);
        methodBeingCalled.checkArg(0, firstArg);
        if(ctx.second != null) {
            for (int i = 0; i < ctx.second.size(); ++i) {
                Type nextArg = visit(ctx.second.get(i));
                methodBeingCalled.checkArg(i + 1, nextArg);
            }
        }
        return Type.VOID;
    }

    @Override
    public Type visitStr(KCGParser.StrContext ctx) {
        return Type.STRING;
    }

    @Override
    public Type visitMethodcall(KCGParser.MethodcallContext ctx) {
        var method = symbolTable.lookup(ctx.IDENTIFIER().getText(), MethodDecl.class);
        if(method.isEmpty()) {
            System.out.println(ctx.IDENTIFIER().getText() + " doesn't exist");
            return Type.ERROR;
        }
        methodBeingCalled = method.get();
        int expectedCount = method.get().getParameters().size();
        int actualCount = 0;
        if(ctx.exprs() != null) {
            actualCount = 1 + (ctx.exprs().second != null ? ctx.exprs().second.size() : 0);
            visit(ctx.exprs());
        }
        if(expectedCount != actualCount) {
            System.out.println(ctx.IDENTIFIER().getText() + " expected " + expectedCount + " args but got " + actualCount);
            return Type.ERROR;
        }
        return method.get().getType();
    }

    @Override
    public Type visitNot(KCGParser.NotContext ctx) {
        Type expr = visit(ctx.expr());
        if(!expr.equals(Type.BOOL)) {
            System.out.println("expression for not is not of type boolean");
            return Type.ERROR;
        }
        return Type.BOOL;
    }

    @Override
    public Type visitOr(KCGParser.OrContext ctx) {
        Type expr1 = visit(ctx.first);
        Type expr2 = visit(ctx.second);
        if(!expr1.equals(Type.BOOL)) {
            System.out.println("lhs expression for or is not of type boolean");
            return Type.ERROR;
        }
        if(!expr2.equals(Type.BOOL)) {
            System.out.println("rhs expression for or is not of type boolean");
            return Type.ERROR;
        }
        return Type.BOOL;
    }

    @Override
    public Type visitAnd(KCGParser.AndContext ctx) {
        Type expr1 = visit(ctx.first);
        Type expr2 = visit(ctx.second);
        if(!expr1.equals(Type.BOOL)) {
            System.out.println("lhs expression for and is not of type boolean");
            return Type.ERROR;
        }
        if(!expr2.equals(Type.BOOL)) {
            System.out.println("rhs expression for and is not of type boolean");
            return Type.ERROR;
        }
        return Type.BOOL;
    }

    @Override
    public Type visitMultdiv(KCGParser.MultdivContext ctx) {
        Type expr1 = visit(ctx.first);
        Type expr2 = visit(ctx.second);
        String operator = ctx.op.getText().equals("+") ? "multiplication" : "division";
        if(!expr1.equals(Type.INTEGER)) {
            System.out.println("lhs expression for " + operator + " is not of type integer");
            return Type.ERROR;
        }
        if(!expr2.equals(Type.INTEGER)) {
            System.out.println("rhs expression for " + operator + " is not of type integer");
            return Type.ERROR;
        }
        return Type.INTEGER;
    }

    @Override
    public Type visitTrue(KCGParser.TrueContext ctx) {
        return Type.BOOL;
    }

    @Override
    public Type visitFalse(KCGParser.FalseContext ctx) {
        return Type.BOOL;
    }

    @Override
    public Type visitAddsub(KCGParser.AddsubContext ctx) {
        Type expr1 = visit(ctx.first);
        Type expr2 = visit(ctx.second);
        String operator = ctx.op.getText().equals("-") ? "addition" : "subtraction";
        if(operator.equals("addition") && !expr1.equals(Type.INTEGER) && !expr1.equals(Type.STRING)) {
            System.out.println("lhs expression for " + operator + " is not of type integer or string");
            return Type.ERROR;
        } else if(operator.equals("subtraction") && !expr1.equals(Type.INTEGER)) {
            System.out.println("lhs expression for " + operator + " is not of type integer");
            return Type.ERROR;
        }
        if(operator.equals("addition") && !expr2.equals(Type.INTEGER) && !expr2.equals(Type.STRING)) {
            System.out.println("rhs expression for " + operator + " is not of type integer or string");
            return Type.ERROR;
        } else if(operator.equals("subtraction") && !expr1.equals(Type.INTEGER)) {
            System.out.println("rhs expression for " + operator + " is not of type integer");
            return Type.ERROR;
        }
        if(operator.equals("addition") && expr1.equals(Type.INTEGER) && expr2.equals(Type.STRING)) {
            System.out.println("cannot add string to integer");
            return Type.ERROR;
        }
        return Type.INTEGER;
    }

    @Override
    public Type visitId(KCGParser.IdContext ctx) {
        var decl1 = symbolTable.lookup(ctx.IDENTIFIER().getText(), VarDecl.class);
        if(decl1.isPresent()) {
            return decl1.get().getType();
        }
        var decl2 = symbolTable.lookup(ctx.IDENTIFIER().getText(), ParamDecl.class);
        if(decl2.isPresent()) {
            return decl2.get().getType();
        }
        System.out.println(ctx.IDENTIFIER().getText() + " does not exist in the current context");
        return Type.ERROR;
    }

    @Override
    public Type visitInt(KCGParser.IntContext ctx) {
        return Type.INTEGER;
    }
}
