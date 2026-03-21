import java.util.*;

public class TokenizerTest {
    public static void main(String[] args) {

        String input = "set x = 5";

        Tokenizer tokenizer = new Tokenizer(input); 
        List<Token> tokens = tokenizer.tokenize();   

        tokens.forEach(System.out::println);
    }
}
