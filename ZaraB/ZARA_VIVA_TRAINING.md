# 🎓 ZARA Interpreter - COMPLETE VIVA TRAINING GUIDE
## Senior Compiler Design Professor + FAANG-Level Interviewer Preparation

---

# 📚 TABLE OF CONTENTS
1. **Tokenizer.java** - Line-by-line Breakdown
2. **Token.java** - Token Representation
3. **TokenType.java** - Token Type Definitions
4. **Parser.java** - Syntax Analysis & AST Building
5. **Interpreter.java** - Execution Engine
6. **Environment.java** - Variable Storage & Scope
7. **BinaryOpNode.java** - Expression Evaluation
8. **Main.java** - Entry Point
9. **COMPLETE FLOW DIAGRAM**
10. **TOP 5 VIVA QUESTIONS**

---

---

# 🔴 PART 1: TOKENIZER.JAVA

## Line 1-3: Class Declaration & Fields
```java
public class Tokenizer {
    private final String source; // input text
    private int pos;             // current index
    private int line;            // current line number
```

### 🔍 Line-by-Line Explanation

**Line 1**: `public class Tokenizer {`
- **क्या करता है**: यह Tokenizer class को define करता है
- **कौन सी चीज़ है**: ये एक Java class है जो **lexical analysis** करती है
- **अगर हटाएं तो**: Compilation नहीं होगी (Java में class होनी ज़रूरी है)

**Line 2**: `private final String source;`
- **क्या करता है**: Input का code store करता है जिसे tokenize करना है
- **`final` क्यों**: Constructor में assign करते हैं, फिर never change करते हैं
- **अगर हटाएं तो**: Source code को access नहीं कर सकते

**Line 3-4**: `private int pos;` और `private int line;`
- **pos**: वर्तमान character की position (index) बताता है
- **line**: किस line पर हैं - error messages के लिए ज़रूरी है
- **अगर हटाएं तो**: Position track नहीं हो सकती, infinite loop हो सकता है

### 🧠 Concept Behind It
**Tokenization = Lexical Analysis**

Real-life analogy 🎬:
- Imagine आप एक **English sentence** को **words** में तोड़ रहे हो
- "Hello World" → ["Hello", "World"]
- Similarly, `set x = 10 + 5` → [SET, IDENTIFIER("x"), EQUALS, NUMBER(10), PLUS, NUMBER(5)]

**State Management**:
- `pos` = current character का position (pointer की तरह)
- `line` = line tracking (debugging के लिए)

---

## Lines 6-10: Constructor
```java
public Tokenizer(String source) {
    this.source = source;
    this.pos = 0;
    this.line = 1;
}
```

### 🔍 Explanation

**क्या करता है**: Tokenizer को initialize करता है
- Source code को store करता है
- Position को 0 से start करता है (पहला character)
- Line को 1 से start करता है (programmers 1-based numbering करते हैं)

### ⚠️ Edge Cases / Bugs

```java
// ❌ क्या हो अगर source = null हो?
Tokenizer t = new Tokenizer(null);
t.tokenize(); // NullPointerException!

// ✅ Fix करना चाहिए:
public Tokenizer(String source) {
    if (source == null) {
        throw new IllegalArgumentException("Source cannot be null");
    }
    this.source = source;
    this.pos = 0;
    this.line = 1;
}
```

### 💣 Viva Questions

**Q1**: "अगर empty string दो (`""`) तो क्या होगा?"
- **उत्तर**: Constructor successful होगा, लेकिन `tokenize()` method में `while (pos < source.length())` तुरंत false हो जाएगा, एक EOF token return होगा - ये सही है!

**Q2**: "Line counting में bug है क्या?"
- **उत्तर**: \r\n (Windows) को handle नहीं किया जाता। अगर Windows file है तो lines गलत count होंगी। Fix: `skipSpaces()` में सभी whitespace check करें

**Q3**: "Position को 1 से क्यों नहीं start करते?"
- **उत्तर**: Index 0-based होता है in Java, पर lines 1-based होती हैं (user के लिए)

---

## Lines 12-46: Main Tokenize Method
```java
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
```

### 🔍 Detailed Line-by-Line

**Line 13**: `List<Token> tokens = new ArrayList<>();`
- **क्या करता है**: एक खाली list बनाता है जहां tokens store होंगे
- **क्यों ArrayList**: Dynamic size, insertion/removal efficient है
- **अगर Array बनाते**: Size fix होता, सब tokens को पहले count करना पड़ता

**Line 16**: `while (pos < source.length())`
- **क्या करता है**: पूरे source को iterate करता है character-by-character
- **Loop का flow**: 
  - शुरुआत: pos = 0
  - हर iteration में कुछ characters skip होती हैं
  - जब pos >= source.length() तो loop exit

**Lines 17-45: Dispatch Logic**
```
char current = source.charAt(pos);
if (whitespace) → skipSpaces()
if (digit) → readNumber()
if (quote) → readString()
if (letter) → readIdentifierOrKeyword()
else → readSymbol()
```

**यह क्या है**: **Look-ahead mechanism**
- Current character को देखकर decide करता है: "यह किस तरह का token है?"
- Compiler design में इसे **lookahead** कहते हैं

**Line 46**: `tokens.add(new Token(TokenType.EOF, "", line));`
- **EOF = End Of File**
- **क्यों ज़रूरी**: Parser को बताता है "भाई, अब कोई token नहीं है"
- यह एक sentinel value है - parser का loop यहां रुकता है

### 🧠 Concept: DFA (Deterministic Finite Automaton)

Tokenizer एक **state machine** है:

```
START
  ↓
[Check Current Char]
  ↓
  ├─ Whitespace? → Skip → START
  ├─ Digit? → Read Number → Add Token → START
  ├─ Quote? → Read String → Add Token → START
  ├─ Letter? → Read Identifier → Add Token → START
  ├─ Symbol? → Read Symbol → Add Token → START
  └─ EOF? → Add EOF Token → END
```

### ⚠️ Edge Cases

**Case 1**: Empty input
```
Input: ""
Output: [EOF]
✓ Correct - parser handles empty programs
```

**Case 2**: Only whitespace
```
Input: "   \n\n   "
Output: [EOF]
✓ Correct - whitespace is ignored
```

**Case 3**: Consecutive operators
```
Input: "+++-"
Output: [PLUS, PLUS, PLUS, MINUS, EOF]
✓ Correct - each character individually
```

### 💣 Viva Questions

**Q1**: "Tokenizer को input दो: `set  x  =  10` (extra spaces). Output?"
- **Answer**: `[SET, IDENTIFIER(x), EQUALS, NUMBER(10), EOF]`
- **क्यों**: `skipSpaces()` सभी whitespace को ignore करता है

**Q2**: "क्या Tokenizer करता है error handling?"
- **Answer**: हाँ! `readNumber()`, `readString()`, `readSymbol()` में checks हैं
- Example: Unterminated string, invalid escape sequences, unknown characters

**Q3**: "Tokenizer में lookahead कितना है?"
- **Answer**: **1 character lookahead** - siर्फ current character देखता है
- Exception: `==` के लिए 2-character lookahead है (readSymbol में)

---

## Lines 48-54: skipSpaces Method
```java
private void skipSpaces() {
    while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
        if (source.charAt(pos) == '\n') line++;
        pos++;
    }
}
```

### 🔍 Explanation

**क्या करता है**: सभी whitespace (spaces, tabs, newlines) को skip करता है

**Line 49**: `while (pos < source.length() && Character.isWhitespace(...))`
- **Bounds check पहले क्यों**: Short-circuit evaluation - अगर pos >= length तो second condition check नहीं होगा
- **अगर order reverse करो**: ArrayIndexOutOfBoundsException!

**Line 50**: `if (source.charAt(pos) == '\n') line++;`
- **क्यों ज़रूरी**: Line number tracking के लिए
- **Error messages में use होता है**: "Error at line 5"

### 🧠 Concept: Whitespace Skipping in Lexers

**Real-life analogy**: जब आप किसी को सुनते हो, तो **pauses** को ignore करते हो
- "Hello__world" (__ = pause) → "Hello world"
- Similarly, `set  x = 10` → `set x = 10` semantically

### ⚠️ Edge Cases

**Case 1**: Multiple newlines
```java
Input: "set\n\n\nx = 5"
After skipSpaces(): line = 3, pos = first 'x' की position
✓ Correct - line count updated
```

**Case 2**: Tab character
```java
Input: "set\tx = 5"
After skipSpaces(): tab को skip, pos at 'x'
✓ Correct
```

### 💣 Viva Question

**Q1**: "क्या यह code `\r` (carriage return) को handle करता है?"
- **Answer**: हाँ! `Character.isWhitespace()` सभी Unicode whitespace को handle करता है
- लेकिन line counting सिर्फ `\n` के लिए है
- **Bug**: Windows files में `\r\n` है - दोनों count होंगे!

---

## Lines 56-76: readNumber Method
```java
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
```

### 🔍 Explanation

**Line 57**: `int start = pos;`
- **क्या करता है**: Number शुरू होने की position को note करता है
- **क्यों**: बाद में `substring(start, pos)` से पूरा number extract करने के लिए

**Line 58**: `boolean hasDot = false;`
- **क्या करता है**: Check करता है कि एक से ज़्यादा dots नहीं हैं
- **Logic**: `1.2.3` invalid है, लेकिन `1.2` valid है
- **hasDot = true** जब first dot मिले, फिर दूसरा dot आए तो reject करो

