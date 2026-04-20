# 🔵 PART 8: INSTRUCTION.JAVA (Interface/Base Class)

Since this file is referenced but not uploaded, here's what it should look like:

```java
// Instruction Interface (base contract)
public interface Instruction {
    void execute(Environment env);
}
```

OR

```java
// Instruction Abstract Class
public abstract class Instruction {
    public abstract void execute(Environment env);
}
```

### 🔍 Design Pattern: Command Pattern

Instruction एक **command** है जो execute होना चाहता है।

```
Instruction (Contract)
├─ AssignInstruction     // set x = 10
├─ PrintInstruction     // show x
├─ IfInstruction        // when condition: body
└─ RepeatInstruction    // loop n: body
```

---

# 🟢 PART 9: ASSIGNINSTRUCTION.JAVA

Expected implementation:

```java
public class AssignInstruction implements Instruction {
    private final String name;
    private final Expression value;

    public AssignInstruction(String name, Expression value) {
        if (name == null || value == null) {
            throw new RuntimeException("Invalid assignment");
        }
        this.name = name;
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        Object result = value.evaluate(env);
        env.set(name, result);
    }
}
```

### 🔍 Line-by-Line

**Constructor**:
- Variable name को store करो
- Expression (right-hand side) को store करो

**execute()**:
1. Expression को evaluate करो (environment में variables available हैं)
2. Result को environment में store करो

### 💣 Viva Question

**Q1**: "यदि undefined variable को use करो assignment में?"
```
set y = x + 5  (x defined नहीं है)

1. value.evaluate(env) → BinaryOpNode.evaluate()
2. left.evaluate() → VariableNode("x").evaluate()
3. env.get("x") → RuntimeException: "Variable not defined: x"
✓ Caught at execution time (not parsing time)
```

---

# 🔵 PART 10: PRINTINSTRUCTION.JAVA

```java
public class PrintInstruction implements Instruction {
    private final Expression value;

    public PrintInstruction(Expression value) {
        if (value == null) {
            throw new RuntimeException("Invalid print instruction");
        }
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        Object result = value.evaluate(env);
        System.out.println(result);
    }
}
```

### 🔍 Line-by-Line

**execute()**:
1. Expression evaluate करो
2. Result को print करो

### Example:

```zara
set x = 10
show x + 5

Execution:
1. AssignInstruction("x", NumberNode(10)).execute(env)
   → env.set("x", 10.0)
2. PrintInstruction(BinaryOpNode(VariableNode("x"), "+", NumberNode(5))).execute(env)
   → result = 10.0 + 5.0 = 15.0
   → System.out.println(15.0)
   
Output: 15.0
```

---

# 🟡 PART 11: IFINSTRUCTION.JAVA

```java
public class IfInstruction implements Instruction {
    private final Expression condition;
    private final Instruction thenBranch;
    private final Instruction elseBranch;

    public IfInstruction(Expression condition, Instruction thenBranch, Instruction elseBranch) {
        if (condition == null || thenBranch == null) {
            throw new RuntimeException("Invalid if instruction");
        }
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;  // can be null
    }

    @Override
    public void execute(Environment env) {
        Object condValue = condition.evaluate(env);
        
        // Truthy check
        boolean isTrue = isTruthy(condValue);
        
        if (isTrue) {
            thenBranch.execute(env);
        } else if (elseBranch != null) {
            elseBranch.execute(env);
        }
    }

    private boolean isTruthy(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Double) {
            return (Double) value != 0.0;
        }
        if (value instanceof String) {
            return !((String) value).isEmpty();
        }
        return true;
    }
}
```

### 🔍 Line-by-Line

**execute()**:
1. Condition को evaluate करो
2. Result को boolean में convert करो
3. अगर true → thenBranch execute करो
4. अगर false और elseBranch exists → elseBranch execute करो

**isTruthy()**:
```
Boolean: directly use
Double: 0.0 = false, otherwise true
String: empty = false, otherwise true
```

### Example:

```zara
when score > 50:
    show "Pass"

Tokens: [WHEN, IDENTIFIER(score), GT, NUMBER(50), COLON, SHOW, STRING("Pass"), EOF]

Parse:
IfInstruction(
    BinaryOpNode(VariableNode("score"), ">", NumberNode(50)),
    PrintInstruction(StringNode("Pass")),
    null
)

Execute (score = 85):
1. condition.evaluate() → 85.0 > 50.0 = true
2. isTrue = true
3. thenBranch.execute() → print "Pass"

Output: Pass
```

