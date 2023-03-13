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
        return super.visitId(ctx);
    }

    @Override
    public List<Instruction> visitInt(KCGParser.IntContext ctx) {
        return List.of(
            new Instruction(Operation.PUSH, InstructionSuffix.QUAD, Integer.parseInt(ctx.DIGIT().getText()))
        );
    }
}