**Lines 61-68: Reading digits and decimal point**
```java
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
```

**Flow**:
1. अगर digit है → pos को आगे बढ़ाओ
2. अगर dot है AND पहले dot नहीं आया → dot को allow करो
3. अगर कोई और character → exit करो

**Examples**:
```
"123abc" → "123" (b पर break होता है)
"12.34" → "12.34" (ठीक है)
"12.34.56" → "12.34" (दूसरा dot पर break)
```

**Lines 74-76: Validation**
```java
if (num.startsWith(".") || num.endsWith(".")) {
    throw new RuntimeException("Invalid number at line " + line);
}
```

**क्या check करता है**:
- `.5` invalid है (should be `0.5`)
- `5.` invalid है (should be `5.0`)

### 🧠 Concept: Finite State Machine for Numbers

```
START (at '.')
  ↓
  ├─ Digit → DIGIT_STATE
  ├─ Dot → ERROR (invalid)
  └─ Other → ERROR

DIGIT_STATE (reading digits)
  ├─ Digit → DIGIT_STATE (continue)
  ├─ Dot → DOT_STATE
  └─ Other → END

DOT_STATE (after seeing dot)
  ├─ Digit → DECIMAL_STATE (continue)
  └─ Other → ERROR (trailing dot)

DECIMAL_STATE (digits after dot)
  ├─ Digit → DECIMAL_STATE
  └─ Other → END
```

### ⚠️ Edge Cases

**Case 1**: Scientific notation
```
Input: "1e5" (should be 100000)
Output: "1" token, then "e" unknown character error
❌ Not supported in ZARA
```

**Case 2**: Negative numbers
```
Input: "-5"
Output: MINUS token, then NUMBER(5)
❌ Not a single token! Parser handles this.
```

**Case 3**: Leading zeros
```
Input: "007"
Output: NUMBER(007)
When parsed as Double: 7.0
⚠️ Octal notation not supported
```

### 💣 Viva Questions

**Q1**: "Number parsing में क्या edge case है?"
- **Answer**: 
  - `.5` को reject करता है (leading dot)
  - `5.` को reject करता है (trailing dot)
  - `5.3.2` को `5.3` तक read करके बाकी error दे सकता है

**Q2**: "क्यों `hasDot` boolean का use है?"
- **Answer**: Single dot constraint के लिए
  - `5.3.2` में दूसरा dot आए तो catch करने के लिए
  - `hasDot && c == '.'` → false (दूसरा dot reject हो जाता है)

**Q3**: "क्या यह `Double.parseDouble()` का use नहीं करता?"
- **Answer**: नहीं! Tokenizer सिर्फ **string extraction** करता है
  - `"123"` को token के रूप में store करता है
  - Parser / Interpreter में `Double.parseDouble()` होता है
  - **Separation of concerns** - tokenizer text को normalize नहीं करे

---

## Lines 78-111: readString Method
```java
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
```

### 🔍 Detailed Breakdown

**Line 79**: `pos++;` 
- Skip opening quote
- Example: `"hello"` से शुरू करते हैं, pos opening quote के बाद चला जाता है

**Line 81**: `StringBuilder sb = new StringBuilder();`
- **क्यों StringBuilder**: String concatenation efficient है
- **अगर sb.append() न करो**: हर concat पर नया String बनता है (O(n²) complexity)

**Lines 83-113: Reading string content**

**Line 85**: `if (c == '"') break;`
- **क्या करता है**: Closing quote मिलते ही exit
- **Important**: Escape sequence का part नहीं है (backslash से पहले check नहीं होता)

**Lines 88-107: Escape sequence handling**
```java
if (c == '\\') {
    pos++;
    // ... check bounds and process
    switch (next) {
        case '"': sb.append('"');      // \" → "
        case 'n': sb.append('\n');     // \n → newline
        case 't': sb.append('\t');     // \t → tab
        case '\\': sb.append('\\');    // \\ → \
    }
}
```

**Escape sequences**:
```
\"   → double quote character
\n   → newline (line count भी increase होता है)
\t   → tab character
\\   → backslash character
```

**Example**:
```
Input: "Hello\nWorld"
Output: "Hello" + newline + "World"
```

**Lines 115-119: String termination check**
```java
if (pos >= source.length() || source.charAt(pos) != '"') {
    throw new RuntimeException("Unterminated string at line " + line);
}
```

**क्या check करता है**:
1. अगर EOF तक पहुँच गए (string close नहीं हुई)
2. अगर current char closing quote नहीं है

### 🧠 Concepts

**Escape Sequences**: Character encoding का एक तरीका
- Real-life: "\n" मतलब single character है (newline), दो characters नहीं
- Raw: two bytes `\` + `n`
- Interpreted: one byte `0x0A` (newline)

### ⚠️ Edge Cases / Bugs

**Case 1**: Unterminated string
```
Input: "hello world
Output: RuntimeException - "Unterminated string at line 1"
✓ Correctly caught
```

**Case 2**: Backslash at EOF
```
Input: "hello\
Output: "Invalid escape at line 1"
✓ Correctly caught (pos++ overflow check)
```

**Case 3**: Unknown escape sequence
```
Input: "hello\x"
Output: "Unknown escape at line 1"
⚠️ Good error, but \x को ignore करके continue करना भी option है
```

**Case 4**: Multiline strings
```
Input: "line1
        line2"
Output: Token में दोनों lines, line count updated
✓ Correct - ZARA supports multiline strings
```

### 💣 Viva Questions

**Q1**: "क्यों `line++` दो जगह है (`\n` escape और direct newline)?**
- **Answer**: 
  - Escape sequence `\n` में: explicitly increment
  - Direct newline में: string के अंदर actual newline
  - दोनों cases में line count increase होना चाहिए

**Q2**: "क्या `\"` को properly handle करता है?"
- **Answer**: हाँ!
  ```
  Input: "He said \"Hi\""
  Output: He said "Hi"
  ```
  - Backslash को see करता है, next char `"` को escape मानता है

**Q3**: "अगर unknown escape हो तो क्या करना चाहिए - error या ignore?"
- **Answer**: Current implementation: **error throw करता है** (strict)
  - Alternative: ignore करके continue (lenient)
  - Compiler design में strict बेहतर है (bugs catch हो जाते हैं)

---

## Lines 121-143: readIdentifierOrKeyword Method
```java
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
        default:
            return new Token(TokenType.IDENTIFIER, word, line);
    }
}
```

### 🔍 Explanation

**Lines 123-128: Reading identifier**
```java
while (pos < source.length() &&
        (Character.isLetterOrDigit(source.charAt(pos)) ||
         source.charAt(pos) == '_')) {
    pos++;
}
```

**क्या करता है**: Identifier के सभी characters को read करता है
- Letter: a-z, A-Z
- Digit: 0-9
- Underscore: _

**Examples**:
```
"variable123" → "variable123"
"_count" → "_count"
"x" → "x"
"for-loop" → "for" (hyphen से break होता है)
```

**Lines 131-142: Keyword vs Identifier distinction**
```java
switch (word) {
    case "set": return new Token(TokenType.SET, word, line);
    case "show": return new Token(TokenType.SHOW, word, line);
    case "when": return new Token(TokenType.WHEN, word, line);
    case "loop": return new Token(TokenType.LOOP, word, line);
    default: return new Token(TokenType.IDENTIFIER, word, line);
}
```

**यह क्या करता है**: **Reserved keywords** को identify करता है
- ZARA में 4 keywords हैं: `set`, `show`, `when`, `loop`
- बाकी सभी `IDENTIFIER` हैं

**Examples**:
```
"set" → TokenType.SET
"show" → TokenType.SHOW
"variable" → TokenType.IDENTIFIER
"x" → TokenType.IDENTIFIER
"Set" → TokenType.IDENTIFIER (case-sensitive!)
```

### 🧠 Concept: Reserved Words vs Identifiers

**Real-life analogy**: 
- जैसे English में कुछ words का special meaning है
- "the", "and", "if" - grammars में special role
- Similarly, "set", "show" - ZARA में special role

**Why case-sensitive?**
- Java convention: keywords lowercase हैं
- `Set` != `set`
- Type safety - "Set" को variable नाम दे सकते हो

### ⚠️ Edge Cases

**Case 1**: Keyword as part of word
```
Input: "settings"
Output: IDENTIFIER("settings")
✓ Correct - "set" keyword सिर्फ exact match के लिए
```

**Case 2**: Leading underscore
```
Input: "_var"
Output: IDENTIFIER("_var")
✓ Correct - underscore allowed
```

**Case 3**: Number in identifier
```
Input: "var123"
Output: IDENTIFIER("var123")
✓ Correct - mid-word digit allowed
```

### 💣 Viva Questions

**Q1**: "क्या identifier digit से शुरू हो सकता है?"
- **Answer**: नहीं! `Character.isLetter()` से शुरू होना चाहिए
  - `123var` → NUMBER(123) + IDENTIFIER(var)
  - यह standard compiler convention है

**Q2**: "Reserved words की list में नए word कैसे add करोगे?"
- **Answer**: 
  ```java
  case "else":
      return new Token(TokenType.ELSE, word, line);
  ```
  - `TokenType.enum` में भी ELSE add करना पड़ेगा

**Q3**: "अगर case-sensitive नहीं करते तो?"
- **Answer**: Problem!
  ```
  "SET" → TokenType.SET
  "Set" → TokenType.SET
  "set" → TokenType.SET
  ```
  - सब same मान जाएंगे (bad for variable naming)