---

# 🟠 PART 12: REPEATINSTRUCTION.JAVA

```java
public class RepeatInstruction implements Instruction {
    private final int times;
    private final List<Instruction> body;

    public RepeatInstruction(int times, List<Instruction> body) {
        if (times < 0 || body == null) {
            throw new RuntimeException("Invalid loop instruction");
        }
        this.times = times;
        this.body = body;
    }

    @Override
    public void execute(Environment env) {
        for (int i = 0; i < times; i++) {
            for (Instruction instr : body) {
                instr.execute(env);
            }
        }
    }
}
```

### 🔍 Line-by-Line

**execute()**:
1. `times` बार loop करो
2. हर iteration में, body के सभी instructions को execute करो

### Example:

```zara
set i = 1
loop 3:
    show i
    set i = i + 1

Parse:
[
    AssignInstruction("i", NumberNode(1)),
    RepeatInstruction(3, [
        PrintInstruction(VariableNode("i")),
        AssignInstruction("i", BinaryOpNode(VariableNode("i"), "+", NumberNode(1)))
    ])
]

Execute:
1. env.set("i", 1.0)

2. RepeatInstruction loop iteration 1:
   - PrintInstruction: print 1.0
   - AssignInstruction: env.set("i", 1.0 + 1.0) = 2.0

3. RepeatInstruction loop iteration 2:
   - PrintInstruction: print 2.0
   - AssignInstruction: env.set("i", 2.0 + 1.0) = 3.0

4. RepeatInstruction loop iteration 3:
   - PrintInstruction: print 3.0
   - AssignInstruction: env.set("i", 3.0 + 1.0) = 4.0

Output:
1.0
2.0
3.0
```

---

# 🟣 PART 13: EXPRESSION.JAVA (Interface)

```java
public interface Expression {
    Object evaluate(Environment env);
}
```

### 🔍 Design Pattern: Visitor / Tree Walking Interpreter

Expression एक **node** है जिसे evaluate करना है।

```
Expression (Contract)
├─ NumberNode       // Leaf: 42
├─ StringNode       // Leaf: "Hello"
├─ VariableNode     // Leaf: x
└─ BinaryOpNode     // Internal: 2 + 3

Tree Example: 2 + 3 * 4
BinaryOpNode(
    NumberNode(2),
    "+",
    BinaryOpNode(NumberNode(3), "*", NumberNode(4))
)

Evaluation: walk the tree recursively
```

---

# 🔴 PART 14: NUMBERNODE.JAVA

```java
public class NumberNode implements Expression {
    private final double value;

    public NumberNode(double value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Environment env) {
        return value;
    }
}
```

### 🔍 Explanation

**Simplest expression**: Just return the value

```
NumberNode(42).evaluate(env) → 42.0
```

---

# 🟠 PART 15: STRINGNODE.JAVA

```java
public class StringNode implements Expression {
    private final String value;

    public StringNode(String value) {
        if (value == null) {
            throw new RuntimeException("String value cannot be null");
        }
        this.value = value;
    }

    @Override
    public Object evaluate(Environment env) {
        return value;
    }
}
```

### 🔍 Explanation

Same as NumberNode, but for strings

```
StringNode("Hello").evaluate(env) → "Hello"
```

---

# 🟡 PART 16: VARIABLENODE.JAVA

```java
public class VariableNode implements Expression {
    private final String name;

    public VariableNode(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Variable name cannot be null/empty");
        }
        this.name = name;
    }

    @Override
    public Object evaluate(Environment env) {
        if (env == null) {
            throw new RuntimeException("Environment is null");
        }
        return env.get(name);  // May throw if not defined
    }
}
```

### 🔍 Explanation

**Variable को resolve करना**: Environment से lookup करना

```
env = { "x" → 10.0 }
VariableNode("x").evaluate(env) → 10.0

env = { }
VariableNode("y").evaluate(env) → RuntimeException: "Variable not defined: y"
```

---

# 🏆 COMPLETE EXECUTION WALK-THROUGH

