# 📊 ZARA INTERPRETER - VISUAL DIAGRAMS & FLOWCHARTS

---

## 🎬 COMPLETE INTERPRETER FLOW (Visual)

```
┌─────────────────────────────────────────────────────────────────┐
│                          .zara FILE                              │
│                   "set x = 10 + 5"                               │
└────────────────────────┬──────────────────────────────────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │   PHASE 1: TOKENIZATION           │
         │   (Tokenizer.java)                │
         │                                   │
         │   Process:                        │
         │   - Scan character by character   │
         │   - Identify token types          │
         │   - Track line numbers            │
         │   - Handle escape sequences       │
         └───────────────┬───────────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │  OUTPUT: TOKEN LIST               │
         │                                   │
         │  [SET, IDENTIFIER(x), EQUALS,    │
         │   NUMBER(10), PLUS, NUMBER(5),   │
         │   EOF]                            │
         └───────────────┬───────────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │   PHASE 2: PARSING                │
         │   (Parser.java)                   │
         │                                   │
         │   Process:                        │
         │   - Recursive descent             │
         │   - Grammar rule matching         │
         │   - Operator precedence           │
         │   - AST construction              │
         └───────────────┬───────────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │  OUTPUT: AST (Instruction list)   │
         │                                   │
         │  [AssignInstruction(              │
         │    "x",                           │
         │    BinaryOpNode(                  │
         │      NumberNode(10),              │
         │      "+",                         │
         │      NumberNode(5)                │
         │    )                              │
         │  )]                               │
         └───────────────┬───────────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │   PHASE 3: EXECUTION              │
         │   (Interpreter.java +             │
         │    Environment.java)              │
         │                                   │
         │   Process:                        │
         │   - Walk AST                      │
         │   - Evaluate expressions          │
         │   - Manage variables              │
         │   - Execute side effects          │
         └───────────────┬───────────────────┘
                         │
                         ↓
         ┌───────────────────────────────────┐
         │  ENVIRONMENT (After execution)    │
         │                                   │
         │  {                                │
         │    "x" → 15.0                     │
         │  }                                │
         └───────────────────────────────────┘
```

---

## 🌳 TOKENIZATION DETAILED FLOWCHART

```
TOKENIZE: "set x = 10"

START at pos=0
│
├─ pos=0, char='s'
│  ├─ isLetter('s')? YES
│  ├─ readIdentifierOrKeyword()
│  │  └─ Read: 's','e','t' → "set"
│  │     Check keyword: "set" → MATCH
│  └─ EMIT Token(SET, "set", 1)
│     pos=3
│
├─ pos=3, char=' '
│  ├─ isWhitespace(' ')? YES
│  ├─ skipSpaces()
│  │  └─ Skip whitespace
│  └─ pos=4
│
├─ pos=4, char='x'
│  ├─ isLetter('x')? YES
│  ├─ readIdentifierOrKeyword()
│  │  └─ Read: 'x' → "x"
│  │     Check keyword: "x" → NOT keyword
│  └─ EMIT Token(IDENTIFIER, "x", 1)
│     pos=5
│
├─ pos=5, char=' '
│  ├─ skipSpaces() → pos=6
│
├─ pos=6, char='='
│  ├─ readSymbol()
│  │  └─ Check next char (pos+1='1')
│  │     Is '='? NO → Single char symbol
│  └─ EMIT Token(EQUALS, "=", 1)
│     pos=7
│
├─ pos=7, char=' '
│  ├─ skipSpaces() → pos=8
│
├─ pos=8, char='1'
│  ├─ isDigit('1')? YES
│  ├─ readNumber()
│  │  └─ Read: '1','0' → "10"
│  │     Check format: valid
│  └─ EMIT Token(NUMBER, "10", 1)
│     pos=10
│
└─ pos=10 >= source.length()
   ├─ Exit loop
   └─ EMIT Token(EOF, "", 1)

OUTPUT: [SET, IDENTIFIER(x), EQUALS, NUMBER(10), EOF]
```

---

## 📐 PARSER GRAMMAR TREE

