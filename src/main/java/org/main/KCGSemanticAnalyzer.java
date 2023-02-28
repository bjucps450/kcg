package org.main;

import org.bju.KCG.KCGParser;
import org.main.decl.ParamDecl;
import org.main.decl.VarDecl;

public class KCGSemanticAnalyzer extends org.bju.KCG.KCGBaseVisitor<Type> {

    private SymbolTable symbolTable = new SymbolTable();

    @Override
    public Type visitStart(KCGParser.StartContext ctx) {
        if(ctx.methods != null) {
            ctx.methods.forEach(this::visit);
        }
        if(ctx.statements != null) {
            ctx.statements.forEach(this::visit);
        }
        return Type.BOOL;
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
        return null;
    }

    @Override
    public Type visitMethod(KCGParser.MethodContext ctx) {
        return null;
    }

    @Override
    public Type visitArgs(KCGParser.ArgsContext ctx) {
        return null;
    }

    @Override
    public Type visitArg(KCGParser.ArgContext ctx) {
        return null;
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

    @Override
    public Type visitExprs(KCGParser.ExprsContext ctx) {
        return null;
    }

    @Override
    public Type visitStr(KCGParser.StrContext ctx) {
        return Type.STRING;
    }

    @Override
    public Type visitMethodcall(KCGParser.MethodcallContext ctx) {
        return null;
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
