# 🚀 ADVANCED VIVA SCENARIOS & CORNER CASES

---

## 🔥 DIFFICULT VIVA QUESTIONS (FAANG Level)

---

## Q1: Whitespace Handling Edge Cases

**Scenario**: 
```
Student's code का Tokenizer यह handle करता है:
Input: "set  x  =  10"  (multiple spaces)

Examiner पूछता है:
"क्या यह सही है? अगर line ending में extra space हो?"
```

**उत्तर**:
```java
// निम्नलिखित को handle करना चाहिए:

1. "set x = 10  " (trailing spaces)
   → Correctly skipped by skipSpaces()
   ✓ Works

2. "set x = 10\n\n\n" (multiple trailing newlines)
   → line counter updates correctly
   ✓ Works

3. "set\t\tx\t=\t10" (tabs)
   → Character.isWhitespace('\t') = true
   ✓ Works

4. "set\r\nx = 10" (Windows CRLF)
   → '\r' and '\n' दोनों whitespace
   → पर line counter सिर्फ '\n' के लिए increment होता है
   ⚠️ Bug potential! Line numbers wrong हो सकते हैं

Fix:
private void skipSpaces() {
    while (pos < source.length()) {
        char c = source.charAt(pos);
        
        if (c == '\r') {
            pos++;
            if (pos < source.length() && source.charAt(pos) == '\n') {
                pos++;  // Handle CRLF as single newline
            }
            line++;
            continue;
        }
        
        if (c == '\n') {
            line++;
            pos++;
            continue;
        }
        
        if (Character.isWhitespace(c)) {
            pos++;
            continue;
        }
        
        break;
    }
}
```

---

## Q2: String Escape Sequence Deep Dive

**Scenario**:
```
Input: "He said \"Hello\\nWorld\""

Examiner: "इस को tokenize करो step-by-step। क्या होगा?"
```

**उत्तर**:
```
pos=0, char='"'  → readString() called, pos++
pos=1, char='H'  → sb.append('H')
pos=2, char='e'  → sb.append('e')
pos=3, char=' '  → sb.append(' ')
pos=4, char='s'  → sb.append('s')
pos=5, char='a'  → sb.append('a')
pos=6, char='i'  → sb.append('i')
pos=7, char='d'  → sb.append('d')
pos=8, char=' '  → sb.append(' ')
pos=9, char='\' → escape sequence!
     pos++ → pos=10
     next='\"' → sb.append('"')
pos=11, char='H' → sb.append('H')
pos=12, char='e' → sb.append('e')
pos=13, char='l' → sb.append('l')
pos=14, char='l' → sb.append('l')
pos=15, char='o' → sb.append('o')
pos=16, char='\' → escape sequence!
      pos++ → pos=17
      next='n' → sb.append('\n'), line++
pos=18, char='W' → sb.append('W')
pos=19, char='o' → sb.append('o')
pos=20, char='r' → sb.append('r')
pos=21, char='l' → sb.append('l')
pos=22, char='d' → sb.append('d')
pos=23, char='\' → escape sequence!
      pos++ → pos=24
      next='\' → sb.append('\')
pos=25, char='"' → closing quote! break
pos++ → skip closing quote

Final string in token:
"He said \"Hello\nWorld\"" (as shown)

But internally sb.toString() = "He said "Hello<newline>World\"
                                          ↑ actual newline character
                                          ↑ backslash at end
```

---

## Q3: Recursive Descent Parser - Left Recursion Problem

**Scenario**:
```
अगर grammar left-recursive होता तो क्या होता?

Left-recursive grammar:
Expression := Expression "+" Term | Term
```

**उत्तर**:
```
Left-recursive grammar से infinite recursion होता है!

Example: 2 + 3

parseExpression() 
  → parseExpression()  ← Recursive call WITHOUT consuming token!
    → parseExpression()
      → ... (infinite recursion!)

इसीलिए हमने non-left-recursive form use किया:

Expression := Term ("+" Term)*

यह left-associative भी है:
2 + 3 + 4
→ ((2 + 3) + 4)  ← Correct!

अगर right-associative चाहते:
Expression := Term ("+" Expression)?
2 + 3 + 4
→ (2 + (3 + 4))  ← Different!
```

