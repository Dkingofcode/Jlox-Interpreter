package jlox.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> values =
        new HashMap<>();

    public final Environment enclosing;

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(String name) {

        if (values.containsKey(name)) {
            return values.get(name);
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeException(
            "Undefined variable '" + name + "'."
        );
    }

    public Object getAt(
    int distance,
    String name
) {
    return ancestor(distance)
        .values
        .get(name);
}

private Environment ancestor(
    int distance
) {
    Environment environment = this;

    for (int i = 0;
         i < distance;
         i++) {

        environment =
            environment.enclosing;
    }

    return environment;
}

public void assignAt(
    int distance,
    String name,
    Object value
) {
    ancestor(distance)
        .values
        .put(name, value);
}

    public void assign(String name, Object value) {

        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeException(
            "Undefined variable '" + name + "'."
        );
    }
}