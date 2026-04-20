class FunctionCallNode implements Expression {
    String name;
    List<Expression> args;
}
🔥 ADD THIS CODE inside evaluate()
@Override
public Object evaluate(Environment env) {

    // =========================
    // 🔥 BUILT-IN: len()
    // =========================
    if (name.equals("len")) {

        // 1 argument check
        if (args.size() != 1) {
            throw new RuntimeException("len() takes exactly 1 argument");
        }

        Object val = args.get(0).evaluate(env);

        // 🔹 String length
        if (val instanceof String) {
            return (double) ((String) val).length();
        }

        // 🔹 Array length
        if (val instanceof List) {
            return (double) ((List<?>) val).size();
        }

        // 🔹 Dictionary length
        if (val instanceof Map) {
            return (double) ((Map<?, ?>) val).size();
        }

        throw new RuntimeException(
            "len() not supported for type: " +
            val.getClass().getSimpleName()
        );
    }

    // =========================
    // 👇 existing function logic
    // =========================
🧠 STEP 2: PARSER CHANGE? ❌

👉 NO change needed
Tum already function calls support karte ho:

len("hello")
🧪 TEST CASES
✅ Case 1: String
show len("hello")

Output:

5
✅ Case 2: Variable
set s = "abc"
show len(s)

Output:

3
✅ Case 3: Array
set arr = [1,2,3,4]
show len(arr)

Output:

4
❌ Case 4: Invalid
show len(10)

Output:

Runtime Error: len() not supported for type Double
🧠 INTERNAL FLOW (VERY IMPORTANT)
len("hello")
   ↓
FunctionCallNode
   ↓
evaluate()
   ↓
String.length()
   ↓
return 5
🧠 MEMORY TRICK
🔥 Always remember:
Tokenizer → read
Parser → build
Evaluator → calculate

👉 .length() = Evaluator ka kaam

🚀 BONUS (CLEAN DESIGN - PRO LEVEL)

Instead of hardcoding:

if (name.equals("len"))

👉 Use:

Map<String, BuiltinFunction> builtins;
💥 FINAL TAKEAWAY

👉 Tumne ab add kiya:

Standard Library Function (len)

👉 Yehi real languages me hota hai (Python, JS, etc.)

🚀 If you want next

I can add:

append(arr, x)
substring()
indexing arr[1]
full string methods like .upper()

Just say 🔥
