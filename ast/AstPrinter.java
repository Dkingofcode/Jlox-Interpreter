package jlox.ast;

import jlox.scanner.Token;

public class AstPrinter implements Expr.Visitor<String> {

    public String print(Expr expr) {

        

        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value == null) {
            return "nil";
        }
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitAssignExpr(Assign expr) {
        return "(= " + expr.name.lexeme + " " + expr.value.accept(this) + ")";
    }

    @Override
    public String visitLogicalExpr(Logical expr) {

        return parenthesize(
            expr.operator.lexeme,
            expr.left,
            expr.right
        );
    }

@Override
public String visitCallExpr(Call expr) {

    return "(call "
        + expr.callee.accept(this)
        + ")";
}

@Override
public String visitGetExpr(Get expr) {
    return "(get "
        + expr.object.accept(this)
        + " "
        + expr.name.lexeme
        + ")";
}

@Override
public String visitSetExpr(Set expr) {
    return "(set "
        + expr.object.accept(this)
        + " "
        + expr.name.lexeme
        + " "
        + expr.value.accept(this)
        + ")";
}



    
    private String parenthesize( String name, Expr... expressions ) {
         StringBuilder builder = new StringBuilder(); 
         builder.append("("); 
         builder.append(name); 
         
         for (Expr expr : expressions) { 
            builder.append(" "); 
            
            builder.append(expr.accept(this)); 
            
         } 
            
            builder.append(")"); 
            
            return builder.toString(); 
            
        } 
        

    
    }
