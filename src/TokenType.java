public enum TokenType {
 SET, SHOW, WHEN, LOOP, // keywords
 NUMBER, STRING, IDENTIFIER, // values
 PLUS, MINUS, MULTIPLY, DIVIDE, // arithmetic
 EQUALS, EQEQ, GT, LT, // assignment & comparison
 COLON, LBRACE, RBRACE, // block structure
 LPAREN, RPAREN, // grouping
 NEWLINE, EOF // line/file control
}