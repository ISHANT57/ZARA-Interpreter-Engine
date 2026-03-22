import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private final String source;
    private int pos;
    private int line;

    public Tokenizer(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < source.length()) {

            char current = source.charAt(pos);

            if (Character.isWhitespace(current)) {
                skipSpaces();
                continue;
            }

            if (Character.isDigit(current)) {
                tokens.add(readNumber());
                continue;
            }

            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            if (current == '=') {
                tokens.add(new Token(TokenType.EQUALS, "=", line));
                pos++;
                continue;
            }

            if (Character.isLetter(current)) {
                tokens.add(readIdentifier());
                continue;
            }
            pos++;
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private void skipSpaces() {
        while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
            if (source.charAt(pos) == '\n') line++;
            pos++;
        }
    }

    private Token readNumber() {
        int start = pos;
        while (pos < source.length() &&
                (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) {
            pos++;
        }
        return new Token(TokenType.NUMBER, source.substring(start, pos), line);
    }

    private Token readString() {
        pos++;
        int start = pos;
        while (pos < source.length() && source.charAt(pos) != '"') {
            if (source.charAt(pos) == '\n') line++;
            pos++;
        }

        String text = source.substring(start, pos);
        if (pos < source.length()) pos++;
        return new Token(TokenType.STRING, text, line);
    }

    private Token readIdentifier() {
        int start = pos;
        while (pos < source.length() &&
                Character.isLetterOrDigit(source.charAt(pos))) {
            pos++;
        }

        String text = source.substring(start, pos);

        if (text.equals("set")) {
            return new Token(TokenType.SET, text, line);
        }
        if (text.equals("show")) {
            return new Token(TokenType.SHOW, text, line);
        }
        return new Token(TokenType.IDENTIFIER, text, line);
    }

}