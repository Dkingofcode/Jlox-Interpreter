package jlox.interpreter;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {

    private final LoxClass klass;

    private final Map<String, Object> fields =
        new HashMap<>();

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

public Object get(String name) {

    if (fields.containsKey(name)) {
        return fields.get(name);
    }

    LoxFunction method =
        klass.findMethod(name);

    if (method != null) {
        return method.bind(this);
    }

    throw new RuntimeException(
        "Undefined property '"
            + name
            + "'."
    );
}

    public void set(
        String name,
        Object value
    ) {
        fields.put(name, value);
    }

    @Override
    public String toString() {
        return klass + " instance";
    }
}