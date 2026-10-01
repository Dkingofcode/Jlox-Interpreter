# jlox Architecture

## 1. Overview

jlox is organized as a pipeline that transforms source text into runtime behavior.

```text
Source
  │
  ▼
Scanner
  │
  ▼
Tokens
  │
  ▼
Parser
  │
  ▼
AST
  │
  ▼
Resolver
  │
  ▼
Resolved AST
  │
  ▼
Interpreter
  │
  ▼
Runtime behavior
```

Around this language pipeline is a small application architecture that separates session management, compilation, execution, and runtime state.

```text
Lox
 │
 ▼
LoxSession
 │
 ├───────────────┐
 ▼               ▼
LoxCompiler   LoxExecutor
 │               │
 └───────┐   ┌───┘
         ▼   ▼
       LoxRuntime
           │
           ▼
       Interpreter
```

## 2. Scanner

The scanner converts raw source code into tokens.

For example:

```lox
var x = 10 + 20;
```

becomes conceptually:

```text
VAR
IDENTIFIER("x")
EQUAL
NUMBER(10)
PLUS
NUMBER(20)
SEMICOLON
EOF
```

The scanner is responsible for recognizing lexical structure but does not determine whether the resulting token sequence is syntactically valid.

## 3. Parser

The parser consumes the token stream and produces an Abstract Syntax Tree.

jlox uses recursive descent parsing.

For example:

```lox
1 + 2 * 3
```

is represented structurally so that multiplication binds more tightly than addition.

Conceptually:

```text
        +
       / \
      1   *
         / \
        2   3
```

The AST allows the interpreter to work with program structure rather than raw source text.

## 4. AST

Expressions and statements are represented as separate AST hierarchies.

Expressions include concepts such as:

* literals
* binary expressions
* unary expressions
* variables
* assignments
* calls
* property access
* `this`
* `super`

Statements include:

* expression statements
* variable declarations
* blocks
* conditionals
* loops
* function declarations
* return statements
* class declarations

The visitor pattern is used to traverse these structures.

## 5. Resolver

The resolver performs static analysis before interpretation.

Its primary responsibility is determining lexical scope information.

Consider:

```lox
var a = "global";

{
  fun showA() {
    print a;
  }

  showA();
}
```

The resolver determines which declaration each variable expression refers to.

It records lexical distances so the interpreter can perform efficient environment lookup without repeatedly walking the entire environment chain.

The resolver also validates context-sensitive rules such as:

* returning from top-level code
* using `this` outside a class
* using `super` outside a subclass
* reading a local variable inside its own initializer
* inheriting from the class currently being declared

## 6. Runtime environments

An `Environment` represents a lexical scope.

Environments form a linked chain:

```text
Local Environment
       │
       ▼
Enclosing Environment
       │
       ▼
Global Environment
```

A variable lookup can therefore walk outward through enclosing scopes.

Resolved expressions can additionally use a known lexical distance to access the correct environment directly.

## 7. Functions and closures

Functions capture the environment in which they are declared.

For example:

```lox
fun makeCounter() {
  var count = 0;

  fun increment() {
    count = count + 1;
    print count;
  }

  return increment;
}
```

After `makeCounter()` returns, `increment()` still has access to `count`.

This is implemented by storing the defining environment inside `LoxFunction`.

The resulting relationship is:

```text
LoxFunction
     │
     └── closure ──► Environment
                         │
                         └── count
```

## 8. Classes and objects

Classes implement `LoxCallable`.

Calling a class creates a `LoxInstance`.

```text
LoxClass
   │
   ├── methods
   │
   └── superclass

      │
      ▼

LoxInstance
   │
   ├── fields
   └── class
```

Methods are represented as functions.

When a method is accessed through an instance, the function is bound to that instance so that:

```lox
this
```

refers to the correct object.

## 9. Inheritance

Inheritance is represented through a superclass relationship.

```text
        LoxClass A
             ▲
             │
        LoxClass B
```

Method lookup first checks the current class and then walks the superclass chain.

The `super` expression uses resolver information to locate the appropriate superclass method while preserving the current instance as `this`.

## 10. Initializers

A class can define:

```lox
init(...)
```

The initializer is automatically invoked when an instance is constructed.

Initializers always return the instance itself, even if they contain an explicit return value.

