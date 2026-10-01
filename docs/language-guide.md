# Lox Language Guide

This document provides a quick overview of the Lox features currently supported by jlox.

## Variables

```lox
var name = "Lox";
print name;
```

Variables can be reassigned:

```lox
var count = 1;
count = count + 1;

print count;
```

## Expressions

Lox supports arithmetic and comparison expressions:

```lox
print 1 + 2 * 3;
print 10 > 5;
print "hello" + " world";
```

## Control flow

### If

```lox
if (condition) {
  print "true";
} else {
  print "false";
}
```

### While

```lox
var i = 0;

while (i < 5) {
  print i;
  i = i + 1;
}
```

### For

```lox
for (var i = 0; i < 5; i = i + 1) {
  print i;
}
```

## Functions

```lox
fun greet(name) {
  print "Hello, " + name;
}

greet("Lox");
```

Functions can return values:

```lox
fun multiply(a, b) {
  return a * b;
}

print multiply(6, 7);
```

## Closures

Functions capture variables from their surrounding lexical environment:

```lox
fun makeCounter() {
  var count = 0;

  fun increment() {
    count = count + 1;
    return count;
  }

  return increment;
}

var counter = makeCounter();

print counter();
print counter();
```

Output:

```text
1
2
```

## Classes

Classes can define methods:

```lox
class Person {
  greet() {
    print "Hello";
  }
}

var person = Person();
person.greet();
```

## Fields

Instances can store fields:

```lox
class Person {
  setName(name) {
    this.name = name;
  }

  getName() {
    return this.name;
  }
}

var person = Person();

person.setName("Lox");

print person.getName();
```

## Initializers

A class can define an `init` method:

```lox
class Person {
  init(name) {
    this.name = name;
  }
}

var person = Person("Lox");
```

The initializer is automatically invoked when the instance is constructed.

## Inheritance

A class can inherit from another class:

```lox
class Animal {
  speak() {
    return "animal";
  }
}

class Dog < Animal {
  speak() {
    return "dog";
  }
}
```

## `super`

A subclass can invoke an overridden superclass method:

```lox
class Animal {
  speak() {
    return "animal";
  }
}

class Dog < Animal {
  speak() {
    return super.speak() + " dog";
  }
}

print Dog().speak();
```

## `this`

Methods can refer to their receiving instance:

```lox
class Counter {
  init(value) {
    this.value = value;
  }

  increment() {
    this.value = this.value + 1;
  }
}

var counter = Counter(0);

counter.increment();
```

## Logical operators

Lox supports short-circuiting logical operators:

```lox
print true and "yes";
print false or "fallback";
```

The right-hand expression is evaluated only when necessary.

## Native functions

The standard runtime exposes:

```lox
clock()
```

which provides the current time as a numeric value.

Example:

```lox
print clock();
```

Native functions can also be registered from Java through the runtime's native-function API.

## Errors

The interpreter distinguishes between errors discovered before execution and errors discovered during execution.

For example:

```lox
var = 123;
```

is invalid syntax.

A runtime type error such as:

```lox
print 1 + true;
```

is detected during interpretation.

## Scope

Variables follow lexical scoping rules.

```lox
var value = "global";

{
  var value = "local";
  print value;
}

print value;
```

Output:

```text
local
global
```

The resolver determines which declaration each variable expression refers to before interpretation begins.
