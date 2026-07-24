public class InputData {
    private String operation;
    private String cypher;
    private String message;

    public InputData(String operation, String cypher, String message) {
        this.operation = operation;
        this.cypher = cypher;
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public String getCypher() {
        return cypher;
    }

    public String getMessage() {
        return message;
    }
}
