package jlox.interpreter;

import jlox.ast.Binary;
import jlox.ast.Expr;
import jlox.ast.Grouping;
import jlox.ast.Literal;
import jlox.ast.Unary;
import jlox.ast.Stmt;
import jlox.ast.Expression;
import jlox.ast.Print;
import java.util.List;
import jlox.ast.Var;
import jlox.ast.Assign;
import jlox.ast.Block;
import jlox.ast.If;
import jlox.ast.While;
import jlox.ast.Variable;
import jlox.scanner.Token;
import jlox.scanner.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class Interpreter
    implements Expr.Visitor<Object>, Stmt.Visitor<Void> {


    private Environment environment = new Environment();    

    private final Map<Expr, Integer> locals = new HashMap<>();


    private void execute(Stmt statement) {
        statement.accept(this);
    }

    public void resolve(
        Expr expr,
        int depth
    ){
        locals.put(expr, depth);
    }

    public void interpret(List<Stmt> statements) {

        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {

            System.out.println(
                "Runtime error: "
                + error.getMessage()
            );

            return null;
        }
    }

    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }

    private String stringify(Object object) {

        if (object == null) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();

            if (text.endsWith(".0")) {
                text = text.substring(
                    0,
                    text.length() - 2
                );
            }

            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);

        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(value);
        return null;
    }


    @Override
public Void visitFunctionStmt(Function stmt) {

    LoxFunction function =
        new LoxFunction(stmt, environment, false);

    environment.define(
        stmt.name.lexeme,
        function
    );

    return null;
}

@Override
public Void visitReturnStmt(Return stmt) {

    Object value = null;

    if(stmt.value != null) {
        value = evaluate(stmt.value);
    }

    throw new ReturnException(value);
}

@Override
public Void visitClassStmt(Class stmt) {

    Object superclass = null;

    if (stmt.superclass != null) {
        superclass =
            evaluate(stmt.superclass);

        if (!(superclass instanceof LoxClass)) {
            throw new RuntimeException(
                "Superclass must be a class."
            );
        }
    }

    environment.define(
        stmt.name.lexeme,
        null
    );

    if (stmt.superclass != null) {
        environment =
            new Environment(environment);

        environment.define(
            "super",
            superclass
        );
    }

    LoxClass klass =
        new LoxClass(
            stmt.name.lexeme,
            (LoxClass) superclass,
            stmt.methods,
            environment
        );

    if (stmt.superclass != null) {
        environment =
            environment.enclosing;
    }

    environment.assign(
        stmt.name.lexeme,
        klass
    );

    return null;
}

@Override
public Object visitSuperExpr(Super expr) {

    Object superclass =
        environment.get("super");

    LoxClass superclassClass =
        (LoxClass) superclass;

    LoxFunction method =
        superclassClass.findMethod(
            expr.method.lexeme
        );

    if (method == null) {
        throw new RuntimeException(
            "Undefined property '"
                + expr.method.lexeme
                + "'."
        );
    }

    Object object =
        environment.get("this");

    LoxInstance instance =
        (LoxInstance) object;

    return method.bind(instance);
}


@Override
public Object visitCallExpr(Call expr) {

    Object callee = evaluate(expr.callee);

    if (
        !(callee instanceof LoxFunction)
        && !(callee instanceof LoxClass)
    ) {
        throw new RuntimeException(
            "Can only call functions and classes."
        );
    }

    int arity;

    if (callee instanceof LoxFunction) {
        arity =
            ((LoxFunction) callee).arity();
    } else {
        arity =
            ((LoxClass) callee).arity();
    }

    if (expr.arguments.size() != arity) {
        throw new RuntimeException(
            "Expected "
                + arity
                + " arguments but got "
                + expr.arguments.size()
                + "."
        );
    }

    List<Object> arguments =
        new ArrayList<>();

    for (Expr argument : expr.arguments) {
        arguments.add(
            evaluate(argument)
        );
    }

    if (callee instanceof LoxFunction) {

        return ((LoxFunction) callee).call(
            this,
            arguments
        );

    } else {

        return ((LoxClass) callee).call(
            this,
            arguments
        );
    }
}


public void executeBlock(
    List<Stmt> statements,
    Environment environment
) {
    Environment previous =
        this.environment;

    try {

        this.environment = environment;

        for (Stmt statement : statements) {
            execute(statement);
        }

    } finally {

        this.environment = previous;
    }
}

@Override
public Void visitWhileStmt(While stmt) {

    while (
        isTruthy(
            evaluate(stmt.condition)
        )
    ) {
        execute(stmt.body);
    }

    return null;
}