---

## Q4: Type System Bugs

**Scenario**:
```zara
set x = 5
set y = "10"
set z = x + y
show z
```

**Examiner**:
```
यह code क्या output करेगा?
अगर strict typing होती तो क्या होता?
```

**उत्तर**:
```
Current behavior:
1. x = 5.0 (Double)
2. y = "10" (String)
3. z = BinaryOpNode(NumberNode(5.0), "+", StringNode("10"))
4. Evaluation:
   - l = 5.0
   - r = "10"
   - Not both Double → skip numeric ops
   - op == "+" → String concat!
   - return "5.0" + "10" = "5.010"
5. Output: "5.010"

⚠️ Unexpected! Type mixing issue।

Strict typing में:
- Error thrown immediately
- "Cannot apply '+' to Double and String"
```

---

## Q5: Parser Precedence Bug in Comparisons

**Scenario**:
```zara
when x + 1 > 5 * 2:
    show "Yes"
```

**Examiner**:
```
यह condition correctly evaluate होगी?
AST क्या बनेगी?
```

**उत्तर**:
```
Grammar:
parseExpression() → Term ((+|-|>|<|==) Term)*
parseTerm() → Primary ((* |/) Primary)*

Parsing: "x + 1 > 5 * 2"

parseExpression()
├─ left = parseTerm() → VariableNode("x")
├─ current = PLUS
├─ op = "+"
├─ right = parseTerm()
│   ├─ left = parsePrimary() → NumberNode(1)
│   ├─ current = GT (no * or / operator)
│   └─ return NumberNode(1)
├─ left = BinaryOpNode(x, "+", 1)
├─ current = GT
├─ op = ">"
├─ right = parseTerm()
│   ├─ left = parsePrimary() → NumberNode(5)
│   ├─ current = MULTIPLY
│   ├─ op = "*"
│   ├─ right = parsePrimary() → NumberNode(2)
│   ├─ left = BinaryOpNode(5, "*", 2)
│   └─ return BinaryOpNode(5, "*", 2)
├─ left = BinaryOpNode(BinaryOpNode(x, "+", 1), ">", BinaryOpNode(5, "*", 2))
└─ return

AST:
       >
      / \
     +   *
    / \ / \
   x  1 5  2

Evaluation (x=3):
- Left: 3 + 1 = 4
- Right: 5 * 2 = 10
- 4 > 10 = false

✓ Correct! But why?

Because:
1. parseTerm() handles * /
2. parseExpression() handles + - > <
3. Method nesting creates precedence:
   * / > + - > > <

Wait! Actually this is WRONG!

Standard precedence should be:
* / > + - > > < > ==

Current code: + - > > < ==

Problem: Comparison has SAME precedence as arithmetic!

Test: x + y > 5 + 2
- Should be: (x + y) > (5 + 2)
- Currently: ((x + y) > 5) + 2  ← WRONG!

Fix needed:
parseComparison() → handles >, <, ==
  └─ parseAddition() → handles +, -
      └─ parseTerm() → handles *, /
```

---

## Q6: Loop Body Parsing Bug

**Scenario**:
```zara
set x = 1
loop 3:
    show x
    set x = x + 1
show "Done"
```

**Examiner**:
```
parseLoop() में body को कब तक read करता हो?
"show Done" को किस loop में include करता है?
```

