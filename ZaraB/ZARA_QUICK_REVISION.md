# ⚡ ZARA INTERPRETER - QUICK REVISION (5 MINUTES)

---

## 🎯 THE BIG PICTURE

```
Source Code (.zara file)
        ↓
   TOKENIZER (break into tokens)
        ↓
    PARSER (build AST)
        ↓
  INTERPRETER (execute with Environment)
        ↓
   Console Output
```

---

## 🔴 TOKENIZER (Scanner)

**What it does**: Breaks `"set x = 10"` into `[SET, IDENTIFIER(x), EQUALS, NUMBER(10)]`

**Key Methods**:
- `skipSpaces()` - ignore whitespace
- `readNumber()` - parse 123 or 12.5
- `readString()` - parse "hello" with escape sequences
- `readIdentifierOrKeyword()` - parse x or set
- `readSymbol()` - parse + - * / : = == > <

**Important Edge Cases**:
- Unterminated string → ERROR
- Invalid escape → ERROR
- `.5` number → ERROR (leading dot)
- Windows CRLF → Line count issue ⚠️

**Memory Trick**: "Character-by-character scanner with lookahead"

---

## 🟠 TOKEN

**What it is**: Data holder

```java
Token(TokenType.NUMBER, "10", lineNumber)
```

**Three fields**: type, value, line

---

## 🟡 TOKENTYPE

**What it is**: Enum of all token types

```
SET, SHOW, WHEN, LOOP (keywords)
NUMBER, STRING, IDENTIFIER (values)
PLUS, MINUS, MULTIPLY, DIVIDE (operators)
EQUALS, EQEQ, GT, LT (operators)
EOF (sentinel)
```

---

## 🟢 PARSER (Grammar Analyzer)

**What it does**: Builds AST from tokens

**Grammar**:
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

**Method Structure** (Recursive Descent):
```
parse() → parseInstruction()
           ├─ parseAssign()
           ├─ parsePrint()
           ├─ parseIf()
           └─ parseLoop()
                └─ parseExpression()
                   └─ parseTerm()
                      └─ parsePrimary()
```

**Key Concept**: Method nesting = Operator precedence
- `parseTerm()` for * /
- `parseExpression()` for + - > < ==

**Left-Associativity**: 2 + 3 + 4 = ((2 + 3) + 4)
```java
while (current is + or -) {  // Iterative loop
    left = new BinaryOpNode(left, op, right);
}
```

**⚠️ Bug**: Comparison and arithmetic have same precedence!

**Memory Trick**: "Top-down, predictive, precedence-climbing parser"

---

## 🔵 INTERPRETER

**What it does**: Coordinates all 3 phases

```java
run(sourceCode)
  ├─ Tokenize → Token list
  ├─ Parse → Instruction list (AST)
  ├─ Execute → for each instruction
  │              instruction.execute(environment)
  └─ Done
```

**Three phases are sequential and separate**

**Memory Trick**: "Tokenize → Parse → Execute"

---

## 🟣 ENVIRONMENT

**What it is**: Symbol table (HashMap)

```
Variable Name → Value
x            → 10.0
name         → "Alice"
```

**Methods**:
- `set(name, value)` - store variable
- `get(name)` - retrieve variable (or error if not exists)
- `has(name)` - check if exists

**Key validation**:
- No null names
- No null values
- Variable must exist before get()

---

## 🔴 INSTRUCTION CLASSES

| Class | Does | Example |
|-------|------|---------|
| `AssignInstruction` | Stores value in environment | `set x = 10` |
| `PrintInstruction` | Prints to console | `show x` |
| `IfInstruction` | Conditional execution | `when x > 5: show "hi"` |
| `RepeatInstruction` | Loop body N times | `loop 3: show i` |

All have: `void execute(Environment env)`

---

## 🟠 EXPRESSION CLASSES

| Class | Returns | Example |
|-------|---------|---------|
| `NumberNode` | Double | `42` → 42.0 |
| `StringNode` | String | `"hello"` → "hello" |
| `VariableNode` | From environment | `x` → env.get("x") |
| `BinaryOpNode` | Recursively evaluates | `2 + 3` → 5.0 |