---

## Lines 145-180: readSymbol Method
```java
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
            throw new RuntimeException("Unexpected character: " + c + " at line " + line);
    }
}
```

### 🔍 Explanation

**Single-character symbols** (lines 151-161):
```java
case '+': pos++; return new Token(TokenType.PLUS, "+", line);
case '-': pos++; return new Token(TokenType.MINUS, "-", line);
// etc.
```

**क्या करता है**:
1. Character को identify करो
2. Position को आगे बढ़ाओ
3. Token return करो

**Two-character symbol** (lines 163-169):
```java
case '=':
    if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
        pos += 2;
        return new Token(TokenType.EQEQ, "==", line);
    }
    pos++;
    return new Token(TokenType.EQUALS, "=", line);
```

**Logic**:
1. First `=` देखा
2. Next character check करो (bounds check पहले!)
3. अगर दूसरा भी `=` है → `==` token (EQEQ)
4. अगर नहीं → single `=` token (EQUALS)

**Examples**:
```
"=" → EQUALS
"==" → EQEQ
"=5" → EQUALS, then NUMBER(5)
"= =" → EQUALS, EQUALS (space के कारण अलग)
```

**Newline handling** (lines 171-174):
```java
case '\n':
    pos++;
    line++;
    return new Token(TokenType.NEWLINE, "\\n", line);
```

**क्यों separate token?** 
- ZARA syntax में block ending के लिए `:` और newline दोनों use होते हैं
- `when x > 5:` के बाद newline से body शुरू होता है

### 🧠 Concept: Lookahead for Multi-character Operators

```
=  →  Check next char
    ├─ '=' → Two-char operator (==)
    └─ Other → Single-char operator (=)
```

यह **2-character lookahead** है!

### ⚠️ Edge Cases / Bugs

**Case 1**: Lookahead bounds check
```java
// ❌ Bug (अगर check न हो):
if (source.charAt(pos + 1) == '=') {  // pos+1 out of bounds!
    
// ✅ Correct:
if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
```

**Case 2**: Unknown character
```
Input: "#"
Output: RuntimeException - "Unexpected character: # at line 1"
✓ Correct - invalid character caught
```

**Case 3**: Chained comparison (not supported)
```
Input: "===="
Output: EQEQ, EQEQ
⚠️ Okay behavior
```

### 💣 Viva Questions

**Q1**: "क्यों `pos + 1 < source.length()` पहले check करते हो?"
- **Answer**: **Short-circuit evaluation**
  - अगर `pos + 1 >= source.length()` तो दूसरा condition check ही नहीं होगा
  - अगर reverse order हो: `source.charAt(pos + 1) == '=' && pos + 1 < source.length()`
    - ArrayIndexOutOfBoundsException!

**Q2**: "क्या `!=` operator का support है?"
- **Answer**: नहीं! केवल `==`, `>`, `<` हैं
  - अगर `!=` add करना हो तो:
    ```java
    case '!':
        if (pos + 1 < source.length() && source.charAt(pos + 1) == '=') {
            pos += 2;
            return new Token(TokenType.NEQEQ, "!=", line);
        }
        throw new RuntimeException("Unknown operator: !");
    ```

**Q3**: "Newline को separate token क्यों बनाया?"
- **Answer**: Syntax में newline significant है
  - `when x > 5:` के बाद newline से block शुरू होता है
  - Parser में block detection के लिए newline check होता है

---

## 🎨 TOKENIZER SUMMARY (Memory Trick)

```
┌─────────────────────────────────────┐
│  TOKENIZER = DFA (State Machine)    │
│                                     │
│  Input: Source Code String          │
│  Process: Character by character    │
│  Output: List of Tokens             │
│                                     │
│  Key: pos (position), line (tracking)
│                                     │
│  Pattern Recognition:               │
│  - Whitespace → skip                │
│  - Digit → readNumber()             │
│  - Quote → readString()             │
│  - Letter → readIdentifierOrKeyword │
│  - Symbol → readSymbol()            │
│                                     │
│  EOF token: Sentinel value          │
└─────────────────────────────────────┘

Remember: "Three-level filtering"
1. Whitespace ignored
2. Type identified (number, string, etc)
3. Token created with metadata (line)
```

---

---

# 🔵 PART 2: TOKEN.JAVA

```java
public class Token {
    private final TokenType type;
    private final String    value;
    private final int       line;

    public Token(TokenType type, String value, int line) {
        this.type  = type;
        this.value = value;
        this.line  = line;
    }

    public TokenType getType() {
        return type;  
    }
    public String getValue() {
        return value; 
    }
    public int getLine() {
        return line; 
    }

    @Override
    public String toString() {
        return "[" + type + ": \"" + value + "\" (line " + line + ")]";
    }
}
```

### 🔍 Token की Structure

**Line 1**: `public class Token`
- Simple data holder class
- immutable (सभी fields `final`)

**Lines 2-4: Fields**
- **type**: TokenType enum (SET, NUMBER, STRING, etc.)
- **value**: Actual string value ("10", "hello", "set", etc.)
- **line**: Source code की किस line पर है

**Constructor (Lines 6-9)**
- तीनों parameters को assign करता है
- Validation नहीं है (Tokenizer level पर हो चुकी है)

**getters (Lines 11-19)**
- Simple accessor methods

**toString() (Lines 21-23)**
```
[SET: "set" (line 1)]
[NUMBER: "10" (line 1)]
[IDENTIFIER: "x" (line 1)]
```

### 🧠 Concept: Token as Data Structure

Token एक **data transfer object (DTO)** है:
- Tokenizer generates करता है
- Parser consumes करता है
- बीच में information carry करता है

### 💣 Viva Question

**Q1**: "क्यों fields `final` हैं?"
- **Answer**: Immutability
  - Token create हो जाए तो change नहीं होना चाहिए
  - Thread-safe, predictable
  - Bugs कम होते हैं

---

# 🟡 PART 3: TOKENTYPE.JAVA

```java
public enum TokenType {
 SET, SHOW, WHEN, LOOP,           // keywords
 NUMBER, STRING, IDENTIFIER,      // values
 PLUS, MINUS, MULTIPLY, DIVIDE,   // arithmetic
 EQUALS, EQEQ, GT, LT,            // assignment & comparison
 COLON, LBRACE, RBRACE,           // block structure
 LPAREN, RPAREN,                  // grouping
 NEWLINE, EOF                     // line/file control
}
```

### 🔍 TokenType Enum

**Enum advantage**:
- Type-safe (string instead of integers)
- Readable
- Compiler checks unknown types

**Categories**:
1. **Keywords**: SET, SHOW, WHEN, LOOP
2. **Values**: NUMBER, STRING, IDENTIFIER
3. **Operators**: PLUS, MINUS, MULTIPLY, DIVIDE, EQUALS, EQEQ, GT, LT
4. **Delimiters**: COLON, LBRACE, RBRACE, LPAREN, RPAREN
5. **Control**: NEWLINE, EOF

### 💣 Viva Question

**Q1**: "नए TokenType कैसे add करोगे (e.g., ELSE)?"
```java
public enum TokenType {
    // ...
    ELSE,      // Add here
    // ...
}
```

---

---

# 🟢 PART 4: PARSER.JAVA

## Lines 1-14: Class Setup
```java
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
    
    // ...
}
```

### 🔍 Explanation

**Parser क्या है**: Syntax analysis करता है
- Input: Token list (Tokenizer से)
- Output: AST (Abstract Syntax Tree)
- Method: Recursive descent parsing

**Fields**:
- **tokens**: Tokens की list
- **pos**: Current token की position (Tokenizer के `pos` जैसे)

**Validation (Line 5-6)**:
```java
if (tokens == null || tokens.isEmpty()) {
    throw new IllegalArgumentException("Token list cannot be null/empty");
}
```

**क्यों important**:
- Empty token list अधिकतर error है
- Empty program भी valid है (but EOF token तो होगा)
- Safer initialization

### 🧠 Concept: Recursive Descent Parser

```
Parser का flow:
parseProgram()
├── parseInstruction()
│   ├── parseAssign() [SET x = expr]
│   ├── parsePrint() [SHOW expr]
│   ├── parseIf() [WHEN condition: body]
│   ├── parseLoop() [LOOP n: body]
│   └── parseExpression() [arithmetic]
├── parseExpression() [lower precedence]
├── parseTerm() [higher precedence: *, /]
└── parsePrimary() [numbers, strings, vars]

हर method एक grammar rule को handle करता है!
```

---

## Lines 16-22: current() Method
```java
private Token current() {
    if (pos >= tokens.size()) {
        return tokens.get(tokens.size() - 1); // return EOF safely
    }
    return tokens.get(pos);
}
```

### 🔍 Explanation

**क्या करता है**: Current token को safely access करता है

**Bounds checking**:
```java
if (pos >= tokens.size()) {
    return tokens.get(tokens.size() - 1);  // Last token is EOF
}
```

**क्यों**:
- अगर pos out of bounds हो तो EOF return करो
- Parser loops को gracefully handle करता है
- NullPointerException या ArrayIndexOutOfBoundsException नहीं

### 💣 Viva Question

**Q1**: "क्या यह `peek()` operation है?"
- **Answer**: हाँ! Lookahead without consuming
- Token को देखते हो, लेकिन `pos` advance नहीं करते

---

## Lines 24-31: consume() Method
```java
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
```

