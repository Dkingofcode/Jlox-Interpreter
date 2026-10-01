# jlox

A Java implementation of the **Lox programming language**, built from scratch while studying language implementation and interpreter architecture.

The project follows the principles and progression presented in *Crafting Interpreters* by Robert Nystrom, while extending the basic interpreter with a more structured runtime architecture, diagnostics, testing infrastructure, and native-function integration.

## Overview

jlox takes Lox source code through a complete language pipeline:

```text
Lox Source
    │
    ▼
┌───────────┐
│  Scanner  │  Source → Tokens
└─────┬─────┘
      │
      ▼
┌───────────┐
│  Parser   │  Tokens → AST
└─────┬─────┘
      │
      ▼
┌───────────┐
│ Resolver  │  Lexical scope resolution
└─────┬─────┘
      │
      ▼
┌────────────┐
│ Interpreter│  AST → Runtime behavior
└─────┬──────┘
      │
      ▼
   Program Output
```

The implementation is intentionally written in Java without relying on a parser generator or external language-runtime framework.

## Features

### Language features

* Expressions and operators
* Variables and assignment
* Lexical scoping
* Blocks
* `if` / `else`
* `while`
* `for`
* Functions
* Parameters and arguments
* `return`
* Closures
* Classes
* Instances and fields
* Methods
* `this`
* Initializers
* Inheritance
* `super`
* Logical operators with short-circuit evaluation

### Runtime features

* Lexical scope resolution
* Persistent interpreter sessions
* Runtime error handling
* Compile-time diagnostics
* Token-aware error reporting
* Native functions
* Custom native functions
* Custom native-library providers
* Runtime reset support

### Engineering features

* Separate scanner, parser, AST, resolver, and interpreter layers
* Explicit compile/execution pipeline
* Runtime abstraction
* Session abstraction
* Native-library dependency injection
* Dedicated result types
* Regression testing
* Injectable output and error streams
* Isolated test sessions

## Architecture

The project separates language processing from runtime execution.

```text
                    ┌───────────────┐
                    │   Lox Source  │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │    Scanner    │
                    └───────┬───────┘
                            │ Tokens
                            ▼
                    ┌───────────────┐
                    │    Parser     │
                    └───────┬───────┘
                            │ AST
                            ▼
                    ┌───────────────┐
                    │   Resolver    │
                    └───────┬───────┘
                            │ Resolved AST
                            ▼
                    ┌───────────────┐
                    │  LoxRuntime   │
                    │               │
                    │  Interpreter  │
                    └───────┬───────┘
                            │
                            ▼
                         Output
```

The application-facing layer is:

```text
Lox
 │
 ▼
LoxSession
 │
 ├── LoxCompiler
 │
 ├── LoxExecutor
 │
 └── LoxRuntime
       │
       └── Interpreter
```

See [`docs/architecture.md`](docs/architecture.md) for a detailed explanation of the design.

## Project structure

```text
src/
└── jlox/
    ├── Main.java
    ├── Lox.java
    ├── RunResult.java
    ├── CompileResult.java
    ├── ErrorReporter.java
    ├── LoxSession.java
    ├── LoxRuntime.java
    ├── LoxCompiler.java
    ├── LoxExecutor.java
    ├── NativeFunction.java
    ├── NativeLibrary.java
    ├── NativeLibraryProvider.java
    │
    ├── scanner/
    │   ├── Scanner.java
    │   ├── Token.java
    │   └── TokenType.java
    │
    ├── ast/
    │   ├── Expr.java
    │   ├── Stmt.java
    │   └── ...
    │
    ├── parser/
    │   └── Parser.java
    │
    ├── interpreter/
    │   ├── Interpreter.java
    │   ├── Environment.java
    │   ├── Resolver.java
    │   ├── LoxFunction.java
    │   ├── LoxClass.java
    │   ├── LoxInstance.java
    │   ├── LoxCallable.java
    │   └── ...
    │
    └── test/
        ├── LoxTest.java
        └── LoxRegressionTest.java
```

## Example

A simple Lox program:

```lox
fun makeCounter() {
  var count = 0;

  fun increment() {
    count = count + 1;
    print count;
  }

  return increment;
}

var counter = makeCounter();

counter();
counter();
counter();
```

Output:

```text
1
2
3
```

This demonstrates lexical scoping and closures: the returned function continues to access and mutate the variable `count` after `makeCounter()` has returned.

More examples are available in [`examples/`](examples/).

## Error handling

jlox distinguishes between compile-time and runtime failures.

For example:

```lox
var = 123;
```

produces a parser error.

Whereas:

```lox
print 1 + true;
```

produces a runtime error.

Execution results are represented using `RunResult`:

```text
Compile error → hadError = true
Runtime error → hadRuntimeError = true
Success       → both false
```

## Native functions

The runtime supports native functions implemented in Java.

The standard library currently provides:

```lox
clock()
```

Native functions can also receive the active interpreter and their arguments, allowing Java code to participate in the Lox runtime.

The native library is provided through `NativeLibraryProvider`, allowing applications and tests to supply their own native functionality.

## Testing

The project includes a regression test suite covering:

* expressions
* variables
* functions
* closures
* lexical resolution
* classes
* inheritance
* `this`
* `super`
* initializers
* native functions
* parser errors
* resolver errors
* runtime errors
* session persistence
* session reset
* custom native functions
* custom native libraries

Output and error streams can be injected into sessions, allowing tests to verify program output without modifying global `System.out` or `System.err`.

## Running the interpreter

Compile the Java sources and start the interpreter through `Main`.

Interactive mode:

```text
jlox
```

Script mode:

```text
jlox path/to/program.lox
```

The exact compilation command depends on how the project is configured in the local development environment.

## Design goals

The project is primarily an exploration of:

* how programming languages are implemented
* how interpreters evaluate syntax trees
* lexical scope and closures
* static resolution
* runtime environments
* object-oriented language features
* error propagation
* dependency boundaries
* testable runtime architecture

The implementation intentionally favors explicit components and understandable control flow over framework-heavy abstractions.

## Learning reference

The language implementation follows the progression of:

**Crafting Interpreters**
Robert Nystrom

The project should be viewed as an educational implementation and architecture experiment rather than a production language runtime.

## Current status

The interpreter currently supports a substantial subset of the Lox language, including functions, closures, classes, inheritance, lexical resolution, native functions, and structured runtime/session management.

The next stage of the project is focused on making the implementation easier to demonstrate, inspect, and experiment with.
