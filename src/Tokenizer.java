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
}