All have: `Object evaluate(Environment env)`

**BinaryOpNode** is KEY:
```
Left operand + Right operand = Result

Example: 2 + 3
├─ left = NumberNode(2).evaluate() = 2.0
├─ right = NumberNode(3).evaluate() = 3.0
└─ 2.0 + 3.0 = 5.0
```

---

## 🔥 TOP 5 QUESTIONS YOU'LL BE ASKED

### Q1: Explain the three phases with example
```
Input: "set x = 5"

Phase 1 (Tokenizer): [SET, IDENTIFIER(x), EQUALS, NUMBER(5)]
Phase 2 (Parser): AssignInstruction("x", NumberNode(5.0))
Phase 3 (Interpreter): env.set("x", 5.0)
```

### Q2: How does operator precedence work?
```
2 + 3 * 4 = 14 (not 20)

Because:
parseTerm() gets 3 * 4 first = 12
Then parseExpression() does 2 + 12 = 14
```

### Q3: Walk through tokenizing "when x > 5:"
```
'w' → readIdentifierOrKeyword() → "when" → WHEN token
' ' → skipSpaces()
'x' → readIdentifierOrKeyword() → "x" → IDENTIFIER token
' ' → skipSpaces()
'>' → readSymbol() → GT token
' ' → skipSpaces()
'5' → readNumber() → NUMBER(5) token
':' → readSymbol() → COLON token
```

### Q4: What about undefined variables?
```
set y = x + 5  (x not defined)

Parse: AssignInstruction("y", BinaryOpNode(VariableNode("x"), ...))
Execute: 
  - left.evaluate() calls VariableNode("x").evaluate()
  - env.get("x") throws RuntimeException
  - Error caught: "Variable not defined: x"
```

### Q5: What are the limitations/bugs?
```
1. Comparison operators same precedence as arithmetic
2. Nested loops don't parse correctly
3. No type coercion (unless +)
4. No functions, no arrays, no objects
5. Single global scope only
```

---

## 🎓 KEY CONCEPTS (With Analogies)

### Lexical Analysis (Tokenizer)
**Analogy**: Breaking English sentence into words
```
"The quick brown fox" → ["The", "quick", "brown", "fox"]
```

### Syntax Analysis (Parser)
**Analogy**: Arranging words into grammatically correct sentence
```
["the", "fox", "brown", "quick"] → Invalid order
["The", "quick", "brown", "fox"] → Valid sentence (rearranged)
```

### Execution (Interpreter)
**Analogy**: Understanding what the sentence means
```
"The quick brown fox jumps over the lazy dog"
↓ (interpret and execute meaning)
Mental image of the scene
```

### Operator Precedence
**Analogy**: Order of operations in Math class
```
2 + 3 * 4
First multiply (3*4=12), then add (2+12=14)
NOT (2+3)*4 = 20
```

### Recursive Descent
**Analogy**: Tree traversal
```
     +
    / \
   2   *
      / \
     3   4
     
Walk: 2 → + → 3 → * → 4 → evaluate
```

---

## ⚠️ COMMON MISTAKES IN VIVA

| Mistake | What NOT to say | Say Instead |
|---------|-----------------|-------------|
| Forget phases | "I parse and execute" | "I tokenize, then parse, then execute" |
| Wrong precedence | "*" and "+" same precedence | "* higher than +, handled by method nesting" |
| Lookahead confusion | "Parser sees whole file" | "Parser 1-token lookahead (via current())" |
| Type system | "Anything works" | "Double ops, String concat, comparisons return bool" |
| Loops | "Nested loops work fine" | "⚠️ There's a known issue with nested loops" |

---

## 📊 CODE STATISTICS