This behavior is handled by `LoxFunction` rather than by the general function-call mechanism.

## 11. Compiler/front-end boundary

`LoxCompiler` owns the source-processing pipeline:

```text
Source
  │
  ▼
Scanner
  │
  ▼
Parser
  │
  ▼
Resolver
  │
  ▼
CompileResult
```

`CompileResult` represents the result of this phase.

It contains:

```text
Resolved AST
Compile error state
```

The compiler does not interpret the program.

## 12. Execution boundary

`LoxExecutor` consumes a `CompileResult`.

```text
CompileResult
      │
      ▼
LoxExecutor
      │
      ▼
LoxRuntime
      │
      ▼
Interpreter
```

This gives the application an explicit separation between:

```text
compilation
```

and:

```text
execution
```

The convenience method:

```java
executor.execute(source);
```

simply performs both phases.

## 13. Runtime ownership

`LoxRuntime` owns the interpreter lifecycle.

It is responsible for:

* creating the interpreter
* installing the native library
* resolving programs
* interpreting programs
* registering native functions
* resetting interpreter state
* providing diagnostic infrastructure

The runtime therefore represents the long-lived execution environment rather than an individual source file.

## 14. Sessions

`LoxSession` represents an application-level interpreter session.

A session owns:

```text
LoxRuntime
LoxExecutor
```

This allows multiple calls to:

```java
session.run(source);
```

to share interpreter state.

For example:

```lox
var x = 10;
```

followed later by:

```lox
print x;
```

works because both programs execute within the same runtime.

Calling:

```java
session.reset();
```

creates a fresh interpreter while preserving the session's configured streams and native-library provider.

## 15. Native functions

Native functions provide an integration boundary between Java and Lox.

The public API is represented by:

```java
NativeFunction
```

and:

```java
NativeLibraryProvider
```

The standard library currently installs:

```lox
clock()
```

The provider abstraction allows applications and tests to install alternative native functionality without coupling the interpreter directly to a specific library implementation.

## 16. Error handling

Errors are separated into two broad categories.

### Compile-time errors

These originate during:

```text
Scanning
Parsing
Resolution
```

They are reported through `ErrorReporter`.

Examples include:

```lox
var = 123;
```

or:

```lox
return 123;
```

at top level.

### Runtime errors

These occur while executing an already-resolved AST.

For example:

```lox
print 1 + true;
```

Runtime errors are represented by:

```text
RuntimeError
```

and converted into a `RunResult`.

## 17. Result objects

The project uses explicit result types.

### CompileResult

Represents:

```text
Compilation succeeded
or
Compilation failed
```

### RunResult

Represents:

```text
Compilation error
Runtime error
Successful execution
```

This avoids requiring callers to inspect global interpreter state.

## 18. Testing architecture

Tests use isolated `LoxSession` instances.

Output and error streams can be injected:

```java
new LoxSession(
    outputStream,
    errorStream
);
```

This makes it possible to test output without replacing global process streams.

The architecture also allows custom native libraries to be injected into tests.

## 19. Design principles

Several principles guide the current architecture:

### Separation of concerns

Scanning, parsing, resolution, interpretation, runtime management, and application session management are separate responsibilities.

### Explicit dependencies

Components receive the objects they need instead of reaching into global state.

### Testability

Streams, native libraries, and runtime sessions can be isolated during tests.

### Persistent state where appropriate

A session can maintain interpreter state across multiple executions.

### Explicit lifecycle

The runtime can be reset intentionally rather than relying on process-global state.

### Small public boundaries

`LoxSession`, `LoxCompiler`, `LoxExecutor`, and `LoxRuntime` each have distinct responsibilities.

## 20. Overall architecture

The resulting system can be viewed as four layers:

```text
┌──────────────────────────────────────┐
│           Application Layer          │
│              Lox / Main              │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│             Session Layer            │
│             LoxSession               │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│          Execution Layer             │
│     LoxCompiler / LoxExecutor        │
└──────────────────┬───────────────────┘
                   │
┌──────────────────▼───────────────────┐
│             Runtime Layer            │
│ LoxRuntime / Interpreter / Environment│
└──────────────────────────────────────┘
```

This separation makes the interpreter easier to test, reason about, extend, and eventually expose through another interface such as a web-based demo.
