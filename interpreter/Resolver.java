package jlox.interpreter;

import jlox.ast.*;

import java.util.List;

public class Resolver
    implements Expr.Visitor<Void>,
               Stmt.Visitor<Void> {

    private final Interpreter interpreter;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void resolve(
        List<Stmt> statements
    ) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt statement) {
        statement.accept(this);
    }

    private void resolve(Expr expression) {
        expression.accept(this);
    }

    @Override
    public Void visitExpressionStmt(
        Expression stmt
    ) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(
        Print stmt
    ) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(
        Literal expr
    ) {
        return null;
    }

    @Override
public Void visitGroupingExpr(
    Grouping expr
) {
    resolve(expr.expression);
    return null;
}

@Override
public Void visitUnaryExpr(
    Unary expr
) {
    resolve(expr.right);
    return null;
}

@Override
public Void visitBinaryExpr(
    Binary expr
) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
}

@Override
public Void visitLogicalExpr(
    Logical expr
) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
}

@Override
public Void visitCallExpr(
    Call expr
) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
        resolve(argument);
    }

    return null;
}

@Override
public Void visitGetExpr(
    Get expr
) {
    resolve(expr.object);
    return null;
}

@Override
public Void visitSetExpr(
    Set expr
) {
    resolve(expr.value);
    resolve(expr.object);
    return null;
}

@Override
public Void visitVariableExpr(
    Variable expr
) {
    return null;
}

@Override
public Void visitSuperExpr(
    Super expr
) {
    return null;
}

@Override
public Void visitThisExpr(
    Variable expr
) {
    return null;
}

@Override
public Void visitVarStmt(
    Var stmt
) {
    if (stmt.initializer != null) {
        resolve(stmt.initializer);
    }

    return null;
}

@Override
public Void visitBlockStmt(
    Block stmt
) {
    resolve(stmt.statements);
    return null;
}

@Override
public Void visitIfStmt(
    If stmt
) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);

    if (stmt.elseBranch != null) {
        resolve(stmt.elseBranch);
    }

    return null;
}

@Override
public Void visitWhileStmt(
    While stmt
) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
}

@Override
public Void visitFunctionStmt(
    Function stmt
) {
    resolve(stmt.body);
    return null;
}

@Override
public Void visitReturnStmt(
    Return stmt
) {
    if (stmt.value != null) {
        resolve(stmt.value);
    }

    return null;
}

@Override
public Void visitAssignExpr(
    Assign expr
) {
    resolve(expr.value);
    return null;
}

}