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


4) ❌ No functions / arrays / objects
✅ Minimal core support (compact)
Arrays
class ArrayNode implements Expression {
    List<Expression> elems;
    public Object evaluate(Environment env) {
        List<Object> out = new ArrayList<>();
        for (var e : elems) out.add(e.evaluate(env));
        return out;
    }
}
Functions (closure + local scope)
class FunctionValue {
    List<String> params;
    List<Instruction> body;
    Environment closure;
}

class FunctionDef implements Instruction {
    String name; List<String> params; List<Instruction> body;
    public void execute(Environment env) {
        env.define(name, new FunctionValue(params, body, env));
    }
}

class CallNode implements Expression {
    String name; List<Expression> args;
    public Object evaluate(Environment env) {
        FunctionValue fn = (FunctionValue) env.get(name);
        Environment local = new Environment(fn.closure);

        for (int i = 0; i < fn.params.size(); i++) {
            local.define(fn.params.get(i), args.get(i).evaluate(env));
        }

        try {
            for (var ins : fn.body) ins.execute(local);
        } catch (ReturnSignal r) {
            return r.value;
        }
        return null;
    }
}

class ReturnSignal extends RuntimeException {
    Object value;
    ReturnSignal(Object v){ this.value=v; }
}

class ReturnInstruction implements Instruction {
    Expression val;
    public void execute(Environment env) {
        throw new ReturnSignal(val.evaluate(env));
    }
}
Objects (Dictionary)
class DictNode implements Expression {
    Map<String, Expression> map;
    public Object evaluate(Environment env) {
        Map<String,Object> out = new HashMap<>();
        for (var k: map.keySet())
            out.put(k, map.get(k).evaluate(env));
        return out;
    }
}
5) ❌ Single global scope only
✅ Fix: lexical scoping (Environment chain)

Before

class Environment {
    Map<String,Object> vars = new HashMap<>();
}

After

class Environment {
    private final Map<String,Object> vars = new HashMap<>();
    private final Environment parent;

    public Environment() { this.parent = null; }
    public Environment(Environment parent) { this.parent = parent; }

    public void define(String k, Object v) { vars.put(k, v); }

    public void set(String k, Object v) {
        if (vars.containsKey(k)) vars.put(k, v);
        else if (parent != null) parent.set(k, v);
        else throw new RuntimeException("Undefined variable: " + k);
    }

    public Object get(String k) {
        if (vars.containsKey(k)) return vars.get(k);
        if (parent != null) return parent.get(k);
        throw new RuntimeException("Undefined variable: " + k);
    }
}

👉 Now:

global scope ✔
function local scope ✔
closures ✔
