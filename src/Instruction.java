public interface Instruction {
    // Executes the statement — reads/writes variables via Environment
    void execute(Environment env);
}