### 🔍 Explanation

**क्या करता है**: Expected token को check करके advance करता है

**Flow**:
1. Current token निकालो
2. Type match करो
3. अगर match न हो → syntax error
4. अगर match हो → position advance करो
5. Token return करो

**Example**:
```java
consume(TokenType.SET);  // Expect "set" keyword
// यदि current token SET नहीं है:
// RuntimeException: "Syntax Error: Expected SET but got 'show' (SHOW)"
```

### 🧠 Concept: Predictive Parsing

Parser को **पता है** कि अगला कौन सा token आना चाहिए।

Example:
```zara
set x = 10

Parser:
1. parseInstruction() calls parseAssign()
2. consume(SET) ✓
3. consume(IDENTIFIER) expecting 'x' ✓
4. consume(EQUALS) expecting '=' ✓
5. parseExpression() for '10'
```

### ⚠️ Error Messages

**Good error message**:
```
Syntax Error: Expected EQUALS but got '>' (GT) at line 5
```

**Why?**
- Expected what
- Got what
- Line number for debugging

---

## Lines 33-40: parse() - Main Entry Point
```java
public List<Instruction> parse() {
    List<Instruction> instructions = new ArrayList<>();
    while (current().getType() != TokenType.EOF) {
        instructions.add(parseInstruction());
    }
    return instructions;
}
```

### 🔍 Explanation

**क्या करता है**: पूरे program को parse करता है

**Flow**:
1. Empty instruction list बनाओ
2. जब तक EOF न आए, instructions जोड़ते जाओ
3. List return करो

**Example**:
```zara
set x = 5
show x

Tokens: [SET, IDENTIFIER(x), EQUALS, NUMBER(5), NEWLINE, SHOW, IDENTIFIER(x), NEWLINE, EOF]

Instructions generated:
[AssignInstruction(x, NumberNode(5)),
 PrintInstruction(VariableNode(x))]
```

---

## Lines 42-52: parseInstruction() - Dispatcher
```java
private Instruction parseInstruction() {
    Token t = current();

    if (t.getType() == TokenType.SET)  return parseAssign();
    if (t.getType() == TokenType.SHOW) return parsePrint();
    if (t.getType() == TokenType.WHEN) return parseIf();
    if (t.getType() == TokenType.LOOP) return parseLoop();

    throw new RuntimeException("Unknown instruction: '" + t.getValue() + "'");
}
```

### 🔍 Explanation

**क्या करता है**: Token के type के आधार पर सही parser method को call करता है

**यह एक dispatch है**:
```
if SET → parseAssign
if SHOW → parsePrint
if WHEN → parseIf
if LOOP → parseLoop
else → error
```

**बिना consume किए**:
- `current()` peek करता है
- `parseAssign()` आदि खुद consume करेंगे

---

## Lines 54-60: parseAssign()
```java
private Instruction parseAssign() {
    consume(TokenType.SET);
    String name = consume(TokenType.IDENTIFIER).getValue();
    consume(TokenType.EQUALS);
    Expression expr = parseExpression();
    return new AssignInstruction(name, expr);
}
```

### 🔍 Explanation

**Grammar rule**:
```
assignment := SET IDENTIFIER EQUALS expression
```

**Parsing steps**:
1. `consume(SET)` - Expect "set" keyword
2. `consume(IDENTIFIER)` - Get variable name
3. `consume(EQUALS)` - Expect "=" operator
4. `parseExpression()` - Parse right-hand side
5. Create AssignInstruction

**Example**:
```zara
set x = 10 + 5

Tokens: [SET, IDENTIFIER(x), EQUALS, NUMBER(10), PLUS, NUMBER(5)]

1. consume(SET) → pos = 1
2. consume(IDENTIFIER) → name = "x", pos = 2
3. consume(EQUALS) → pos = 3
4. parseExpression() → BinaryOpNode(NumberNode(10), "+", NumberNode(5))
5. return AssignInstruction("x", expr)
```

### 🧠 Concept: Grammar Rules

```
Program := Instruction*
Instruction := Assignment | Print | If | Loop
Assignment := "set" IDENTIFIER "=" Expression
Print := "show" Expression
If := "when" Expression ":" Instruction
Loop := "loop" NUMBER ":" Instruction*
Expression := Term (("+"|"-"|">"|"<"|"==") Term)*
Term := Primary (("*"|"/") Primary)*
Primary := NUMBER | STRING | IDENTIFIER | "(" Expression ")"
```

---

## Lines 62-66: parsePrint()
```java
private Instruction parsePrint() {
    consume(TokenType.SHOW);
    Expression expr = parseExpression();
    return new PrintInstruction(expr);
}
```

### 🔍 Simple और straightforward

```
show 10 + 5

1. consume(SHOW) ✓
2. parseExpression() → BinaryOpNode(...)
3. return PrintInstruction(expr)
```

---

## Lines 68-74: parseIf()
```java
private Instruction parseIf() {
    consume(TokenType.WHEN);
    Expression condition = parseExpression();
    consume(TokenType.COLON);

    Instruction body = parseInstruction(); // single-line body
    return new IfInstruction(condition, body, null);
}
```

### 🔍 Explanation

**Grammar**:
```
if := "when" Expression ":" Instruction
```

**Note**: ZARA का `if` single-line है (no multi-line blocks)

**Example**:
```zara
when score > 50:
    show "Pass"

1. consume(WHEN) ✓
2. parseExpression() → BinaryOpNode(VariableNode(score), ">", NumberNode(50))
3. consume(COLON) ✓
4. parseInstruction() → PrintInstruction(StringNode("Pass"))
5. return IfInstruction(condition, body, null)
```

**Note**: `null` के लिए else clause नहीं है (अभी supported नहीं)

---

## Lines 76-98: parseLoop()
```java
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

    while (current().getType() != TokenType.EOF &&
           current().getType() != TokenType.LOOP) {
        body.add(parseInstruction());
    }

    return new RepeatInstruction(times, body);
}
```

### 🔍 Detailed Breakdown

**Line 77**: `consume(TokenType.LOOP);`
- "loop" keyword को consume करो

**Line 79**: `String numStr = consume(TokenType.NUMBER).getValue();`
- Number को string के रूप में निकालो
- अभी parse नहीं करते (बाद में double conversion)

**Lines 82-91: Number Parsing and Validation**
```java
double val = Double.parseDouble(numStr);

if (val % 1 != 0 || val < 0) {
    throw new RuntimeException("Loop count must be a non-negative integer");
}

times = (int) val;
```

**क्या check करता है**:
1. Number को double में convert करो
2. अगर decimal है (e.g., 4.5) → error
3. अगर negative है → error
4. अगर valid है → integer में convert करो

**Examples**:
```
"4" → 4 (valid)
"4.0" → 4 (valid, decimal point नहीं है)
"4.5" → Error (decimal है)
"-1" → Error (negative है)
```

**Line 93**: `consume(TokenType.COLON);`
- ":" को expect करो

**Lines 95-98: Loop Body Parsing**
```java
List<Instruction> body = new ArrayList<>();

while (current().getType() != TokenType.EOF &&
       current().getType() != TokenType.LOOP) {
    body.add(parseInstruction());
}
```

**Logic**:
- Loop के अंदर के instructions को read करो
- जब तक EOF या दूसरा LOOP न आए

**Issue**: यह simple है, nested loops को properly handle नहीं करता
- Comment में कहा है: "⚠️ FIX: infinite loop bug removed"

### 🧠 Concept: Indentation vs Explicit Delimiters

ZARA में:
```
loop 4:
    show i      ← Implicit block (indentation नहीं check होता)
    set i = i + 1
```

पर parser में explicit ":" है
- Python जैसे indentation-based नहीं है
- Block ending: next `loop` या EOF

### ⚠️ Edge Cases / Bugs

**Bug 1**: Nested loops
```zara
loop 2:
    set i = 1
    loop 3:
        show i

Parser का flow:
1. parseLoop() → consume(LOOP), times=2
2. body.add(parseInstruction()) → AssignInstruction
3. body.add(parseInstruction()) → parseLoop() (recursive!)
4. Inner parseLoop() → body में instructions
5. Outer loop का while: current() = ??? (nested loop के बाद क्या?)
```

**Issue**: Nested loops properly handle नहीं हो सकते current logic से

**Bug 2**: Empty loop
```zara
loop 3:
show "Done"

Parser:
1. times = 3
2. While loop: current = SHOW (not EOF, not LOOP)
3. body.add(parseInstruction()) → PrintInstruction
4. While loop: current = EOF → exit
5. RepeatInstruction(3, [PrintInstruction])
✓ Correct!
```

**Bug 3**: Missing colon
```zara
loop 3
    show i

Parser:
1. times = 3
2. consume(COLON) → RuntimeException!
✓ Caught
```

### 💣 Viva Questions

**Q1**: "Loop count को float दो (3.5), क्या होगा?"
- **Answer**: Error!
  ```java
  3.5 % 1 = 0.5 ≠ 0 → true
  throw new RuntimeException("Loop count must be a non-negative integer")
  ```

**Q2**: "Nested loops को handle करने के लिए क्या change करोगे?"
- **Answer**: 
  ```java
  // Current (wrong):
  while (current().getType() != TokenType.EOF &&
         current().getType() != TokenType.LOOP)
  
  // Better (indentation track करके):
  // या explicit block delimiter (e.g., {})
  // या indentation level check
  ```