**उत्तर**:
```java
// Current parseLoop() code:
while (current().getType() != TokenType.EOF &&
       current().getType() != TokenType.LOOP) {
    body.add(parseInstruction());
}

// Tokens after COLON:
[SHOW, IDENTIFIER(x), NEWLINE, SET, IDENTIFIER(x), EQUALS, ...
 PLUS, NUMBER(1), NEWLINE, SHOW, STRING("Done"), EOF]

// Parsing:
Iteration 1: current = SHOW
  → parseInstruction() → PrintInstruction
  → pos++ after parseExpression()

Iteration 2: current = SET
  → parseInstruction() → AssignInstruction
  → pos++ 

Iteration 3: current = SHOW (for "Done")
  → parseInstruction() → PrintInstruction
  → pos++

Iteration 4: current = EOF
  → while condition false → exit

Result:
RepeatInstruction(3, [
    PrintInstruction(VariableNode(x)),
    AssignInstruction(x, ...),
    PrintInstruction(StringNode("Done"))  ← WRONG!
])

"Done" को 3 बार print होगा! ❌

Correct behavior से "Done" को 1 बार print होना चाहिए।

Fix:
- Indentation tracking करो
- Explicit block delimiters use करो ({})
- या NEWLINE token count करो
```

---

## Q7: Double vs Float Precision

**Scenario**:
```zara
set x = 0.1
set y = 0.2
set z = x + y
when z == 0.3:
    show "Equal"
```

**Examiner**:
```
"Equal" print होगा?
```

**उत्तर**:
```
NO! Classic floating-point precision issue।

Actual computation:
x = 0.1 (Double) = 0.1000000000000000055511...
y = 0.2 (Double) = 0.2000000000000000111022...
z = 0.1 + 0.2 = 0.3000000000000000444089...

Comparison:
z == 0.3
0.3000000000000000444089... == 0.3 → FALSE

Output: (nothing printed)

Fix (Epsilon comparison):
```java
private static final double EPSILON = 1e-9;

if (Math.abs(ld - rd) < EPSILON) {
    return true;  // Equal enough
}
return ld == rd;
```

या use BigDecimal for financial apps
```

---

## Q8: Null Pointer Exception Scenarios

**Scenario**:
```java
// क्या NPE हो सकता है?
String sourceCode = null;
Interpreter interpreter = new Interpreter();
interpreter.run(sourceCode);
```

**उत्तर**:
```
Line 3 में Interpreter.run():
```
if (sourceCode == null || sourceCode.trim().isEmpty()) {
    throw new IllegalArgumentException("Source code cannot be null or empty");
}
```

✓ Caught! No NPE.

But अगर check न हो:
sourceCode.trim() → NPE!

अन्य potential NPE:
1. Tokenizer token generation null return करे
   - Fixed by: if (tokens == null)
2. Parser null instruction generate करे
   - Fixed by: if (instr == null)
3. Environment में null value store करे
   - Fixed by: if (value == null) throw
```

---

## Q9: Unbounded Resource Consumption

**Scenario**:
```zara
loop 1000000000:
    show "x"
```

**Examiner**:
```
Memory/Time issue होगा?
```

**उत्तर**:
```
RepeatInstruction.execute():
for (int i = 0; i < times; i++) {
    for (Instruction instr : body) {
        instr.execute(env);
    }
}

अगर times = 1000000000 (1 billion):
- Outer loop 1 billion iterations
- Inner loop execute करेगा
- System.out.println() हर बार
- Time: बहुत ज़्यादा (सेकंड/मिनट)
- Memory: कम (सिर्फ environment)

Better:
- Timeout detection
- Loop count limit
- Lazy evaluation (generator)
```

---

## Q10: Scope and Variable Shadowing

**Current ZARA behavior**:
```zara
set x = 10
show x  // Output: 10
set x = 20
show x  // Output: 20
```

**Examiner**:
```
क्या nested scopes support हैं?
Function-level scoping?
```

**उत्तर**:
```
NO! Current implementation में:
- Single global scope
- Environment एक single HashMap है
- कोई nesting नहीं

अगर function definition हो:
```zara
function add(a, b):
    set sum = a + b
    return sum

set result = add(3, 5)
show result
```

यह supported नहीं है।

Nested scope add करने के लिए:
```java
class Environment {
    private Map<String, Object> variables;
    private Environment parent;  // Parent scope
    
    public Environment(Environment parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }
    
    public Object get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (parent != null) {
            return parent.get(name);  // Look up chain
        }
        throw new RuntimeException("Variable not defined: " + name);
    }
}
```

Function call करते वक्त नया Environment create होगा।
```