```
parse()
  │
  └─ parseInstruction() × many
      │
      ├─ parseAssign()
      │  ├─ consume(SET)
      │  ├─ consume(IDENTIFIER)
      │  ├─ consume(EQUALS)
      │  ├─ parseExpression()
      │  └─ return AssignInstruction
      │
      ├─ parsePrint()
      │  ├─ consume(SHOW)
      │  ├─ parseExpression()
      │  └─ return PrintInstruction
      │
      ├─ parseIf()
      │  ├─ consume(WHEN)
      │  ├─ parseExpression()
      │  ├─ consume(COLON)
      │  ├─ parseInstruction()
      │  └─ return IfInstruction
      │
      └─ parseLoop()
         ├─ consume(LOOP)
         ├─ consume(NUMBER)
         ├─ consume(COLON)
         ├─ loop {
         │  └─ parseInstruction() × while not EOF/LOOP
         │  }
         └─ return RepeatInstruction

parseExpression()
  │
  ├─ parseTerm()
  │  │
  │  ├─ parsePrimary()
  │  │  │
  │  │  ├─ NUMBER → NumberNode
  │  │  ├─ STRING → StringNode
  │  │  ├─ IDENTIFIER → VariableNode
  │  │  └─ LPAREN → parseExpression() [recursion]
  │  │
  │  └─ while MULTIPLY/DIVIDE
  │     └─ BinaryOpNode(left, op, parsePrimary())
  │
  └─ while PLUS/MINUS/GT/LT/EQEQ
     └─ BinaryOpNode(left, op, parseTerm())
```

---

## 🌲 EXAMPLE: PARSING "2 + 3 * 4"

```
Tokens: [NUMBER(2), PLUS, NUMBER(3), MULTIPLY, NUMBER(4), EOF]

parseExpression() called
│
├─ left = parseTerm()
│  │
│  ├─ left = parsePrimary()
│  │  └─ pos=0: NUMBER(2) → NumberNode(2)
│  │
│  ├─ current() = PLUS (not * or /)
│  └─ return NumberNode(2)
│
├─ current() = PLUS ✓
│
├─ op = "+", pos++
│
├─ right = parseTerm()
│  │
│  ├─ left = parsePrimary()
│  │  └─ pos=2: NUMBER(3) → NumberNode(3)
│  │
│  ├─ current() = MULTIPLY ✓
│  │
│  ├─ op = "*", pos++
│  │
│  ├─ right = parsePrimary()
│  │  └─ pos=4: NUMBER(4) → NumberNode(4)
│  │
│  ├─ left = BinaryOpNode(NumberNode(3), "*", NumberNode(4))
│  │
│  ├─ current() = EOF (not * or /)
│  └─ return BinaryOpNode(3, "*", 4)
│
├─ left = BinaryOpNode(NumberNode(2), "+", BinaryOpNode(3, "*", 4))
│
├─ current() = EOF (not + - > < ==)
│
└─ return BinaryOpNode(2, "+", BinaryOpNode(3, "*", 4))

RESULTING AST:
       +
      / \
     2   *
        / \
       3   4

EVALUATION:
├─ Evaluate left: 2 = 2.0
├─ Evaluate right: BinaryOpNode(3, "*", 4)
│  ├─ Evaluate left: 3 = 3.0
│  ├─ Evaluate right: 4 = 4.0
│  ├─ Compute: 3.0 * 4.0 = 12.0
│  └─ return 12.0
├─ Compute: 2.0 + 12.0 = 14.0
└─ return 14.0

OUTPUT: 14.0 ✓
```

---

## 🔄 EXECUTION FLOWCHART

```
Interpreter.run(sourceCode)
│
├─ Validate input (not null, not empty)
│
├─ TOKENIZE
│  ├─ Tokenizer tokenizer = new Tokenizer(sourceCode)
│  └─ List<Token> tokens = tokenizer.tokenize()
│
├─ Validate tokens (not null, not empty)
│
├─ PARSE
│  ├─ Parser parser = new Parser(tokens)
│  └─ List<Instruction> instructions = parser.parse()
│
├─ Validate instructions (not null)
│
├─ EXECUTE
│  ├─ Environment env = new Environment()
│  │
│  └─ for (Instruction instr : instructions)
│     │
│     ├─ if instr = AssignInstruction
│     │  ├─ expr.evaluate(env) → value
│     │  └─ env.set(name, value)
│     │
│     ├─ if instr = PrintInstruction
│     │  ├─ expr.evaluate(env) → value
│     │  └─ System.out.println(value)
│     │
│     ├─ if instr = IfInstruction
│     │  ├─ condition.evaluate(env) → boolean
│     │  ├─ if true:
│     │  │   └─ thenBranch.execute(env)
│     │  └─ else if elseBranch exists:
│     │      └─ elseBranch.execute(env)
│     │
│     └─ if instr = RepeatInstruction
│        └─ for i = 0 to times-1:
│           └─ for (Instruction body : body)
│              └─ body.execute(env)
│
└─ Done!
```