**Q3**: "क्यों `double` conversion करते हो अगर सिर्फ `int` चाहिए?"
- **Answer**: Number tokenizer सभी numbers को "123" string के रूप में दे सकता है
  - `"3.0"` को double parse करके `3` integer बनाते हो
  - Flexibility के लिए

---

## Lines 100-115: parseExpression()
```java
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
```

### 🔍 Explanation

**Grammar** (Operator Precedence):
```
Expression := Term (("+"|"-"|">"|"<"|"==") Term)*
```

**Precedence Rules**:
- Addition/Subtraction: **lower precedence** (parsed first, executed last)
- Multiplication/Division: **higher precedence** (parsed later, executed first)

**Example**: `2 + 3 * 4`
- Precedence order: `*` > `+`
- Execution: `(2 + (3 * 4))` = 14, not `((2 + 3) * 4)` = 20

**Parsing strategy** (Left-recursive elimination):
```
Instead of:
Expression := Term "+" Expression  ← Right-recursive (left-associative problem)

We use:
Expression := Term (("+" | "-") Term)*  ← Iterative (left-associative)
```

**Example Walk**: `5 + 3 - 2`
```
1. left = parseTerm() → NumberNode(5)
2. current = PLUS (+)
   - op = "+"
   - right = parseTerm() → NumberNode(3)
   - left = BinaryOpNode(5, "+", 3)
3. current = MINUS (-)
   - op = "-"
   - right = parseTerm() → NumberNode(2)
   - left = BinaryOpNode(BinaryOpNode(5, "+", 3), "-", 2)
4. return left
```

**AST**:
```
       -
      / \
     +   2
    / \
   5   3
```

**Evaluation order** (in-order traversal):
- `((5 + 3) - 2)` = `6` ✓

### 🧠 Concept: Operator Precedence Climbing

```
Lowest Precedence:  + - > < ==
                    * /
Highest Precedence: (literals, variables, parentheses)
```

**Grammar hierarchy**:
```
Expression      ← lowest precedence (addition/subtraction)
  └─ Term       ← higher precedence (multiplication/division)
      └─ Primary  ← highest precedence (atoms)
```

### ⚠️ Edge Cases

**Case 1**: Mixed operators
```
5 + 3 * 2 - 1

Expected: (5 + (3 * 2)) - 1 = 10
Parser walk:
1. parseExpression(): left = parseTerm() → 5
2. op = "+", right = parseTerm() → 3 * 2 = 6
3. left = 5 + 6 = 11
4. op = "-", right = parseTerm() → 1
5. left = 11 - 1 = 10
✓ Correct!
```

**Case 2**: Comparison in expression
```
5 > 3 + 2

Expected: 5 > (3 + 2) = false
Parser walk:
1. left = parseTerm() → 5
2. op = ">", right = parseTerm() → 3 + 2
Wait! parseTerm() सिर्फ multiplication/division करता है
parseTerm() में addition नहीं है!

Actual behavior:
1. left = parseTerm() → 5
2. op = ">", right = parseTerm() → 3
3. left = BinaryOpNode(5, ">", 3) = true
4. op = "+", right = parseTerm() → 2
5. left = BinaryOpNode(true, "+", 2)
// Error during evaluation! Type mismatch

BUG: Comparison operators should have lower precedence than arithmetic!
```

### 💣 Viva Questions

**Q1**: "Precedence को गलत करे (= - के साथ), क्या होगा?"
- **Answer**: Wrong order of operations
  ```
  5 - 3 + 2
  
  If - parsed first: (5 - 3) + 2 = 4 ✓ (left-associative, correct)
  If + parsed first: 5 - (3 + 2) = 0 ✗ (right-associative, wrong)
  ```

**Q2**: "Comparison operators का precedence क्या होना चाहिए?"
- **Answer**: **Lower than arithmetic**
  ```
  Current (WRONG):
  parseExpression() handles +, -, >, <, ==
  parseTerm() handles *, /
  
  Correct should be:
  parseComparison() handles >, <, ==
    └─ parseAddition() handles +, -
        └─ parseTerm() handles *, /
  ```

---

## Lines 117-130: parseTerm()
```java
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
```

### 🔍 Explanation

**Same pattern as parseExpression, but for multiplication/division**

**Grammar**:
```
Term := Primary (("*"|"/") Primary)*
```

**Example**: `2 * 3 / 4`
```
1. left = parsePrimary() → 2
2. op = "*", right = parsePrimary() → 3
   left = BinaryOpNode(2, "*", 3)
3. op = "/", right = parsePrimary() → 4
   left = BinaryOpNode(BinaryOpNode(2, "*", 3), "/", 4)
4. return left
```

**AST**:
```
     /
    / \
   *   4
  / \
 2   3
```

**Evaluation**: `((2 * 3) / 4)` = 1.5 ✓

---

## Lines 132-166: parsePrimary()
```java
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
```

### 🔍 Explanation

**Grammar**:
```
Primary := NUMBER | STRING | IDENTIFIER | "(" Expression ")"
```

**यह सबसे low-level expression handling है**

**Case 1: NUMBER**
```
"10" → NumberNode(10.0)
```

**Case 2: STRING**
```
"Hello" → StringNode("Hello")
```

**Case 3: IDENTIFIER**
```
"x" → VariableNode("x")
```

**Case 4: PARENTHESES (Recursion!)**
```
"(2 + 3)" 
→ parsePrimary() sees LPAREN
→ parseExpression() recursively
→ parseExpression() returns BinaryOpNode(2, "+", 3)
→ consume(RPAREN)
→ return BinaryOpNode(2, "+", 3)
```

**This allows nested expressions!**

### 🧠 Concept: Recursion in Parsing

```
parseExpression()
  └─ parseTerm()
      └─ parsePrimary()
          └─ if LPAREN: parseExpression() [recursion!]
```

यह allow करता है:
- `((2 + 3) * (4 + 5))`
- arbitrary nesting

### ⚠️ Edge Cases

**Case 1**: Unmatched parentheses
```
"(2 + 3"

Parser:
1. parsePrimary() sees LPAREN
2. parseExpression() → 2 + 3
3. consume(RPAREN) → current() is EOF
4. RuntimeException: "Syntax Error: Expected RPAREN but got 'EOF'"
✓ Caught
```

**Case 2**: Empty parentheses
```
"()"

Parser:
1. parsePrimary() sees LPAREN
2. parseExpression() → current() is RPAREN
3. parseExpression()→ parseTerm() → parsePrimary()
4. parsePrimary() sees RPAREN
5. RuntimeException: "Unexpected token: ')'"
✓ Caught
```

### 💣 Viva Questions

**Q1**: "Parentheses को दो बार `pos++` क्यों होता है?"
- **Answer**:
  ```java
  // Line 149: LPAREN को consume करने के लिए
  pos++;
  
  // Line 151: consume(RPAREN) में दूसरी बार
  pos++;
  ```

**Q2**: "Negative numbers को कैसे support करोगे?"
- **Answer**:
  ```java
  if (t.getType() == TokenType.MINUS) {
      pos++;
      Expression operand = parsePrimary();
      return new UnaryOpNode("-", operand);  // Unary minus
  }
  ```

---

## 🎨 PARSER SUMMARY

```
┌─────────────────────────────────────────────────┐
│  PARSER = Recursive Descent Parser              │
│                                                 │
│  Input: List<Token>                             │
│  Output: List<Instruction>                      │
│                                                 │
│  Strategy: Top-down, predictive parsing         │
│                                                 │
│  Grammar Hierarchy:                             │
│  parse() → parseInstruction()                   │
│      ├─ parseAssign()                           │
│      ├─ parsePrint()                            │
│      ├─ parseIf()                               │
│      └─ parseLoop()                             │
│           └─ parseExpression()                  │
│                └─ parseTerm()                   │
│                     └─ parsePrimary()           │
│                          └─ (recursion)        │
│                                                 │
│  Key: Operator precedence via method nesting   │
│  Left-associativity via iterative loops        │
└─────────────────────────────────────────────────┘
```

---

---

# 🟣 PART 5: INTERPRETER.JAVA

```java
public class Interpreter {

    public void run(String sourceCode) {

        // Validate input source
        if (sourceCode == null || sourceCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Source code cannot be null or empty");
        }

        try {
            // Step 1: Tokenize
            Tokenizer tokenizer = new Tokenizer(sourceCode);
            List<Token> tokens = tokenizer.tokenize();

            if (tokens == null || tokens.isEmpty()) {
                throw new IllegalStateException("Tokenization failed: no tokens generated");
            }

            // Step 2: Parse
            Parser parser = new Parser(tokens);
            List<Instruction> instructions = parser.parse();

            if (instructions == null) {
                throw new IllegalStateException("Parsing failed: instructions are null");
            }

            // Step 3: Execute
            Environment env = new Environment();

            for (Instruction instr : instructions) {
                if (instr == null) {
                    throw new IllegalStateException("Encountered null instruction");
                }

                instr.execute(env);
            }

        } catch (RuntimeException e) {
            // Centralized error handling
            throw new RuntimeException("Interpreter Error: " + e.getMessage(), e);
        }
    }
}
```

### 🔍 Line-by-Line

**Line 4**: `public void run(String sourceCode)`
- Entry point
- No return value (side effect: prints output)

**Lines 7-9: Input validation**
```java
if (sourceCode == null || sourceCode.trim().isEmpty()) {
    throw new IllegalArgumentException("Source code cannot be null or empty");
}
```