```
Tokenizer.java         ~300 lines
Parser.java            ~200 lines
Interpreter.java       ~50 lines
Environment.java       ~50 lines
Token.java             ~20 lines
TokenType.java         ~10 lines

Instruction classes    ~150 lines total
  ├─ AssignInstruction
  ├─ PrintInstruction
  ├─ IfInstruction
  └─ RepeatInstruction

Expression classes     ~150 lines total
  ├─ NumberNode
  ├─ StringNode
  ├─ VariableNode
  └─ BinaryOpNode

Main.java              ~40 lines

TOTAL: ~1000 lines (without comments)
```

---

## 🚀 VIVA OPENING STATEMENT

Prepare this 30-second intro:

```
"Our ZARA interpreter is a complete language implementation in Java.

It has three main phases:

First, TOKENIZATION - we scan the source code character by character,
recognizing numbers, strings, keywords, and symbols.

Second, PARSING - we use recursive descent parsing to build an Abstract
Syntax Tree (AST) that respects operator precedence and grammar rules.

Third, EXECUTION - we walk the AST and execute each instruction,
maintaining an environment for variable storage.

The key insight is that operator precedence is handled through the
nesting of parser methods: parseTerm() handles * and /, parseExpression()
handles + - and comparisons, and parsePrimary() handles literals and variables.

Let me show you an example: set x = 2 + 3 * 4

Tokenizer produces: [SET, IDENTIFIER(x), EQUALS, NUMBER(2), PLUS, NUMBER(3), MULTIPLY, NUMBER(4)]

Parser builds this AST:
       =
      / \
     x   +
        / \
       2   *
          / \
         3   4

Interpreter evaluates:
- 3 * 4 = 12
- 2 + 12 = 14
- Store in x

So the result is 14, not 20 - proving our precedence works correctly."
```

---

## 💡 IF EXAMINER ASKS...

| If they ask... | Quick answer |
|---|---|
| "Why 3 phases?" | Separation of concerns. Each phase independent. Easy debugging. |
| "How does precedence work?" | Method nesting. parseTerm for *, parseExpression for +. |
| "What's an AST?" | Tree representation of program. Nodes = operations, leaves = values. |
| "Why HashMap for environment?" | O(1) lookup. Dynamic size. Natural fit for symbol table. |
| "Recursive descent vs LL(k)?" | Recursive descent simpler. Manual control. LL(k) uses tables. |
| "What about errors?" | Null checks everywhere. Throw RuntimeException with line info. |
| "Type coercion?" | Numbers for arithmetic. Strings for concat. Comparisons return bool. |
| "Nested loops bug?" | Current code stops at next LOOP keyword. Should track indentation. |

---

## 🎯 PRACTICE QUESTIONS

Before viva, answer these without looking:

1. What are the three phases of interpretation?
2. How do you distinguish between `=` and `==` in tokenizer?
3. Why does `2 + 3 * 4` evaluate to 14 and not 20?
4. How does recursive descent parsing work for parentheses?
5. What happens if variable is not defined?
6. How does `when` condition work with boolean evaluation?
7. How many times does `loop 3: show i` body execute?
8. What's the difference between Token and Expression?
9. Why is Environment a HashMap?
10. What's wrong with nested loops in current parser?

---

## ✅ FINAL CHECKLIST (Day of Viva)

- [ ] Understand the three-phase pipeline completely
- [ ] Know the grammar rules by heart
- [ ] Be ready to draw AST diagrams
- [ ] Prepare 2-3 edge cases you handled
- [ ] Have improvements ready to suggest
- [ ] Practice saying "I'm not 100% sure, but..." (honesty helps!)
- [ ] Smile and be confident
- [ ] Speak clearly and don't rush

---

## 🎉 YOU'RE READY!

This interpreter demonstrates:
✅ Compiler design knowledge
✅ OOP principles (inheritance, polymorphism)
✅ Data structures (trees, maps, lists)
✅ Algorithms (recursive descent, tree walking)
✅ Error handling and validation
✅ Problem-solving ability

**Go ace that viva!** 🚀

---

*Created with ❤️ for ZARA Interpreter viva preparation*
*Last updated: April 18, 2026*
