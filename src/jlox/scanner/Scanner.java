package jlox.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Scanner() {
    
private final String source;

private final List<Token> tokens = new ArrayList<>();

private int start = 0;
private int current = 0;
private int line = 1;


// Lox keywords 
private static final Map<String, TokenType> keywords;


static {
    keywords = new HashMap<>();

    keywords.put("and", TokenType.AND); 
    keywords.put("class", TokenType.CLASS); 
    keywords.put("else", TokenType.ELSE); 
    keywords.put("false", TokenType.FALSE); 
    keywords.put("for", TokenType.FOR); 
    keywords.put("fun", TokenType.FUN); 
    keywords.put("if", TokenType.IF); 
    keywords.put("nil", TokenType.NIL); 
    keywords.put("or", TokenType.OR); 
    keywords.put("print", TokenType.PRINT); 
    keywords.put("return", TokenType.RETURN); 
    keywords.put("super", TokenType.SUPER); 
    keywords.put("this", TokenType.THIS); 
    keywords.put("true", TokenType.TRUE); 
    keywords.put("var", TokenType.VAR); 
    keywords.put("while", TokenType.WHILE);
}


public Scanner(String source) {
    this.source = source;
}


public List<Token>  scanTokens() {
    while(!isAtEnd()){
        start = current;
        scanToken();
    }

    tokens.add(new Token(
        TokenType.EOF,
        "",
        null,
        line
    ));

    return tokens;

}

private void scanToken(){
    char c = advance();

    switch (c) {
        // Single-character tokens

        case '(':
            addToken(TokenType.LEFT_PAREN);
            break;

        case ')':
            addToken(TokenType.RIGHT_PAREN);
            break;
        
        case '{': addToken(TokenType.LEFT_BRACE); 
            break; 
        
        case '}': addToken(TokenType.RIGHT_BRACE); 
            break; 
        
        case ',': addToken(TokenType.COMMA); 
            break; 
        
        case '.': addToken(TokenType.DOT); 
            break;    

        case '-':
            addToken(TokenType.MINUS);
            break;

        case '+':
            addToken(TokenType.PLUS);
            break;

        case ';':
            addToken(TokenType.SEMICOLON);
            break;

        case '*':
            addToken(TokenType.STAR);
            break;

        // One or two characters

        case '!':
            addToken(match('=') 
                ? TokenType.BANG_EQUAL 
                : TokenType.BANG
                 );
            break;

        case '=':
            addToken(match('=')
              ? TokenType.EQUAL_EQUAL
              : TokenType.EQUAL);
            break;

        case '<':
            addToken(match('=')
              ? TokenType.LESS_EQUAL
              : TokenType.LESS);
            break;

        case '>':
             addToken(match('=')
                ? TokenType.GREATER_EQUAL
                : TokenType.GREATER);
            break;
            
        // Slash or comment    

        case '/':
           if (match('/')) {
               while (peek() != '\n' && !isAtEnd()) {
                   advance();
                }
            } else {
               addToken(TokenType.SLASH);
            }
            break;

        // Whitespace   

        case ' ':
        case '\r':
        case '\t':
            // Ignore whitespace.
            break;

        //  New Line  

        case '\n':
            line++;
            break;

        // String 

        case '"':
            string();
            break;


         // Everything else   

        default:

            if (isDigit(c)){
                number();
            }else if (isAlpha(c)) {
                identifier();
            }else{
                System.out.println("Unexpected character at line " + line + ": " + c);
            }                                           

            break; 
    }
}


private char advance(){
    // get character at current
   char charT = source.charAt(current);


    // increase current
    current++;


    // return the character
    return charT;
   }    



private char peek(){
    if(isAtEnd()){
        return '\0';
    }else{ 
    // get character at current
   char charT = source.charAt(current);

    // return the character
    return charT;
    }
}


private char peekNext(){
    if(current + 1 >= source.length()){
        return '\0';
    }else{ 
    // get character at current
   char charT = source.charAt(current + 1);

    // return the character
    return charT;
    }
}



private void addToken(TokenType type,  Object literal){

      String lexeme = source.substring(start,  current);  
    
      Token token =  createToken(type, lexeme, literal, line);
     
      tokens.add(token);
}





private boolean isAtEnd() {
    if (current >= source.length()) {
        return true;
    } else {
        return false;
    }
}



boolean match(char character){
   if(isAtEnd()){
      return false;
   }
   else if(source.charAt(current) == character){
      current++;
      return true;
   }else{
    return false;
   }
}


private void string() {

    while (peek() != '"' && !isAtEnd()) {

        if (peek() == '\n') {
            line++;
        }

        advance();
    }

    if (isAtEnd()) {
        System.out.println("Unterminated string at line " + line);
        return;
    }


    // Consume closing "
    advance();

    // Remove surrounding quotes.
    String value = source.substring(
        start + 1,
        current - 1
    );

    addToken(TokenType.STRING, value);
}


private void number() {

    while (isDigit(peek())) {
        advance();
    }

    // Look for decimal part.
    if (peek() == '.' && isDigit(peekNext())) {
        
        // Consume the .
        advance();

        while (isDigit(peek())) {
            advance();
        }
    }

    Double value = Double.parseDouble(
        source.substring(start, current)
    );

    addToken(TokenType.NUMBER, value);
}


private void identifier() {

    while(isAlphanumeric(peek())) {
        advance();
    }

    String text = source.substring(start, current);

    // For now, everything is an identifier.
    addToken(TokenType.IDENTIFIER); 
}


private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
}

private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
}


private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
}

}




