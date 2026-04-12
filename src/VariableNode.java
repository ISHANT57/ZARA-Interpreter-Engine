// VariableNode represents a variable in an expression.
// It follows the Expression interface so it can be used like other expressions.
public class VariableNode implements Expression {
    
    // This stores the name of the variable (like "x" or "y").
    private final String name;
    
    // Constructor: when you create a VariableNode, you give it a name.
    public VariableNode(String name) { 
        this.name = name; 
    }

    // The evaluate method looks up the variable's value in the Environment.
    // It asks the Environment for the value that matches the variable's name.
    @Override
    public Object evaluate(Environment env) { 
        return env.get(name); 
    }
}
