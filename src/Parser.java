import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    private Token current() {
        return tokens.get(pos);
    }

    private Token consume(TokenType expected) {
        Token t = current();
        if (t.getType() != expected) {
            throw new RuntimeException(
                "Syntax Error: Expected " + expected +
                " but got '" + t.getValue() + "' (" + t.getType() + ")"
            );
        }
        pos++;
        return t;
    }

    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();
        while (current().getType() != TokenType.EOF) {
            instructions.add(parseInstruction());
        }
        return instructions;
    }

    private Instruction parseInstruction() {
        Token t = current();
        if (t.getType() == TokenType.SET)  return parseAssign();
        if (t.getType() == TokenType.SHOW) return parsePrint();
        if (t.getType() == TokenType.WHEN) return parseIf();
        if (t.getType() == TokenType.LOOP) return parseLoop();
        throw new RuntimeException("Unknown instruction: '" + t.getValue() + "'");
    }

    private Instruction parseAssign() {
        consume(TokenType.SET);
        String name = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.EQUALS);
        Expression expr = parseExpression();
        return new AssignInstruction(name, expr);
    }

    private Instruction parsePrint() {
        consume(TokenType.SHOW);
        Expression expr = parseExpression();
        return new PrintInstruction(expr);
    }

    private Instruction parseIf() {
        consume(TokenType.WHEN);
        Expression condition = parseExpression();
        consume(TokenType.COLON);
        Instruction body = parseInstruction();
        return new IfInstruction(condition, body, null);
    }

    private Instruction parseLoop() {
        consume(TokenType.LOOP);
        int times = (int) Double.parseDouble(consume(TokenType.NUMBER).getValue());
        consume(TokenType.COLON);

        List<Instruction> body = new ArrayList<>();
        while (current().getType() != TokenType.EOF) {
            body.add(parseInstruction());
        }

        return new RepeatInstruction(times, body);
    }

    private Expression parseExpression() {
        Expression left = parseTerm();

        while (current().getType() == TokenType.PLUS ||
               current().getType() == TokenType.MINUS ||
               current().getType() == TokenType.GT ||
               current().getType() == TokenType.LT ||
               current().getType() == TokenType.EQEQ) {

            String op = current().getValue();
            pos++;
            Expression right = parseTerm();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private Expression parseTerm() {
        Expression left = parsePrimary();

        while (current().getType() == TokenType.MULTIPLY ||
               current().getType() == TokenType.DIVIDE) {

            String op = current().getValue();
            pos++;
            Expression right = parsePrimary();
            left = new BinaryOpNode(left, op, right);
        }

        return left;
    }

    private Expression parsePrimary() {
        Token t = current();

        if (t.getType() == TokenType.NUMBER) {
            pos++;
            return new NumberNode(Double.parseDouble(t.getValue()));
        }

        if (t.getType() == TokenType.STRING) {
            pos++;
            return new StringNode(t.getValue());
        }

        if (t.getType() == TokenType.IDENTIFIER) {
            pos++;
            return new VariableNode(t.getValue());
        }

        if (t.getType() == TokenType.LPAREN) {
            pos++;
            Expression expr = parseExpression();
            consume(TokenType.RPAREN);
            return expr;
        }

        throw new RuntimeException("Unexpected token: '" + t.getValue() + "'");
    }
}