## Program: 
```zara
set x = 10
set y = 3
set result = x + y * 2
show result
```

## Full Pipeline:

### PHASE 1: TOKENIZATION
```
Input: "set x = 10\nset y = 3\nset result = x + y * 2\nshow result"

Output Tokens:
[
    Token(SET, "set", 1),
    Token(IDENTIFIER, "x", 1),
    Token(EQUALS, "=", 1),
    Token(NUMBER, "10", 1),
    Token(NEWLINE, "\n", 2),
    Token(SET, "set", 2),
    Token(IDENTIFIER, "y", 2),
    Token(EQUALS, "=", 2),
    Token(NUMBER, "3", 2),
    Token(NEWLINE, "\n", 3),
    Token(SET, "set", 3),
    Token(IDENTIFIER, "result", 3),
    Token(EQUALS, "=", 3),
    Token(IDENTIFIER, "x", 3),
    Token(PLUS, "+", 3),
    Token(IDENTIFIER, "y", 3),
    Token(MULTIPLY, "*", 3),
    Token(NUMBER, "2", 3),
    Token(NEWLINE, "\n", 4),
    Token(SHOW, "show", 4),
    Token(IDENTIFIER, "result", 4),
    Token(EOF, "", 4)
]
```

### PHASE 2: PARSING
```
Input: Token list

Parsing steps:
parse() → parseInstruction() loop

Instruction 1: parseAssign()
├─ consume(SET) ✓
├─ consume(IDENTIFIER) → name = "x"
├─ consume(EQUALS) ✓
├─ parseExpression() → NumberNode(10.0)
└─ AssignInstruction("x", NumberNode(10.0))

Instruction 2: parseAssign()
├─ consume(SET) ✓
├─ consume(IDENTIFIER) → name = "y"
├─ consume(EQUALS) ✓
├─ parseExpression() → NumberNode(3.0)
└─ AssignInstruction("y", NumberNode(3.0))

Instruction 3: parseAssign()
├─ consume(SET) ✓
├─ consume(IDENTIFIER) → name = "result"
├─ consume(EQUALS) ✓
├─ parseExpression()
│   ├─ parseTerm() → VariableNode("x")
│   ├─ op = PLUS
│   ├─ parseTerm()
│   │   ├─ parsePrimary() → VariableNode("y")
│   │   ├─ op = MULTIPLY
│   │   ├─ parsePrimary() → NumberNode(2.0)
│   │   └─ BinaryOpNode(VariableNode("y"), "*", NumberNode(2.0))
│   └─ BinaryOpNode(VariableNode("x"), "+", BinaryOpNode(...))
└─ AssignInstruction("result", BinaryOpNode(...))

Instruction 4: parsePrint()
├─ consume(SHOW) ✓
├─ parseExpression() → VariableNode("result")
└─ PrintInstruction(VariableNode("result"))

Output AST:
[
    AssignInstruction("x", NumberNode(10.0)),
    AssignInstruction("y", NumberNode(3.0)),
    AssignInstruction("result", 
        BinaryOpNode(
            VariableNode("x"),
            "+",
            BinaryOpNode(VariableNode("y"), "*", NumberNode(2.0))
        )
    ),
    PrintInstruction(VariableNode("result"))
]
```

### PHASE 3: EXECUTION
```
Initial Environment: {}

Instruction 1 Execute:
├─ name = "x"
├─ value.evaluate(env) = NumberNode(10.0).evaluate() = 10.0
└─ env.set("x", 10.0)
Environment after: { "x" → 10.0 }

Instruction 2 Execute:
├─ name = "y"
├─ value.evaluate(env) = NumberNode(3.0).evaluate() = 3.0
└─ env.set("y", 3.0)
Environment after: { "x" → 10.0, "y" → 3.0 }

Instruction 3 Execute:
├─ name = "result"
├─ value.evaluate(env) = BinaryOpNode.evaluate(env)
│   ├─ left.evaluate(env) = VariableNode("x").evaluate(env) = env.get("x") = 10.0
│   ├─ right.evaluate(env) = BinaryOpNode.evaluate(env)
│   │   ├─ left.evaluate(env) = VariableNode("y").evaluate(env) = 3.0
│   │   ├─ right.evaluate(env) = NumberNode(2.0).evaluate(env) = 2.0
│   │   ├─ op = "*"
│   │   └─ 3.0 * 2.0 = 6.0
│   ├─ op = "+"
│   └─ 10.0 + 6.0 = 16.0
└─ env.set("result", 16.0)
Environment after: { "x" → 10.0, "y" → 3.0, "result" → 16.0 }

Instruction 4 Execute:
├─ value.evaluate(env) = VariableNode("result").evaluate(env)
│   └─ env.get("result") = 16.0
├─ System.out.println(16.0)

Output to console: 16.0
```

