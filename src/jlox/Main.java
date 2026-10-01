package jlox;

import jlox.ast.Stmt;
import jlox.interpreter.Interpreter;
import jlox.parser.Parser;
import jlox.scanner.Scanner;
import jlox.scanner.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        String source =
            "print 1 + 2 * 3;";

        Scanner scanner =
            new Scanner(source);

        List<Token> tokens =
            scanner.scanTokens();

        Parser parser =
            new Parser(tokens);

        List<Stmt> statements =
            parser.parse();

        Interpreter interpreter =
    new Interpreter();

Resolver resolver =
    new Resolver(interpreter);

resolver.resolve(statements);

interpreter.interpret(statements);
    }
}