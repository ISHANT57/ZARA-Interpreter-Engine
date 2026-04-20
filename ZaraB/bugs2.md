repeat instruction -- infinite loop fix

for (int i = 0; i < times; i++) {
    instr.execute(env);
} -- bug

Fix--

private static final int MAX_LOOP = 1_000_000;

if (times > MAX_LOOP) {
    throw new RuntimeException("Loop limit exceeded");
}

long start = System.currentTimeMillis();

for (int i = 0; i < times; i++) {

    if (System.currentTimeMillis() - start > 2000) {
        throw new RuntimeException("Execution timeout");
    }

    for (Instruction instr : body) {
        instr.execute(env);
    }
}


1) ❌ Comparison precedence == arithmetic
✅ Fix: split precedence levels

Before (buggy)

// +, -, >, <, == all together ❌
private Expression parseExpression() {
    Expression left = parseTerm();
    while (isAddOrCmp(current())) {
        String op = current().getValue(); pos++;
        Expression right = parseTerm();
        left = new BinaryOpNode(left, op, right);
    }
    return left;
}

After (correct)

// lowest: comparisons
private Expression parseExpression() {
    Expression left = parseAdditive();
    while (isCmp(current())) {
        String op = current().getValue(); pos++;
        Expression right = parseAdditive();
        left = new BinaryOpNode(left, op, right);
    }
    return left;
}

// +, -
private Expression parseAdditive() {
    Expression left = parseTerm();
    while (isAdd(current())) {
        String op = current().getValue(); pos++;
        Expression right = parseTerm();
        left = new BinaryOpNode(left, op, right);
    }
    return left;
}

// *, /
private Expression parseTerm() {
    Expression left = parsePrimary();
    while (isMul(current())) {
        String op = current().getValue(); pos++;
        Expression right = parsePrimary();
        left = new BinaryOpNode(left, op, right);
    }
    return left;
}
2) ❌ Nested loops don’t parse correctly
✅ Fix: explicit block {} + recursion-safe

Before (buggy)

while (current().getType() != EOF) {   // eats everything ❌
    body.add(parseInstruction());
}

After (correct)

private Instruction parseLoop() {
    consume(TokenType.LOOP);
    int times = Integer.parseInt(consume(TokenType.NUMBER).getValue());
    consume(TokenType.COLON);
    consume(TokenType.LBRACE);

    List<Instruction> body = new ArrayList<>();

    while (current().getType() != TokenType.RBRACE &&
           current().getType() != TokenType.EOF) {

        body.add(parseInstruction());   // supports nested loop ✔

        if (current().getType() == TokenType.NEWLINE) {
            consume(TokenType.NEWLINE);
        }
    }

    if (current().getType() == TokenType.EOF)
        throw new RuntimeException("Missing '}'");

    consume(TokenType.RBRACE);
    return new RepeatInstruction(times, body);
}
3) ❌ No type coercion (except +)
✅ Fix: centralize coercion helpers

Before

if (op.equals("+")) { /* ad-hoc */ }

After

private double toNumber(Object v) {
    if (v instanceof Double) return (Double) v;
    if (v instanceof String) return Double.parseDouble((String) v);
    throw new RuntimeException("Cannot coerce to number: " + v);
}

private String toStr(Object v) {
    return String.valueOf(v);
}

Use in BinaryOpNode

if (op.equals("+")) {
    if (l instanceof String || r instanceof String)
        return toStr(l) + toStr(r);      // Java-like
    return toNumber(l) + toNumber(r);
}