@Override
public Object visitVariableExpr(Variable expr) {

    Integer distance =
        locals.get(expr);

    if (distance != null) {
        return environment.getAt(
            distance,
            expr.name.lexeme
        );
    }

    return environment.get(
        expr.name.lexeme
    );
}

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;

        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);

        return null;
    }

    @Override
public Void visitIfStmt(If stmt) {

    if (isTruthy(
        evaluate(stmt.condition)
    )) {

        execute(stmt.thenBranch);

    } else if (stmt.elseBranch != null) {

        execute(stmt.elseBranch);
    }

    return null;
}

@Override
public Void visitBlockStmt(Block stmt) {

    for (Stmt statement : stmt.statements) {
        execute(statement);
    }

    return null;
}

   @Override
public Object visitAssignExpr(Assign expr) {

    Object value =
        evaluate(expr.value);

    Integer distance =
        locals.get(expr);

    if (distance != null) {
        environment.assignAt(
            distance,
            expr.name.lexeme,
            value
        );
    } else {
        environment.assign(
            expr.name.lexeme,
            value
        );
    }

    return value;
}

    @Override
public Object visitGetExpr(Get expr) {

    Object object =
        evaluate(expr.object);

    if (object instanceof LoxInstance) {

        return ((LoxInstance) object).get(
            expr.name.lexeme
        );
    }

    throw new RuntimeException(
        "Only instances have properties."
    );
}

@Override
public Object visitSetExpr(Set expr) {

    Object object =
        evaluate(expr.object);

    if (!(object instanceof LoxInstance)) {
        throw new RuntimeException(
            "Only instances have fields."
        );
    }

    Object value =
        evaluate(expr.value);

    ((LoxInstance) object).set(
        expr.name.lexeme,
        value
    );

    return value;
}



    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {

        Object right = evaluate(expr.right);

        switch (expr.operator.type) {

            case MINUS:

                checkNumberOperand(
                    expr.operator,
                    right
                );

                return -(double) right;

            case BANG:
                return !isTruthy(right);
        }

        return null;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {

        Object left =
            evaluate(expr.left);

        Object right =
            evaluate(expr.right);

        switch (expr.operator.type) {

            case MINUS:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    - (double) right;

            case SLASH:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    / (double) right;

            case STAR:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    * (double) right;

            case PLUS:

                if (
                    left instanceof Double
                    && right instanceof Double
                ) {
                    return (double) left
                        + (double) right;
                }

                if (
                    left instanceof String
                    && right instanceof String
                ) {
                    return (String) left
                        + (String) right;
                }

                throw new RuntimeError(
                    expr.operator,
                    "Operands must be two numbers or two strings."
                );

            case GREATER:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    > (double) right;

            case GREATER_EQUAL:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    >= (double) right;

            case LESS:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    < (double) right;

            case LESS_EQUAL:

                checkNumberOperands(
                    expr.operator,
                    left,
                    right
                );

                return (double) left
                    <= (double) right;

            case EQUAL_EQUAL:
                return isEqual(left, right);

            case BANG_EQUAL:
                return !isEqual(left, right);
        }

        return null;
    }

    @Override
public Object visitLogicalExpr(Logical expr) {

    Object left =
        evaluate(expr.left);

    if (expr.operator.type == TokenType.OR) {

        if (isTruthy(left)) {
            return left;
        }

    } else {

        if (!isTruthy(left)) {
            return left;
        }
    }

    return evaluate(expr.right);
}

    private boolean isTruthy(Object object) {

        if (object == null) {
            return false;
        }

        if (object instanceof Boolean) {
            return (boolean) object;
        }

        return true;
    }

    private boolean isEqual(
        Object a,
        Object b
    ) {

        if (a == null && b == null) {
            return true;
        }

        if (a == null) {
            return false;
        }

        return a.equals(b);
    }

    private void checkNumberOperand(
        Token operator,
        Object operand
    ) {

        if (operand instanceof Double) {
            return;
        }

        throw new RuntimeError(
            operator,
            "Operand must be a number."
        );
    }

    private void checkNumberOperands(
        Token operator,
        Object left,
        Object right
    ) {

        if (
            left instanceof Double
            && right instanceof Double
        ) {
            return;
        }

        throw new RuntimeError(
            operator,
            "Operands must be numbers."
        );
    }

    private static class RuntimeError
        extends RuntimeException {

        final Token token;

        RuntimeError(
            Token token,
            String message
        ) {

            super(message);

            this.token = token;
        }
    }
}