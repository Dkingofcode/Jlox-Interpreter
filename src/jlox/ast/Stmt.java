package jlox.ast;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
     
        R visitPrintStmt(Print stmt);
     
        R visitVarStmt(Var stmt);

        R visitIfStmt(If stmt);

        R visitBlockStmt(Block stmt);

        R visitWhileStmt(While stmt);

        R visitFunctionStmt(Function stmt);

        R visitReturnStmt(Return stmt);

        R visitClassStmt(Class stmt);
    }

    public abstract <R> R accept(Visitor<R> visitor);
}