---

## 🎯 REAL INTERVIEW QUESTIONS (Google/Meta/Amazon Level)

---

## Q: Design Question - Language Extension

**Q**: "अगर ZARA में यह features add करने हों:"
1. String slicing (s[1:3])
2. Arrays (arr = [1, 2, 3])
3. Functions (def add(a, b): return a+b)
4. Dictionaries (d = {"x": 10})

**एक को implement करने के लिए क्या changes करने पड़ेंगे?"

**Approach**:
```
Feature: String Slicing (s[0:2])

TOKENIZER changes:
- LBRACKET, RBRACKET tokens add करो
- या COLON already है

PARSER changes:
- parsePrimary() में LBRACKET handling:
  ```java
  if (t.getType() == TokenType.LBRACKET) {
      pos++;
      Expression start = parseExpression();
      consume(TokenType.COLON);
      Expression end = parseExpression();
      consume(TokenType.RBRACKET);
      return new SliceNode(left, start, end);
  }
  ```

EXPRESSION changes:
- SliceNode class बनाओ
  ```java
  public class SliceNode implements Expression {
      private Expression string;
      private Expression start;
      private Expression end;
      
      @Override
      public Object evaluate(Environment env) {
          String s = string.evaluate(env).toString();
          int st = ((Double)start.evaluate(env)).intValue();
          int en = ((Double)end.evaluate(env)).intValue();
          return s.substring(st, en);
      }
  }
  ```

Total effort: ~30-40 lines of code
Impact: Minimal (no grammar conflicts)
```

---

## Q: Performance Optimization

**Q**: "अगर 100MB का ZARA file parse करना हो, क्या problem होगा?"

**Answer**:
```
Problems:
1. Tokenizer: String tokenization
   - pos pointer + substring() calls
   - Memory: O(n) where n = source length
   - Time: O(n) scanning
   ✓ Acceptable for 100MB

2. Parser: Building full AST
   - 100MB source = millions of tokens
   - AST nodes: one for each instruction
   - Memory: O(number of instructions)
   ⚠️ Problem! Entire AST in memory

3. Interpreter: Executing AST
   - Linear pass through instructions
   - ✓ Memory OK

Solutions:
1. Streaming tokenizer (chunked reading)
2. Lazy evaluation (parse-as-you-go)
3. Bytecode compilation (intermediate format)
4. JIT compilation (like JavaScript engines)
```

---

## Q: Error Recovery

**Q**: "Syntax error के बाद parse करना बंद कर देता है। अगर multiple errors report करना हो?"

**Answer**:
```java
class Parser {
    private List<ParseError> errors = new ArrayList<>();
    
    private void error(String message) {
        errors.add(new ParseError(message, current().getLine()));
        // Don't throw, continue parsing
    }
    
    private Token consumeSafe(TokenType expected) {
        Token t = current();
        if (t.getType() != expected) {
            error("Expected " + expected + " but got " + t.getType());
            // Try to recover by skipping tokens
            while (current().getType() != TokenType.NEWLINE &&
                   current().getType() != TokenType.EOF) {
                pos++;
            }
            pos++;  // skip newline
            return null;
        }
        pos++;
        return t;
    }
    
    public List<Instruction> parse() {
        // ... parse, but don't throw
        if (!errors.isEmpty()) {
            printErrors();
        }
        return instructions;
    }
}
```

---

## 🎓 VIVA TIPS & TRICKS

### 1. **Answer Structure (STAR method)**
```
SITUATION: "ZARA interpreter में..."
TASK: "मुझे tokenizer implement करना था"
ACTION: "मैंने DFA design किया, character-by-character scan..."
RESULT: "सभी test cases pass हुए, nested loops handle हुए"
```

### 2. **When Stuck, Ask Questions**
```
Examiner: "Operator precedence को कैसे handle करते हो?"

Student: "क्या आप बता सकते हैं - क्या right-associative या left-associative?"
"Comparison operators को arithmetic से higher या lower precedence?"

यह दिखाता है कि तुम सोचते हो!
```