---

## 🔗 EXPRESSION EVALUATION TREE

```
BinaryOpNode.evaluate(env)
│
├─ left.evaluate(env)
│  │
│  ├─ if NumberNode: return 10.0
│  ├─ if StringNode: return "hello"
│  ├─ if VariableNode: 
│  │  └─ env.get(name) → value
│  └─ if BinaryOpNode: [RECURSIVE]
│
├─ right.evaluate(env) [same as above]
│
├─ Type check
│  ├─ if both Double:
│  │  ├─ Arithmetic: + - * /
│  │  └─ Comparison: > < ==
│  ├─ else if op == "+":
│  │  └─ String concatenation
│  └─ else if op == "==":
│     └─ Object equality
│
└─ return result
```

---

## 🔐 ENVIRONMENT STATE DIAGRAM

```
Initial State:
env = { }

After: set x = 10
env = {
  "x" → 10.0
}

After: set y = 3
env = {
  "x" → 10.0,
  "y" → 3.0
}

After: set x = x + y
env = {
  "x" → 13.0,  ← Updated!
  "y" → 3.0
}

Variable Lookup:
env.get("x") → 13.0 ✓
env.get("z") → RuntimeException: "Variable not defined: z" ✗
```

---

## 🔀 STRING TOKENIZATION EXAMPLE

```
Input: "Hello\"World"

START pos=0, current='"'
│
├─ readString() called
│  ├─ pos++ → pos=1 (skip opening quote)
│  │
│  ├─ LOOP: while pos < length
│  │  │
│  │  ├─ pos=1, char='H': sb.append('H')
│  │  ├─ pos=2, char='e': sb.append('e')
│  │  ├─ pos=3, char='l': sb.append('l')
│  │  ├─ pos=4, char='l': sb.append('l')
│  │  ├─ pos=5, char='o': sb.append('o')
│  │  ├─ pos=6, char='\': [ESCAPE SEQUENCE]
│  │  │  ├─ pos++ → pos=7
│  │  │  ├─ next = '"'
│  │  │  └─ sb.append('"')  [Add quote to string]
│  │  ├─ pos=8, char='W': sb.append('W')
│  │  ├─ pos=9, char='o': sb.append('o')
│  │  ├─ pos=10, char='r': sb.append('r')
│  │  ├─ pos=11, char='l': sb.append('l')
│  │  ├─ pos=12, char='d': sb.append('d')
│  │  ├─ pos=13, char='"': BREAK (closing quote)
│  │  │
│  │  └─ EXIT LOOP
│  │
│  └─ pos++ → pos=14 (skip closing quote)
│
└─ EMIT Token(STRING, 'Hello"World', lineNumber)

Note: The actual string content is:
Hello"World
(The quotes are escape sequences, not delimiters inside string)
```

---

## 🔢 NUMBER PARSING FSM

```
State Machine for Number Reading:

START
  │
  └─ [digit] → READ_DIGIT
                 │
                 ├─ [digit] → READ_DIGIT (continue)
                 ├─ [dot] → DOT_FOUND
                 │           │
                 │           ├─ [digit] → READ_DECIMAL
                 │           │             │
                 │           │             ├─ [digit] → READ_DECIMAL (continue)
                 │           │             └─ [non-digit] → DONE
                 │           │
                 │           └─ [non-digit] → ERROR (trailing dot)
                 │
                 └─ [non-digit] → DONE

Examples:
"123" → [123] → DONE ✓
"12.34" → [12.34] → DONE ✓
"12." → [12.] → ERROR ✗ (trailing dot)
".12" → [.12] → ERROR ✗ (leading dot in different context)
"12.34.56" → [12.34] stops at second dot
```

---

## 🎯 PRECEDENCE HIERARCHY

```
LOWEST  PRECEDENCE (parsed first, executed last)
│
├─ Level 1: Comparison
│           > < ==
│           Returns: boolean
│
├─ Level 2: Addition/Subtraction
│           + -
│           Returns: number or string (for +)
│
├─ Level 3: Multiplication/Division
│           * /
│           Returns: number
│
├─ Level 4: Unary (NOT IMPLEMENTED)
│           - (negation)
│           ! (logical not)
│
└─ Level 5: Primary (parsed last, executed first)
            Literals, Variables, Parentheses
            Numbers, Strings, Identifiers, (expr)

HIGHEST PRECEDENCE

Implementation via Method Nesting:
parseExpression() ← Level 1 (lowest)
  └─ parseTerm() ← Level 2-3
      └─ parsePrimary() ← Level 5 (highest)
```