---

## Memory Tricks for Each Class:

```
TOKENIZER:        Char-by-char scanner, state machine, lookahead
TOKEN:            Data holder (type, value, line)
TOKENTYPE:        Enum categories
PARSER:           Recursive descent, grammar rules, precedence
INSTRUCTION:      Command pattern, execute() contract
ASSIGNINSTRUCTION: Evaluate expression, store in environment
PRINTINSTRUCTION:  Evaluate expression, output to console
IFINSTRUCTION:     Conditional execution, truthy check
REPEATINSTRUCTION: Loop body N times
EXPRESSION:       Tree node, recursive evaluation
NUMBERNODE:       Return double literal
STRINGNODE:       Return string literal
VARIABLENODE:     Lookup in environment
BINARYOPNODE:     Recursive evaluation of operands, operation

ENVIRONMENT:      HashMap symbol table
INTERPRETER:      Three-phase pipeline coordinator
MAIN:             File I/O, error handling, program entry
```

---

## 💣 CROSS-COMPONENT VIVA QUESTIONS

**Q1**: "Parser में null check है Instruction के लिए, लेकिन Parser null instruction कैसे generate कर सकता है?"
- **Answer**: Theoretically नहीं करना चाहिए, पर defensive programming practice है
- अगर future में नए instruction type add करते हो और भूल जाते हो initialize करने में
- या अगर external code from करते हो जो null दे दे

**Q2**: "Why does Tokenizer track line numbers लेकिन Parser में line information को ignore करते हो (error messages में नहीं दिखता)?"
- **Answer**: 
  - Tokenizer errors के लिए line information ज़रूरी है
  - Parser errors में भी line number include कर सकते हो
  - Current implementation में नहीं है - improvement point है

**Q3**: "Nested loop parsing में infinite loop की possibility है. कैसे fix करते हो?"
- **Answer**:
  ```java
  // Current (problematic):
  while (current().getType() != TokenType.EOF &&
         current().getType() != TokenType.LOOP) {
      body.add(parseInstruction());
  }
  
  // Better (with indentation tracking):
  int startIndent = getIndentLevel();
  while (getIndentLevel() > startIndent) {
      body.add(parseInstruction());
  }
  
  // Or use explicit block delimiters:
  while (current().getType() != TokenType.RBRACE) {
      body.add(parseInstruction());
  }
  ```

**Q4**: "Type coercion में string + number को क्यों allow करते हो?"
- **Answer**:
  - Python और JavaScript में भी string coercion होता है
  - लेकिन strict typing के लिए नहीं करना चाहिए
  - ZARA में implicit coercion nहीं होना चाहिए
  - Better: throw error or explicitly cast

**Q5**: "अगर huge loop करते हो (10000 iterations), performance क्या होगी?"
- **Answer**:
  - हर iteration में सभी instructions को execute करते हो
  - No optimization
  - Tree walking interpreter - slow है
  - Bytecode compiler use करते तो faster होता

---

**THESE 9 CLASSES + MAIN = COMPLETE ZARA INTERPRETER!**

```
Entry: Main.java
  ├─ Tokenizer.java → Token list
  ├─ Parser.java → Instruction list (AST)
  └─ Interpreter.java (with Environment.java)
      ├─ Executes Instruction (polymorphic dispatch)
      │   ├─ AssignInstruction
      │   ├─ PrintInstruction
      │   ├─ IfInstruction
      │   └─ RepeatInstruction
      │
      ├─ Each Instruction calls Expression.evaluate()
      │   ├─ NumberNode
      │   ├─ StringNode
      │   ├─ VariableNode
      │   └─ BinaryOpNode
      │
      └─ Environment stores variables
```

Perfect! You have the complete interpreter! 🎉