### 3. **Draw Diagrams**
```
AST example:
     +
    / \
   *   4
  / \
 2   3

Token stream:
[SET][IDENTIFIER][EQUALS][NUMBER][EOF]

कभी भी diagram draw करो - clarity show करता है।
```

### 4. **Test Cases Mention करो**
```
जब कुछ explain कर रहे हो:

"यह case को handle करता है: empty string
यह case को भी: nested loops
पर यह edge case है: Windows CRLF line endings (currently not handled)"

दिखाता है कि तुमने edge cases सोचे हैं।
```

### 5. **Comparison करो**
```
"हम recursive descent parser use करते हैं क्योंकि:
- Simple to implement (vs LL/LALR generator)
- Easy to debug (vs table-driven)
- Good error messages (vs regex)

Alternative: yacc/bison, ANTLR, hand-written regex
पर recursive descent best है छोटे DSL के लिए"
```

---

## 📋 LAST-MINUTE CHECKLIST

```
15 MINUTES BEFORE VIVA:

□ Tokenizer की working समझ लो
  - Character types
  - Keywords vs identifiers
  - Number/string parsing

□ Parser की working समझ लो
  - Grammar rules
  - Precedence
  - Recursion

□ Interpreter pipeline समझ लो
  - Three phases
  - Data flow
  - Environment management

□ 2 Edge cases तैयार करो
  - Unterminated string
  - Undefined variable

□ 2 Improvements सोच लो
  - Line tracking in parser
  - Nested loops fix

□ Diagram बना लो
  - Token stream example
  - AST example
  - Environment at end

□ अपना code छुआ हुआ समझ लो
  - किसने write किया
  - कहां modifications किए
```

---

## 🚀 FINAL VIVA SCRIPT

### Opening (जब examiner पूछे "अपना project बताओ")

```
"हमने ZARA language का एक interpreter implement किया Java में।

ZARA एक छोटी programming language है जिसमें:
- Variables (set x = 10)
- Arithmetic (x + y * 2)
- Conditionals (when score > 50: show "Pass")
- Loops (loop 4: show i)

Interpreter के 3 phases हैं:

1. TOKENIZATION: Source code को tokens में तोड़ता है
   - "set x = 10" → [SET, IDENTIFIER(x), EQUALS, NUMBER(10)]
   - Tokenizer character-by-character scan करता है

2. PARSING: Tokens को AST में convert करता है
   - Grammar rules follow करता है
   - Operator precedence handle करता है
   - Recursive descent parser का use किया

3. EXECUTION: AST को execute करता है
   - Environment में variables store करते हैं
   - हर instruction को run करते हैं
   - Side effects (print, assignment) होते हैं

उदाहरण के लिए:
set x = 10
show x + 5

TOKENIZE: [SET, ID(x), EQUALS, NUMBER(10), SHOW, ID(x), PLUS, NUMBER(5)]
PARSE: [AssignInstruction(x, 10), PrintInstruction(x+5)]
EXECUTE: x becomes 10, then 15 is printed

मैंने Tokenizer implement किया है - सभी token types, string escaping,
number parsing आदि। काफी tricky था special characters और escape
sequences को handle करना।"
```

---

## 🎯 FINAL SUMMARY

```
ZARA INTERPRETER = COMPLETE SYSTEM

Tokenizer (600 lines)
  ↓
Parser (250 lines)
  ↓
Interpreter (50 lines)
  ↓
Environment (60 lines)
  ↓
Instruction classes (150 lines)
  ↓
Expression classes (150 lines)

Total: ~1300 lines of Java

Key concepts:
✓ Lexical analysis (tokenization)
✓ Syntax analysis (parsing)
✓ Tree walking interpretation
✓ Symbol table management
✓ Operator precedence
✓ Recursive data structures
✓ Error handling
✓ Type checking

Ready for viva! 🎉
```

---

*आपका interpreter complete है। अब आप किसी भी examiner को confident
answer दे सकते हो!*

**Good luck! 🚀**