---

## 📋 INSTRUCTION DISPATCH

```
parseInstruction()
│
├─ current().getType() == SET?
│  └─ YES → parseAssign() → AssignInstruction
│
├─ current().getType() == SHOW?
│  └─ YES → parsePrint() → PrintInstruction
│
├─ current().getType() == WHEN?
│  └─ YES → parseIf() → IfInstruction
│
├─ current().getType() == LOOP?
│  └─ YES → parseLoop() → RepeatInstruction
│
└─ NO → RuntimeException: "Unknown instruction"

Execution Dispatch:
instruction.execute(env)
│
├─ if AssignInstruction → env.set(name, value)
├─ if PrintInstruction → System.out.println(value)
├─ if IfInstruction → execute if condition is true
└─ if RepeatInstruction → execute body N times
```

---

## 🔴 ERROR HANDLING FLOW

```
Runtime Error Detection:

Tokenizer Level:
├─ Invalid escape sequence → RuntimeException
├─ Unterminated string → RuntimeException
├─ Unexpected character → RuntimeException
└─ Invalid number format → RuntimeException

Parser Level:
├─ Expected token not found → RuntimeException (Syntax Error)
├─ Unmatched parentheses → RuntimeException
└─ Invalid loop count → RuntimeException

Execution Level:
├─ Variable not defined → RuntimeException
├─ Null operand → RuntimeException
├─ Division by zero → RuntimeException
├─ Type mismatch → RuntimeException
└─ Null instruction → RuntimeException

All caught by:
catch (RuntimeException e) {
    throw new RuntimeException("Interpreter Error: " + e.getMessage(), e);
}
```

---

## 🎬 COMPLETE PROGRAM EXECUTION TRACE

```
Program:
set x = 2 + 3
show x

═══════════════════════════════════════════════

TOKENIZATION:

Input: "set x = 2 + 3\nshow x"

Output:
[
  Token(SET, "set", 1),
  Token(IDENTIFIER, "x", 1),
  Token(EQUALS, "=", 1),
  Token(NUMBER, "2", 1),
  Token(PLUS, "+", 1),
  Token(NUMBER, "3", 1),
  Token(NEWLINE, "\n", 2),
  Token(SHOW, "show", 2),
  Token(IDENTIFIER, "x", 2),
  Token(EOF, "", 2)
]

═══════════════════════════════════════════════

PARSING:

Instruction 1:
Token stream: [SET, IDENTIFIER(x), EQUALS, NUMBER(2), PLUS, NUMBER(3), ...]

parseInstruction() → parseAssign()
  ├─ consume(SET) ✓
  ├─ consume(IDENTIFIER) → name = "x"
  ├─ consume(EQUALS) ✓
  └─ parseExpression()
      ├─ parseTerm() → NumberNode(2)
      ├─ current = PLUS
      ├─ parseTerm() → NumberNode(3)
      └─ BinaryOpNode(2, "+", 3)

Result: AssignInstruction("x", BinaryOpNode(2, "+", 3))

Instruction 2:
Token stream: [SHOW, IDENTIFIER(x), EOF]

parseInstruction() → parsePrint()
  ├─ consume(SHOW) ✓
  └─ parseExpression() → VariableNode("x")

Result: PrintInstruction(VariableNode("x"))

Final AST:
[
  AssignInstruction("x", BinaryOpNode(2, "+", 3)),
  PrintInstruction(VariableNode("x"))
]

═══════════════════════════════════════════════

EXECUTION:

Environment: {}

Instruction 1 Execute:
  value.evaluate(env)
    ├─ left.evaluate() = NumberNode(2).evaluate() = 2.0
    ├─ right.evaluate() = NumberNode(3).evaluate() = 3.0
    ├─ 2.0 + 3.0 = 5.0
    └─ return 5.0
  
  env.set("x", 5.0)

Environment after: { "x" → 5.0 }

Instruction 2 Execute:
  value.evaluate(env)
    └─ VariableNode("x").evaluate(env)
        └─ env.get("x") = 5.0
  
  System.out.println(5.0)

═══════════════════════════════════════════════

OUTPUT TO CONSOLE:

5.0
```

---

*These diagrams visualize every aspect of ZARA interpreter!*
*Study these before viva for maximum clarity.* 📚

