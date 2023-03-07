package org.main;

import com.ibm.icu.text.ArabicShaping;
import org.bju.KCG.KCGParser;
import org.main.il.*;

import java.util.ArrayList;
import java.util.List;

public class KCGCodeGenerator extends org.bju.KCG.KCGBaseVisitor<List<Instruction>> {
    private String filename;
    private SymbolTable symbolTable;
    public KCGCodeGenerator(SymbolTable symbolTable, String filename) {
        this.symbolTable = symbolTable;
        this.filename = filename;
    }

    @Override
    public List<Instruction> visitStart(KCGParser.StartContext ctx) {
        return super.visitStart(ctx);
    }

    @Override
    public List<Instruction> visitStatementif(KCGParser.StatementifContext ctx) {
        return super.visitStatementif(ctx);
    }

    @Override
    public List<Instruction> visitStatementwhile(KCGParser.StatementwhileContext ctx) {
        return super.visitStatementwhile(ctx);
    }

    @Override
    public List<Instruction> visitStatementassignment(KCGParser.StatementassignmentContext ctx) {
        return super.visitStatementassignment(ctx);
    }

    @Override
    public List<Instruction> visitStatementexpr(KCGParser.StatementexprContext ctx) {
        return super.visitStatementexpr(ctx);
    }

    @Override
    public List<Instruction> visitStatement_if(KCGParser.Statement_ifContext ctx) {
        return super.visitStatement_if(ctx);
    }

    @Override
    public List<Instruction> visitElse(KCGParser.ElseContext ctx) {
        return super.visitElse(ctx);
    }

    @Override
    public List<Instruction> visitStatement_while(KCGParser.Statement_whileContext ctx) {
        return super.visitStatement_while(ctx);
    }

    @Override
    public List<Instruction> visitStatement_assignment(KCGParser.Statement_assignmentContext ctx) {
        return super.visitStatement_assignment(ctx);
    }

    @Override
    public List<Instruction> visitMethod(KCGParser.MethodContext ctx) {
        return super.visitMethod(ctx);
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

    @Override
    public List<Instruction> visitExprs(KCGParser.ExprsContext ctx) {
        return super.visitExprs(ctx);
    }

    @Override
    public List<Instruction> visitStr(KCGParser.StrContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Instruction> visitMethodcall(KCGParser.MethodcallContext ctx) {
        return super.visitMethodcall(ctx);
    }

    @Override
    public List<Instruction> visitNot(KCGParser.NotContext ctx) {
        return List.of(
            new Pop(Register.RAX),
            new Not(Register.RAX),
            new Push(Register.RAX)
        );
    }

    @Override
    public List<Instruction> visitOr(KCGParser.OrContext ctx) {
        return super.visitOr(ctx);
    }

    @Override
    public List<Instruction> visitAnd(KCGParser.AndContext ctx) {
        return super.visitAnd(ctx);
    }

    @Override
    public List<Instruction> visitMultdiv(KCGParser.MultdivContext ctx) {
        return super.visitMultdiv(ctx);
    }

    @Override
    public List<Instruction> visitTrue(KCGParser.TrueContext ctx) {
        return List.of(
            new Push(1)
        );
    }

    @Override
    public List<Instruction> visitFalse(KCGParser.FalseContext ctx) {
        return List.of(
            new Push(0)
        );
    }

    @Override
    public List<Instruction> visitAddsub(KCGParser.AddsubContext ctx) {
        return super.visitAddsub(ctx);
    }

    @Override
    public List<Instruction> visitId(KCGParser.IdContext ctx) {
        return super.visitId(ctx);
    }

    @Override
    public List<Instruction> visitInt(KCGParser.IntContext ctx) {
        return List.of(
            new Push(Integer.parseInt(ctx.DIGIT().getText()))
        );
    }
}
