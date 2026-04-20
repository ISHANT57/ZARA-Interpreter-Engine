🧠 🎯 FEATURES TO ADD
String slicing → s[1:3]
Arrays → [1,2,3]
Functions → def add(a,b): ...
Dictionaries → {"x":10}

new astNode

class ArrayNode implements Expression {
    List<Expression> elements;

    public ArrayNode(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public Object evaluate(Environment env) {
        List<Object> list = new ArrayList<>();
        for (Expression e : elements) {
            list.add(e.evaluate(env));
        }
        return list;
    }
}
🚀 1. ARRAYS [1,2,3]
✅ New AST Node
class ArrayNode implements Expression {
    List<Expression> elements;

    public ArrayNode(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public Object evaluate(Environment env) {
        List<Object> list = new ArrayList<>();
        for (Expression e : elements) {
            list.add(e.evaluate(env));
        }
        return list;
    }
}
✅ Parser Add (in parsePrimary())
if (t.getType() == TokenType.LBRACKET) {
    pos++; // [

    List<Expression> elements = new ArrayList<>();

    if (current().getType() != TokenType.RBRACKET) {
        elements.add(parseExpression());

        while (current().getType() == TokenType.COMMA) {
            pos++;
            elements.add(parseExpression());
        }
    }

    consume(TokenType.RBRACKET); // ]

    return new ArrayNode(elements);
}
🚀 2. STRING / ARRAY SLICING s[1:3]
✅ New AST Node
class SliceNode implements Expression {
    Expression target;
    Expression start;
    Expression end;

    public SliceNode(Expression target, Expression start, Expression end) {
        this.target = target;
        this.start = start;
        this.end = end;
    }

    @Override
    public Object evaluate(Environment env) {
        Object val = target.evaluate(env);
        int s = ((Double) start.evaluate(env)).intValue();
        int e = ((Double) end.evaluate(env)).intValue();

        if (val instanceof String) {
            return ((String) val).substring(s, e);
        }

        if (val instanceof List) {
            return ((List<?>) val).subList(s, e);
        }

        throw new RuntimeException("Slicing not supported");
    }
}
✅ Parser Add

Inside parsePrimary() after identifier:

Expression base = new VariableNode(t.getValue());
pos++;

if (current().getType() == TokenType.LBRACKET) {
    pos++; // [

    Expression start = parseExpression();
    consume(TokenType.COLON);
    Expression end = parseExpression();

    consume(TokenType.RBRACKET);

    return new SliceNode(base, start, end);
}

return base;
🚀 3. DICTIONARY { "x":10 }
✅ New AST Node
class DictNode implements Expression {
    Map<String, Expression> map;

    public DictNode(Map<String, Expression> map) {
        this.map = map;
    }

    @Override
    public Object evaluate(Environment env) {
        Map<String, Object> result = new HashMap<>();

        for (String key : map.keySet()) {
            result.put(key, map.get(key).evaluate(env));
        }

        return result;
    }
}
✅ Parser Add
if (t.getType() == TokenType.LBRACE) {
    pos++; // {

    Map<String, Expression> map = new HashMap<>();

    if (current().getType() != TokenType.RBRACE) {
        while (true) {
            String key = consume(TokenType.STRING).getValue();
            consume(TokenType.COLON);

            Expression value = parseExpression();
            map.put(key, value);

            if (current().getType() != TokenType.COMMA) break;
            pos++;
        }
    }

    consume(TokenType.RBRACE);
    return new DictNode(map);
}
🚀 4. FUNCTIONS
✅ Function Definition Node
class FunctionDefInstruction implements Instruction {
    String name;
    List<String> params;
    List<Instruction> body;

    public FunctionDefInstruction(String name, List<String> params, List<Instruction> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public void execute(Environment env) {
        env.set(name, this);
    }
}
✅ Function Call Node
class FunctionCallNode implements Expression {
    String name;
    List<Expression> args;

    public FunctionCallNode(String name, List<Expression> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public Object evaluate(Environment env) {
        FunctionDefInstruction fn = (FunctionDefInstruction) env.get(name);

        Environment local = new Environment(env);

        for (int i = 0; i < fn.params.size(); i++) {
            local.set(fn.params.get(i), args.get(i).evaluate(env));
        }

        for (Instruction instr : fn.body) {
            instr.execute(local);
        }

        return local.get("__return__");
    }
}
✅ Return Instruction
class ReturnInstruction implements Instruction {
    Expression value;

    public ReturnInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        env.set("__return__", value.evaluate(env));
    }
}
✅ Parser Add (Function)
// def add(a,b): { ... }
private Instruction parseFunction() {
    consume(TokenType.DEF);

    String name = consume(TokenType.IDENTIFIER).getValue();
    consume(TokenType.LPAREN);

    List<String> params = new ArrayList<>();

    if (current().getType() != TokenType.RPAREN) {
        params.add(consume(TokenType.IDENTIFIER).getValue());

        while (current().getType() == TokenType.COMMA) {
            pos++;
            params.add(consume(TokenType.IDENTIFIER).getValue());
        }
    }

    consume(TokenType.RPAREN);
    consume(TokenType.COLON);
    consume(TokenType.LBRACE);

    List<Instruction> body = new ArrayList<>();

    while (current().getType() != TokenType.RBRACE) {
        body.add(parseInstruction());
    }

    consume(TokenType.RBRACE);

    return new FunctionDefInstruction(name, params, body);
}