**क्यों ज़रूरी**:
- Null source → NPE in Tokenizer
- Empty source → valid (but usually error)
- Whitespace-only → also treated as empty

**Line 12-18: Tokenization Phase**
```java
Tokenizer tokenizer = new Tokenizer(sourceCode);
List<Token> tokens = tokenizer.tokenize();

if (tokens == null || tokens.isEmpty()) {
    throw new IllegalStateException("Tokenization failed: no tokens generated");
}
```

**Two-stage check**:
1. tokens null? (shouldn't happen, but defensive)
2. tokens empty? (valid in ZARA, but typically error for non-empty source)

**Line 21-25: Parsing Phase**
```java
Parser parser = new Parser(tokens);
List<Instruction> instructions = parser.parse();

if (instructions == null) {
    throw new IllegalStateException("Parsing failed: instructions are null");
}
```

**Same defensive checks**

**Line 28-37: Execution Phase**
```java
Environment env = new Environment();

for (Instruction instr : instructions) {
    if (instr == null) {
        throw new IllegalStateException("Encountered null instruction");
    }

    instr.execute(env);
}
```

**क्या होता है**:
1. Empty environment (no variables yet)
2. हर instruction को execute करो
3. Environment update होता रहता है

**Line 39-42: Error handling**
```java
catch (RuntimeException e) {
    throw new RuntimeException("Interpreter Error: " + e.getMessage(), e);
}
```

**सभी errors को wrap करता है** ("Interpreter Error" prefix के साथ)

### 🧠 Concept: Three-Phase Interpreter

```
┌─────────────────────────────────────────────┐
│ INTERPRETER PIPELINE                        │
│                                             │
│ Source Code                                 │
│      ↓                                       │
│ ┌─────────────────────────────────────────┐ │
│ │ PHASE 1: TOKENIZATION (Lexical Analysis)│ │
│ │ ────────────────────────────────────────│ │
│ │ Input: String                           │ │
│ │ Process: Break into tokens              │ │
│ │ Output: List<Token>                     │ │
│ │ Tool: Tokenizer                         │ │
│ └─────────────────────────────────────────┘ │
│      ↓                                       │
│ ┌─────────────────────────────────────────┐ │
│ │ PHASE 2: PARSING (Syntax Analysis)      │ │
│ │ ────────────────────────────────────────│ │
│ │ Input: List<Token>                      │ │
│ │ Process: Build AST                      │ │
│ │ Output: List<Instruction> (AST)         │ │
│ │ Tool: Parser                            │ │
│ └─────────────────────────────────────────┘ │
│      ↓                                       │
│ ┌─────────────────────────────────────────┐ │
│ │ PHASE 3: EXECUTION (Interpretation)     │ │
│ │ ────────────────────────────────────────│ │
│ │ Input: List<Instruction>                │ │
│ │ Process: Execute each instruction       │ │
│ │ Output: Side effects (printed output)   │ │
│ │ Tool: Interpreter (with Environment)    │ │
│ └─────────────────────────────────────────┘ │
│      ↓                                       │
│ Console Output                              │
│                                             │
└─────────────────────────────────────────────┘
```

### 💣 Viva Questions

**Q1**: "तीनों phases को अलग क्यों रखते हो? एक-साथ नहीं कर सकते?"
- **Answer**: Yes, एक-साथ कर सकते हो, पर फायदे:
  - **Separation of concerns**: हर phase independent है
  - **Error reporting**: कौन-सा phase fail हुआ, पता चल जाता है
  - **Optimization**: Parsing के बाद optimization pass add कर सकते हो
  - **Code reuse**: Multiple programs के लिए phases reuse करो

**Q2**: "Defensive checks क्यों हैं (`null` checks)?"
- **Answer**: 
  - Tokenizer fail हो सकता है (invalid input)
  - Parser fail हो सकता है (syntax error)
  - हर phase के output को validate करो
  - Production code में ज़रूरी है

---

---

# 🔴 PART 6: ENVIRONMENT.JAVA

```java
import java.util.HashMap;
import java.util.Map;

public class Environment {

    private final Map<String, Object> variables = new HashMap<>();

    public void set(String name, Object value) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Invalid variable name");
        }

        if (value == null) {
            throw new RuntimeException("Cannot assign null to variable: " + name);
        }

        variables.put(name, value);
    }

    public Object get(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Invalid variable name");
        }

        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not defined: " + name);
        }

        return variables.get(name);
    }

    public boolean has(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        return variables.containsKey(name);
    }
}
```

### 🔍 Line-by-Line

**Line 6**: `private final Map<String, Object> variables = new HashMap<>();`
- **String**: Variable name
- **Object**: Value (could be Double, String, Boolean)

**Why HashMap?**
- O(1) average lookup
- Dynamic size

### 🧠 Concept: Symbol Table / Environment

Environment एक **symbol table** है:

```
Variable Name → Value
───────────────────────
x             → 10.0
y             → 3.5
name          → "Alice"
score         → 85.0
```

### set() Method (Lines 8-18)

```java
public void set(String name, Object value) {
    if (name == null || name.trim().isEmpty()) {
        throw new RuntimeException("Invalid variable name");
    }

    if (value == null) {
        throw new RuntimeException("Cannot assign null to variable: " + name);
    }

    variables.put(name, value);
}
```

**Validation**:
1. Name not null/empty
2. Value not null

**Example**:
```
env.set("x", 10.0);  // ✓
env.set("x", null);  // ✗ Error
env.set("", 5.0);    // ✗ Error
```

### get() Method (Lines 20-30)

```java
public Object get(String name) {
    if (name == null || name.trim().isEmpty()) {
        throw new RuntimeException("Invalid variable name");
    }

    if (!variables.containsKey(name)) {
        throw new RuntimeException("Variable not defined: " + name);
    }

    return variables.get(name);
}
```

**Two checks**:
1. Name validity
2. Variable existence

**Example**:
```
env.get("x");  // ✓ 10.0
env.get("y");  // ✗ "Variable not defined: y"
```

### has() Method (Lines 32-38)

```java
public boolean has(String name) {
    if (name == null || name.trim().isEmpty()) {
        return false;
    }

    return variables.containsKey(name);
}
```

**Returns boolean** instead of throwing error
- Useful for optional checking

### 💣 Viva Questions

**Q1**: "Value क्यों `Object` है, अगर सिर्फ Double, String, Boolean?"
- **Answer**: 
  - Future extensibility
  - Any type store कर सकते हो
  - Type checking at runtime

**Q2**: "Null value को allow क्यों नहीं करते?"
- **Answer**:
  - undefined variable vs null variable - confusing
  - ZARA में null concept नहीं है
  - Uninitialized variables को detect करना आसान

---

---

# 🟠 PART 7: BINARYOPNODE.JAVA

```java
public class BinaryOpNode implements Expression {

    private final Expression left;
    private final Expression right;
    private final String op;

    public BinaryOpNode(Expression left, String op, Expression right) {
        if (left == null || right == null || op == null) {
            throw new RuntimeException("Invalid Binary Operation");
        }
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Object evaluate(Environment env) {
        if (env == null) {
            throw new RuntimeException("Environment is null");
        }

        Object l = left.evaluate(env);
        Object r = right.evaluate(env);

        if (l == null || r == null) {
            throw new RuntimeException("Null operand for operator '" + op + "'");
        }

        // Number operations
        if (l instanceof Double && r instanceof Double) {
            double ld = (Double) l;
            double rd = (Double) r;

            switch (op) {
                case "+": return ld + rd;
                case "-": return ld - rd;
                case "*": return ld * rd;

                case "/":
                    if (rd == 0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return ld / rd;

                case ">": return ld > rd;
                case "<": return ld < rd;
                case "==": return ld == rd;

                default:
                    throw new RuntimeException("Unknown operator: " + op);
            }
        }

        // String / mixed concatenation
        if (op.equals("+")) {
            return l.toString() + r.toString();
        }

        // Equality check
        if (op.equals("==")) {
            return l.equals(r);
        }

        // Final fallback error
        throw new RuntimeException(
            "Type error: cannot apply '" + op + "' to " + l + " and " + r
        );
    }
} 
```

### 🔍 Line-by-Line

**Lines 3-5: Fields**
- **left, right**: Operands (expressions)
- **op**: Operator string ("+", "-", etc.)

**Lines 7-13: Constructor**
```java
public BinaryOpNode(Expression left, String op, Expression right) {
    if (left == null || right == null || op == null) {
        throw new RuntimeException("Invalid Binary Operation");
    }
    this.left = left;
    this.op = op;
    this.right = right;
}
```

**All-null-checks**: Defensive programming

**Line 16**: `Object evaluate(Environment env)`
- Recursive evaluation
- लेता है: environment (variable values के लिए)
- देता है: computed value

**Lines 17-19: Null environment check**

**Lines 21-24: Recursive evaluation**
```java
Object l = left.evaluate(env);
Object r = right.evaluate(env);
```

**यह recursive है!**
- अगर left एक BinaryOpNode है, तो उसका evaluate() call होगा
- Example: `(2 + 3) * 4`
  ```
  BinaryOpNode("*", 
      BinaryOpNode("+", 2, 3),  ← left
      4                          ← right
  )
  ```
  Evaluation:
  ```
  left.evaluate() → BinaryOpNode("+", 2, 3).evaluate() → 5
  right.evaluate() → 4
  5 * 4 = 20
  ```

**Lines 30-46: Number Operations**
```java
if (l instanceof Double && r instanceof Double) {
    double ld = (Double) l;
    double rd = (Double) r;
    
    switch (op) {
        case "+": return ld + rd;
        case "-": return ld - rd;
        case "*": return ld * rd;
        case "/": 
            if (rd == 0) throw new RuntimeException("Division by zero");
            return ld / rd;
        case ">": return ld > rd;
        case "<": return ld < rd;
        case "==": return ld == rd;
    }
}
```

**Type check**: दोनों operands Double हैं?
- If yes: numeric operations करो
- Comparison operators return Boolean!

**Division by zero check**: ✓

**Lines 48-49: String concatenation**
```java
if (op.equals("+")) {
    return l.toString() + r.toString();
}
```

**अगर numbers नहीं हैं पर `+` है**: String concat
- `"Hello" + " World"` → `"Hello World"`
- `"Number: " + 5` → `"Number: 5"`

**Lines 51-53: Equality for non-numbers**
```java
if (op.equals("==")) {
    return l.equals(r);
}
```

**Object equality check**
- String equality, etc.

**Lines 55-58: Error fallback**
```java
throw new RuntimeException(
    "Type error: cannot apply '" + op + "' to " + l + " and " + r
);
```

### 🧠 Concept: Polymorphism in Evaluation

```
Expression (interface)
├─ NumberNode → evaluate() returns Double
├─ StringNode → evaluate() returns String
├─ VariableNode → evaluate() returns value from env
└─ BinaryOpNode → evaluate() recursively calls operands
```

यह **tree walking interpreter** है।

### ⚠️ Edge Cases / Bugs

**Case 1**: String minus number
```
"hello" - 5

1. l = "hello", r = 5.0
2. Not both Double → skip numeric ops
3. op != "+" → skip concat
4. op != "==" → skip equality
5. throw RuntimeException("Type error: cannot apply '-' to hello and 5.0")
✓ Caught
```

**Case 2**: Type coercion missing
```
10 + "5"

1. l = 10.0 (Double), r = "5" (String)
2. Not both Double → skip numeric ops
3. op == "+" → String concat!
4. return "10.0" + "5" = "10.05"
⚠️ Unexpected behavior! Integer/Double को 10.0 में convert होता है।

Better fix:
- Type coercion करो (Java की तरह)
- या strict typing enforce करो
```

**Case 3**: Double equality
```
0.1 + 0.2 == 0.3

1. 0.1 + 0.2 = 0.30000000000000004 (floating point precision)
2. 0.30000000000000004 == 0.3 → false
⚠️ Floating point precision issue!

Fix: epsilon comparison करो
```

### 💣 Viva Questions

**Q1**: "String + Number क्या करेगा?"
- **Answer**: String concatenation
  ```
  "Hello" + 5 → "Hello5.0"
  5 + "Hello" → "5.0Hello"
  ```

**Q2**: "Type checking को strict कैसे करोगे?"
- **Answer**:
  ```java
  if (l instanceof Double && r instanceof Double) {
      // numeric ops
  } else if (l instanceof String && r instanceof String) {
      // string ops
  } else {
      throw new RuntimeException("Type mismatch");
  }
  ```

**Q3**: "क्या `-` को unary operator के रूप में support है?"
- **Answer**: नहीं! सिर्फ binary है
  ```
  -5  ← Unsupported (Tokenizer में MINUS, फिर NUMBER(5))
  ```

---

---

# 🎓 COMPLETE INTERPRETER FLOW DIAGRAM

```
┌──────────────────────────────────────────────────────────────────┐
│                     ZARA INTERPRETER PIPELINE                    │
│                                                                  │
│  INPUT: .zara source file                                       │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ STEP 1: TOKENIZATION (Tokenizer.java)                       ││
│  │ ─────────────────────────────────────────────────────────── ││
│  │                                                              ││
│  │ Input: "set x = 10 + 5"                                     ││
│  │                                                              ││
│  │ Process:                                                     ││
│  │ 1. Character-by-character scan (pos pointer)               ││
│  │ 2. Type identification:                                      ││
│  │    - 's' → 'set' keyword check → SET token                 ││
│  │    - ' ' → whitespace → skip                                ││
│  │    - 'x' → identifier → IDENTIFIER token                    ││
│  │    - ' ' → skip                                              ││
│  │    - '=' → symbol → EQUALS token                            ││
│  │    - '1','0' → digits → NUMBER(10) token                    ││
│  │    - '+' → symbol → PLUS token                              ││
│  │    - '5' → digit → NUMBER(5) token                          ││
│  │    - EOF → add EOF token                                     ││
│  │                                                              ││
│  │ Output Token List:                                           ││
│  │ [SET, IDENTIFIER(x), EQUALS, NUMBER(10), PLUS, NUMBER(5), EOF]
│  └─────────────────────────────────────────────────────────────┘│
│                            ↓                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ STEP 2: PARSING (Parser.java)                               ││
│  │ ─────────────────────────────────────────────────────────── ││
│  │                                                              ││
│  │ Input: Token list                                            ││
│  │ [SET, IDENTIFIER(x), EQUALS, NUMBER(10), PLUS, NUMBER(5), EOF]
│  │                                                              ││
│  │ Grammar-based Parsing (Recursive Descent):                  ││
│  │                                                              ││
│  │ parse()                                                      ││
│  │   → parseInstruction()                                        ││
│  │       → parseAssign()                                         ││
│  │           1. consume(SET) ✓                                 ││
│  │           2. consume(IDENTIFIER) → name = "x"               ││
│  │           3. consume(EQUALS) ✓                              ││
│  │           4. parseExpression()                               ││
│  │               → parseTerm()                                   ││
│  │                   → parsePrimary()                            ││
│  │                       → NUMBER(10) → NumberNode(10.0)       ││
│  │               Loop: current() = PLUS                         ││
│  │               → parseTerm()                                   ││
│  │                   → parsePrimary()                            ││
│  │                       → NUMBER(5) → NumberNode(5.0)         ││
│  │               → BinaryOpNode(NumberNode(10), "+", NumberNode(5))
│  │           5. return AssignInstruction("x", expr)            ││
│  │   → current() = EOF, loop exits                             ││
│  │                                                              ││
│  │ Output: AST (Abstract Syntax Tree)                           ││
│  │ [ AssignInstruction("x",                                     ││
│  │       BinaryOpNode(NumberNode(10), "+", NumberNode(5))      ││
│  │ ]                                                             ││
│  └─────────────────────────────────────────────────────────────┘│
│                            ↓                                     │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ STEP 3: EXECUTION (Interpreter.java + Environment.java)    ││
│  │ ─────────────────────────────────────────────────────────── ││
│  │                                                              ││
│  │ Input: AST + Empty Environment                              ││
│  │ env = {  }                                                   ││
│  │                                                              ││
│  │ Execute Instruction 1: AssignInstruction("x", expr)         ││
│  │   1. Evaluate expression: expr.evaluate(env)                ││
│  │       → BinaryOpNode.evaluate(env)                          ││
│  │           → left.evaluate(env) = NumberNode(10).evaluate()  ││
│  │                                 = 10.0                       ││
│  │           → right.evaluate(env) = NumberNode(5).evaluate()  ││
│  │                                 = 5.0                        ││
│  │           → perform: 10.0 + 5.0 = 15.0                      ││
│  │           → return 15.0                                      ││
│  │   2. env.set("x", 15.0)                                      ││
│  │                                                              ││
│  │   Environment after execution:                               ││
│  │   env = { "x" → 15.0 }                                       ││
│  │                                                              ││
│  │ All instructions processed → Done!                           ││
│  └─────────────────────────────────────────────────────────────┘│
│                            ↓                                     │
│  OUTPUT: Side effects (variable values stored, output printed)  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

---

# 🏆 TOP 5 MOST IMPORTANT VIVA QUESTIONS

## 🥇 Question 1: Three-Phase Interpretation Architecture

**Question**: "अपने interpreter को three phases में क्यों divide किया? यह sequence क्यों जरूरी है?"

**Expected Answer** (detailed):
```
Three phases:
1. TOKENIZATION: String को tokens में break करना
   - क्यों: Raw text को structured format में बदलना
   - Tool: Tokenizer
   - Input: "set x = 10"
   - Output: [SET, IDENTIFIER(x), EQUALS, NUMBER(10)]

2. PARSING: Tokens को AST में convert करना
   - क्यों: Grammar rules follow करना, syntax errors catch करना
   - Tool: Parser (recursive descent)
   - Input: Token list
   - Output: Instruction tree
   - Grammar example: assignment := SET IDENTIFIER EQUALS expression

3. EXECUTION: AST को execute करना
   - क्यों: Actual computation करना, side effects (output, variables)
   - Tool: Interpreter with Environment
   - Input: AST + empty environment
   - Output: Modified environment, printed output

Sequence जरूरी क्यों:
- Phase 1 → Phase 2 → Phase 3 (one-way dependency)
- Tokenizer के बिना, string को समझ नहीं सकते
- Parser के बिना, grammar validate नहीं कर सकते
- Execution के बिना, program run नहीं हो सकता

लाभ:
- Separation of concerns (हर phase independent)
- Error reporting (कहां fail हुआ, पता चल जाता है)
- Code reuse (दोनों programs के लिए phases reuse)
- Debugging आसान
```

---

## 🥈 Question 2: Operator Precedence in Parser

**Question**: "अपने parser में 2 + 3 * 4 को कैसे handle करते हो? AST क्या बनती है? अगर गलत precedence हो तो क्या होगा?"

**Expected Answer** (with AST drawing):
```
Correct Precedence (* > +):

Tokens: [NUMBER(2), PLUS, NUMBER(3), MULTIPLY, NUMBER(4)]

Parsing:
parseExpression()
├─ parseTerm()
│   └─ parsePrimary() → NumberNode(2)
├─ loop: op = PLUS
├─ parseTerm()
│   └─ parsePrimary() → NumberNode(3)
│   └─ loop: op = MULTIPLY
│   └─ parsePrimary() → NumberNode(4)
│   └─ BinaryOpNode(3, "*", 4) = 12
├─ BinaryOpNode(BinaryOpNode(2, "+", 12))
└─ return

Final AST:
       +
      / \
     2   *
        / \
       3   4

Evaluation: 2 + (3 * 4) = 2 + 12 = 14 ✓

---

Wrong Precedence (+ > *):

Token parsing order same, but evaluate:
       *
      / \
     +   4
    / \
   2   3

Evaluation: (2 + 3) * 4 = 5 * 4 = 20 ✗

---

Implementation:
Grammar hierarchy:
parseExpression()    ← lowest precedence (+ -)
├─ parseTerm()      ← higher precedence (* /)
├─ parsePrimary()   ← highest (literals, variables)

Method nesting = precedence!
```

---

## 🥉 Question 3: Tokenization Process - Detailed Example

**Question**: "String `when score > 50:` को tokenize करते समय क्या होता है? Line-by-line walk करो। क्या edge cases हैं?"

**Expected Answer** (with pos tracking):
```
Source: "when score > 50:"
pos: 0

Step 1: pos=0, char='w'
├─ isLetter('w') = true
├─ readIdentifierOrKeyword()
│   └─ while: 'w','h','e','n' (pos=0→4)
│   └─ word = "when"
│   └─ switch: case "when" → Token(WHEN, "when", line=1)
└─ pos=4, token added

Step 2: pos=4, char=' '
├─ isWhitespace(' ') = true
├─ skipSpaces()
│   └─ while: ' ' (pos=4→5)
└─ pos=5, continue

Step 3: pos=5, char='s'
├─ isLetter('s') = true
├─ readIdentifierOrKeyword()
│   └─ 's','c','o','r','e' (pos=5→10)
│   └─ word = "score"
│   └─ not a keyword → Token(IDENTIFIER, "score", line=1)
└─ pos=10, token added

Step 4: pos=10, char=' '
├─ skipSpaces()
└─ pos=11

Step 5: pos=11, char='>'
├─ readSymbol()
├─ case '>': pos++; return Token(GT, ">", line=1)
└─ pos=12

Step 6: pos=12, char=' '
├─ skipSpaces()
└─ pos=13

Step 7: pos=13, char='5'
├─ isDigit('5') = true
├─ readNumber()
│   └─ '5','0' (pos=13→15)
│   └─ num = "50"
│   └─ valid number
│   └─ Token(NUMBER, "50", line=1)
└─ pos=15

Step 8: pos=15, char=':'
├─ readSymbol()
├─ case ':': pos++; return Token(COLON, ":", line=1)
└─ pos=16

Step 9: pos=16
├─ pos >= source.length()
├─ exit while loop
└─ add Token(EOF, "", line=1)

Output Tokens:
[WHEN(when), IDENTIFIER(score), GT(>), NUMBER(50), COLON(:), EOF]

Edge Cases:
1. "when  score" (extra spaces) → skipSpaces() handles
2. "score2var" → identifier "score2var" (digit allowed mid-word)
3. "2score" → NUMBER(2), then IDENTIFIER(score)
4. ">" vs ">=" → lookahead, but ZARA doesn't support ">="
```

---

## 🏅 Question 4: Loop Parsing and Nested Structures

**Question**: "Loop को parse करते समय क्या होता है? Nested loops को कैसे handle करते हो? क्या limitation है?"

**Expected Answer**:
```
Example Code:
loop 3:
    set i = 1
    loop 2:
        show i

Parsing Phase:
parseLoop() called (pos at LOOP token)
├─ consume(LOOP) → pos++
├─ consume(NUMBER) → numStr = "3", times = 3
├─ consume(COLON) → pos++
├─ while (current != EOF && current != LOOP):
│   ├─ Iteration 1: current = SET
│   │   ├─ parseInstruction() → parseAssign()
│   │   └─ AssignInstruction("i", NumberNode(1))
│   ├─ Iteration 2: current = LOOP (nested!)
│   │   ├─ parseInstruction() → parseLoop() [recursive!]
│   │   ├─ Inner parseLoop():
│   │   │   ├─ consume(LOOP)
│   │   │   ├─ times = 2
│   │   │   ├─ consume(COLON)
│   │   │   ├─ while: current = SHOW
│   │   │   │   └─ parseInstruction() → parsePrint()
│   │   │   └─ current = EOF (or next token after show)
│   │   │   └─ exit inner while
│   │   │   └─ return RepeatInstruction(2, [PrintInstruction])
│   │   ├─ AssignInstruction added to outer body
│   │   └─ current = EOF
│   └─ Iteration 3: loop condition check
│       └─ current = EOF → exit while
└─ return RepeatInstruction(3, [AssignInstruction, RepeatInstruction(2, ...)])

LIMITATION:
Problem: Loop body को कब end करना है?
```
while (current != EOF && current != LOOP)
```
यह नहीं समझता:
```
loop 3:
    loop 2:
        show i
    show "Done"  ← यह outer loop का है या inner?

Current code का behavior:
Inner loop के बाद "show Done" को inner loop का समझ सकता है!

Proper Solution:
- Indentation track करो (Python style)
- Explicit block delimiters (Java style: { })
- या Parser logic को improve करो
```

---

## ⭐ Question 5: Type System and Expression Evaluation

**Question**: "अपने interpreter में type system क्या है? अगर `\"hello\" + 5` दो तो क्या output है? क्या type coercion है?"

**Expected Answer**:
```
Type System in ZARA:

Supported Types:
1. Double (numbers): 10, 3.5, 0.0
2. String (text): "Hello", "42"
3. Boolean (implicit): true/false (from comparisons)

Type Representation:
- Everything is Object in Environment
- Type checking at runtime (duck typing)

Expression Evaluation in BinaryOpNode.evaluate():

Rule 1: Both are Double
├─ Arithmetic: +, -, *, / (numeric)
├─ Comparison: >, <, == (boolean result)
└─ Division by zero check ✓

Rule 2: Both are String (implicit)
├─ Only "+" is supported → concatenation
└─ "Hello" + "World" = "HelloWorld"

Rule 3: Mixed types with "+"
├─ String concatenation
├─ "Hello" + 5 → "Hello5.0" (both converted to string)
└─ 5 + "Hello" → "5.0Hello"

Rule 4: Other operations with non-numbers
└─ throw RuntimeException("Type error")

Example: "hello" + 5
1. l = "hello" (String)
2. r = 5.0 (Double)
3. Not both Double → skip numeric ops
4. op == "+" → concatenation
5. return "hello" + "5.0" = "hello5.0"

Edge Case: "hello" - 5
1. l = "hello", r = 5.0
2. Not both Double
3. op != "+", op != "=="
4. throw RuntimeException("Type error: cannot apply '-' to hello and 5.0")

Comparison Operations:
10 == 10.0 → true (numeric)
"10" == "10" → true (string)
10 == "10" → false (different types)

Limitation:
- No implicit integer→double conversion
- No type coercion for arithmetic (except +)
- No function overloading

Better Design:
- Explicit type promotion
- Or strict type checking
```

---

---

# 📋 QUICK REVISION CHECKLIST

```
TOKENIZER:
□ समझो: Character-by-character scan, state machine
□ समझो: DFA (Deterministic Finite Automaton)
□ याद रखो: skipSpaces(), readNumber(), readString(), readIdentifierOrKeyword(), readSymbol()
□ Edge case: Unterminated string, invalid escape, consecutive operators
□ Concept: Line tracking for error messages

TOKEN:
□ समझो: Data holder (type, value, line)
□ समझो: Immutable (final fields)
□ Use: Passed between Tokenizer → Parser

TOKENTYPE:
□ समझो: Enum for type safety
□ समझो: Categories (keywords, values, operators, delimiters, control)

PARSER:
□ समझो: Recursive descent parsing
□ समझो: Grammar rules → methods
□ याद रखो: parseExpression() vs parseTerm() vs parsePrimary()
□ समझो: Operator precedence via method nesting
□ समझो: Left-associativity via iterative loops
□ Edge case: Unmatched parentheses, nested expressions
□ Concept: Predictive parsing (1-token lookahead)

INTERPRETER:
□ समझो: Three phases (Tokenize → Parse → Execute)
□ समझो: Separation of concerns
□ समझो: Error handling centralization

ENVIRONMENT:
□ समझो: Symbol table (HashMap)
□ याद रखो: set(), get(), has()
□ समझो: Null checks, variable existence validation

BINARYOPNODE:
□ समझो: Recursive tree evaluation
□ समझो: Type checking and operations
□ याद रखो: Division by zero, string concatenation, comparisons
□ Edge case: Type coercion limitations

MAIN:
□ समझो: File I/O, error handling
□ समझो: Coordination of all components
```

---

**END OF VIVA TRAINING DOCUMENT**

Great work! You're ready for the viva! 🚀
