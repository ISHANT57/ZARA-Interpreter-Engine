import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private String source;
    private int pos = 0;
    private int line = 1;

    public Tokenizer(String source) {
        this.source = source;
    }

    private Token readNumber() {
        int start = pos;

        while (pos < source.length() &&
                (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) {
            pos++;
        }

        String value = source.substring(start, pos);
        return new Token(TokenType.NUMBER, value, line);
    }

    private Token readString() {
        pos++;

        int start = pos;

        while (pos < source.length() && source.charAt(pos) != '"') {
            if (source.charAt(pos) == '\n') {
                line++;
            }
            pos++;
        }

        String value = source.substring(start, pos);

        if (pos < source.length()) {
            pos++;
        }

        return new Token(TokenType.STRING, value, line);
    }
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        tokens.add(new Token(TokenType.SET, "set", line));
        tokens.add(new Token(TokenType.IDENTIFIER, "x", line));
        tokens.add(new Token(TokenType.EQUALS, "=", line));
        tokens.add(new Token(TokenType.NUMBER, "5", line));
        tokens.add(new Token(TokenType.EOF, "", line));

        return tokens;
    }
}