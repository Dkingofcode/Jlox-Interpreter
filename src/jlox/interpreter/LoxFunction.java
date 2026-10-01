package jlox.interpreter;

import jlox.ast.Function;
import jlox.ast.Stmt;

import java.util.List;

public class LoxFunction {

    private final Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    public LoxFunction(Function declaration, 
    Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    public Object call(
        Interpreter interpreter,
        List<Object> arguments
    ) {

        Environment environment =
            new Environment(closure);

        for (int i = 0; i < declaration.params.size(); i++) {

            String parameter =
                declaration.params.get(i).lexeme;

            Object argument =
                arguments.get(i);

            environment.define(
                parameter,
                argument
            );
        }

        try {

        interpreter.executeBlock(
            declaration.body,
            environment
        );

        }catch (ReturnException returnValue) {

         if (isInitializer) {
            return closure.get("this");
        }

        return returnValue.value;
    }

    if (isInitializer) {
        return closure.get("this");
    }

        

        return null;

    }

    public int arity() {
        return declaration.params.size();
    }


    public LoxFunction bind(
    LoxInstance instance
) {

    Environment environment =
        new Environment(closure);

    environment.define(
        "this",
        instance
    );

    return new LoxFunction(
        declaration,
        environment,
        isInitializer
    );
}

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}