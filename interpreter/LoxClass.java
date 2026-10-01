package jlox.interpreter;

import jlox.ast.Function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoxClass {

    private final String name;

    private final LoxClass superclass;

    private final Environment closure;

    private final Map<String, LoxFunction> methods =
        new HashMap<>();

    public LoxClass(
    String name,
    LoxClass superclass,
    List<Function> declarations,
    Environment closure
) {
    this.name = name;
    this.superclass = superclass;
    this.closure = closure;

    for (Function declaration : declarations) {
        LoxFunction method =
            new LoxFunction(
                declaration,
                closure,
                declaration.name.lexeme.equals("init")
            );

        methods.put(
            declaration.name.lexeme,
            method
        );
    }
}

    public LoxFunction findMethod(String name) {
    if (methods.containsKey(name)) {
        return methods.get(name);
    }

    if (superclass != null) {
        return superclass.findMethod(name);
    }

    return null;
}

   public LoxInstance call(
    Interpreter interpreter,
    List<Object> arguments
) {
    LoxInstance instance =
        new LoxInstance(this);

    LoxFunction initializer =
        findMethod("init");

    if (initializer != null) {
        initializer
            .bind(instance)
            .call(interpreter, arguments);
    }

    return instance;
}

   public int arity() {
    LoxFunction initializer =
        findMethod("init");

    if (initializer == null) {
        return 0;
    }

    return initializer.arity();
}

    @Override
    public String toString() {
        return name;
    }
}