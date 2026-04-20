import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException("Token list cannot be null/empty");
        }
        this.tokens = tokens;
        this.pos = 0;
    }

    // Get current token safely
    private Token current() {
        if (pos >= tokens.size()) {
            return tokens.get(tokens.size() - 1); // return EOF safely
        }
        return tokens.get(pos);
    }

    // Match expected token or throw syntax error
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

    // Parse full program
    public List<Instruction> parse() {
        List<Instruction> instructions = new ArrayList<>();
        while (current().getType() != TokenType.EOF) {
            instructions.add(parseInstruction());
        }
        return instructions;
    }

    // Decide which instruction to parse
    private Instruction parseInstruction() {
        Token t = current();

        if (t.getType() == TokenType.SET)  return parseAssign();
        if (t.getType() == TokenType.SHOW) return parsePrint();
        if (t.getType() == TokenType.WHEN) return parseIf();
        if (t.getType() == TokenType.LOOP) return parseLoop();

        throw new RuntimeException("Unknown instruction: '" + t.getValue() + "'");
    }

    // Parse: SET x = expr
    private Instruction parseAssign() {
        consume(TokenType.SET);
        String name = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.EQUALS);
        Expression expr = parseExpression();
        return new AssignInstruction(name, expr);
    }

    // Parse: SHOW expr
    private Instruction parsePrint() {
        consume(TokenType.SHOW);
        Expression expr = parseExpression();
        return new PrintInstruction(expr);
    }

    // // Parse: WHEN condition : instruction
    // private Instruction parseIf() {
    //     consume(TokenType.WHEN);
    //     Expression condition = parseExpression();
    //     consume(TokenType.COLON);

    //     Instruction body = parseInstruction(); // single-line body
    //     return new IfInstruction(condition, body, null);

    // }

    private Instruction parseIf() {
    consume(TokenType.WHEN);

    Expression condition = parseExpression();
    consume(TokenType.COLON);

    // skip newline if present
    if (current().getType() == TokenType.NEWLINE) {
        consume(TokenType.NEWLINE);
    }

    // ✅ THEN (single instruction)
    Instruction thenBranch = parseInstruction();

    // skip newline
    if (current().getType() == TokenType.NEWLINE) {
        consume(TokenType.NEWLINE);
    }

    // ✅ ELSE (optional)
    Instruction elseBranch = null;

    if (current().getType() == TokenType.ELSE) {
        consume(TokenType.ELSE);
        consume(TokenType.COLON);

        if (current().getType() == TokenType.NEWLINE) {
            consume(TokenType.NEWLINE);
        }

        elseBranch = parseInstruction();
    }

    return new IfInstruction(condition, thenBranch, elseBranch);
}

    // Parse: LOOP n : instructions
    private Instruction parseLoop() {
        consume(TokenType.LOOP);

        String numStr = consume(TokenType.NUMBER).getValue();

        int times;
        try {
            double val = Double.parseDouble(numStr);

            if (val % 1 != 0 || val < 0) {
                throw new RuntimeException("Loop count must be a non-negative integer");
            }

            times = (int) val;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid loop count: " + numStr);
        }

        consume(TokenType.COLON);

        List<Instruction> body = new ArrayList<>();

        // ⚠️ FIX: infinite loop bug removed
        while (current().getType() != TokenType.EOF &&
               current().getType() != TokenType.LOOP) { // simple block stop
            body.add(parseInstruction());
        }

        return new RepeatInstruction(times, body);
    }

    // Parse expression with +, -, >, <, ==
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

    // Parse *, /
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

    // Parse literals, variables, parentheses
    private Expression parsePrimary() {
        Token t = current();

        if (t.getType() == TokenType.NUMBER) {
            pos++;
            try {
                return new NumberNode(Double.parseDouble(t.getValue()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid number: " + t.getValue());
            }
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
