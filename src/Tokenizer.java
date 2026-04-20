import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private final String source; // input text
    private int pos;             // current index
    private int line;            // current line number

    public Tokenizer(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        // go through whole input
        while (pos < source.length()) {

            char current = source.charAt(pos);

            // skip spaces
            if (Character.isWhitespace(current)) {
                skipSpaces();
                continue;
            }

            // number
            if (Character.isDigit(current)) {
                tokens.add(readNumber());
                continue;
            }

            // string
            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            // identifier / keyword
            if (Character.isLetter(current)) {
                tokens.add(readIdentifierOrKeyword());
                continue;
            }

            // symbols
            tokens.add(readSymbol());
        }

        // end token
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    // skip whitespace and count lines
    private void skipSpaces() {
        while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
            if (source.charAt(pos) == '\n') line++;
            pos++;
        }
    }

    // read number
    private Token readNumber() {
        int start = pos;
        boolean hasDot = false;

        while (pos < source.length()) {
            char c = source.charAt(pos);

            if (Character.isDigit(c)) {
                pos++;
            } 
            else if (c == '.' && !hasDot) {
                hasDot = true;
                pos++;
            } 
            else {
                break;
            }
        }

        String num = source.substring(start, pos);

        // check invalid format
        if (num.startsWith(".") || num.endsWith(".")) {
            throw new RuntimeException("Invalid number at line " + line);
        }

        return new Token(TokenType.NUMBER, num, line);
    }

    // read string
    private Token readString() {
        pos++; // skip "

        StringBuilder sb = new StringBuilder();

        while (pos < source.length()) {
            char c = source.charAt(pos);

            if (c == '"') break;

            // handle escape chars
            if (c == '\\') {
                pos++;

                if (pos >= source.length()) {
                    throw new RuntimeException("Invalid escape at line " + line);
                }

                char next = source.charAt(pos);

                switch (next) {
                    case '"': sb.append('"'); break;
                    case 'n': sb.append('\n'); line++; break;
                    case 't': sb.append('\t'); break;
                    case '\\': sb.append('\\'); break;
                    default:
                        throw new RuntimeException("Unknown escape at line " + line);
                }
            } else {
                if (c == '\n') line++; // track line
                sb.append(c);
            }

            pos++;
        }

        // check if string closed
        if (pos >= source.length() || source.charAt(pos) != '"') {
            throw new RuntimeException("Unterminated string at line " + line);
        }

        pos++; // skip closing "

        return new Token(TokenType.STRING, sb.toString(), line);
    }

    // read identifier or keyword
    private Token readIdentifierOrKeyword() {
        int start = pos;

        while (pos < source.length() &&
                (Character.isLetterOrDigit(source.charAt(pos)) ||
                 source.charAt(pos) == '_')) {
            pos++;
        }

        String word = source.substring(start, pos);

        // check keywords
        switch (word) {
            case "set":
                return new Token(TokenType.SET, word, line);
            case "show":
                return new Token(TokenType.SHOW, word, line);
            case "when":
                return new Token(TokenType.WHEN, word, line);
            case "loop":
                return new Token(TokenType.LOOP, word, line);
                case "else":
    return new Token(TokenType.ELSE, word, line);
            default:
                return new Token(TokenType.IDENTIFIER, word, line);
        }
    }

    // read symbols
    private Token readSymbol() {
        char c = source.charAt(pos);

        switch (c) {
            case '+': pos++; return new Token(TokenType.PLUS, "+", line);
            case '-': pos++; return new Token(TokenType.MINUS, "-", line);
            case '*': pos++; return new Token(TokenType.MULTIPLY, "*", line);
            case '/': pos++; return new Token(TokenType.DIVIDE, "/", line);
            case ':': pos++; return new Token(TokenType.COLON, ":", line);
            case '{': pos++; return new Token(TokenType.LBRACE, "{", line);
            case '}': pos++; return new Token(TokenType.RBRACE, "}", line);
            case '(': pos++; return new Token(TokenType.LPAREN, "(", line);
            case ')': pos++; return new Token(TokenType.RPAREN, ")", line);
            case '>': pos++; return new Token(TokenType.GT, ">", line);
            case '<': pos++; return new Token(TokenType.LT, "<", line);

            case '=':
                if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
                    pos += 2;
                    return new Token(TokenType.EQEQ, "==", line);
                }
                pos++;
                return new Token(TokenType.EQUALS, "=", line);

            case '\n':
                pos++;
                line++;
                return new Token(TokenType.NEWLINE, "\\n", line);

            default:
                // unknown character
                throw new RuntimeException("Unexpected character: " + c + " at line " + line);
        }
    }
}
