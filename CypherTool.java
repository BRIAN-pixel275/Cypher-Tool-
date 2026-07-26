import java.util.NoSuchElementException;
import java.util.Scanner;

public class CypherTool {


    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String EXIT_COMMAND = "exit";

    public enum Operation { ENCRYPT, DECRYPT }

    public enum Cypher {
        ROT13, ATBASH, CAESAR;

        public String label() {
            return switch (this) {
                case ROT13 -> "ROT13";
                case ATBASH -> "Atbash";
                case CAESAR -> "Caesar";
            };
        }
    }

    public static class InputData {
        Operation operation;
        Cypher cypher;
        int shift; // only relevant when cypher == CAESAR
        String message;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the Cypher Tool!");
        System.out.println();

        while (true) {
            InputData input = getInput();

            if (input == null) {
                System.out.println("Goodbye!");
                break;
            }

            String result = (input.operation == Operation.ENCRYPT)
                    ? encrypt(input)
                    : decrypt(input);

            String verb = (input.operation == Operation.ENCRYPT) ? "Encrypted" : "Decrypted";
            System.out.println();
            System.out.println(verb + " message (" + input.cypher.label() + "):");
            System.out.println(result);
            System.out.println();
        }
    }

    public static InputData getInput() {
        InputData data = new InputData();

        Operation operation = readOperation();
        if (operation == null) return null;
        data.operation = operation;

        Cypher cypher = readCypher();
        if (cypher == null) return null;
        data.cypher = cypher;

        if (cypher == Cypher.CAESAR) {
            Integer shift = readShift();
            if (shift == null) return null;
            data.shift = shift;
        }

        String message = readMessage();
        if (message == null) return null;
        data.message = message;

        return data;
    }

    private static Operation readOperation() {
        while (true) {
            System.out.println("Select operation:");
            System.out.println("1. Encrypt");
            System.out.println("2. Decrypt");
            System.out.print("$> ");

            String rawLine = readLineOrNull();
            if (rawLine == null) return null;
            String line = rawLine.trim();
            if (isExit(line)) return null;

            switch (line) {
                case "1":
                    return Operation.ENCRYPT;
                case "2":
                    return Operation.DECRYPT;
                default:
                    System.out.println("Invalid choice: \"" + line + "\". Please enter 1 or 2 (or type 'exit' to quit).");
                    System.out.println();
            }
        }
    }

    private static Cypher readCypher() {
        while (true) {
            System.out.println("Select cypher:");
            System.out.println("1. ROT13");
            System.out.println("2. Atbash");
            System.out.println("3. Caesar");
            System.out.print("$> ");

            String rawLine = readLineOrNull();
            if (rawLine == null) return null;
            String line = rawLine.trim();
            if (isExit(line)) return null;

            switch (line) {
                case "1":
                    return Cypher.ROT13;
                case "2":
                    return Cypher.ATBASH;
                case "3":
                    return Cypher.CAESAR;
                default:
                    System.out.println("Invalid choice: \"" + line + "\". Please enter 1, 2 or 3 (or type 'exit' to quit).");
                    System.out.println();
            }
        }
    }

    private static Integer readShift() {
        while (true) {
            System.out.print("Enter shift amount (1-25): ");

            String rawLine = readLineOrNull();
            if (rawLine == null) return null;
            String line = rawLine.trim();
            if (isExit(line)) return null;

            try {
                int shift = Integer.parseInt(line);
                if (shift >= 1 && shift <= 25) {
                    return shift;
                }
                System.out.println("Shift must be a whole number between 1 and 25.");
            } catch (NumberFormatException e) {
                System.out.println("\"" + line + "\" is not a valid number. Please enter a whole number between 1 and 25 (or type 'exit' to quit).");
            }
            System.out.println();
        }
    }

    private static String readMessage() {
        while (true) {
            System.out.print("Enter the message: ");

            String rawLine = readLineOrNull();
            if (rawLine == null) return null;
            String trimmed = rawLine.trim();

            if (isExit(trimmed)) {
                // "exit" is also a perfectly valid message someone might want to encrypt.
                // Don't silently swallow it as a quit command - ask once, explicitly.
                System.out.println("Type 'exit' again to quit, or enter a message to continue (it won't be treated as exit this time).");
                System.out.print("$> ");

                String confirmLine = readLineOrNull();
                if (confirmLine == null) return null;
                String confirmTrimmed = confirmLine.trim();

                if (isExit(confirmTrimmed)) return null;

                if (confirmTrimmed.isEmpty()) {
                    System.out.println("Message cannot be empty. Please try again (or type 'exit' to quit).");
                    System.out.println();
                    continue;
                }

                return confirmTrimmed;
            }

            if (trimmed.isEmpty()) {
                System.out.println("Message cannot be empty. Please try again (or type 'exit' to quit).");
                System.out.println();
                continue;
            }

            return trimmed;
        }
    }

    private static boolean isExit(String s) {
        return s.equalsIgnoreCase(EXIT_COMMAND);
    }

    // If the input stream is closed or exhausted (e.g. piped/redirected input running out),
    // treat it the same as the user typing "exit" instead of letting the exception surface.
    private static String readLineOrNull() {
        try {
            return SCANNER.nextLine();
        } catch (NoSuchElementException | IllegalStateException e) {
            return null;
        }
    }

    private static String encrypt(InputData data) {
        return switch (data.cypher) {
            case ROT13 -> encryptRot13(data.message);
            case ATBASH -> encryptAtbash(data.message);
            case CAESAR -> encryptCaesar(data.message, data.shift);
        };
    }

    private static String decrypt(InputData data) {
        return switch (data.cypher) {
            case ROT13 -> decryptRot13(data.message);
            case ATBASH -> decryptAtbash(data.message);
            case CAESAR -> decryptCaesar(data.message, data.shift);
        };
    }

    public static String encryptRot13(String s) {
        return shiftAlphabetic(s, 13);
    }

    public static String decryptRot13(String s) {
        // ROT13 is its own inverse: shifting by 13 twice = shifting by 26 = no change.
        return shiftAlphabetic(s, 13);
    }

    public static String encryptAtbash(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append((char) ('Z' - (c - 'A')));
            } else if (c >= 'a' && c <= 'z') {
                sb.append((char) ('z' - (c - 'a')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String decryptAtbash(String s) {
        // Atbash mirrors the alphabet; mirroring twice returns the original.
        return encryptAtbash(s);
    }

    public static String encryptCaesar(String s, int shift) {
        return shiftAlphabetic(s, shift);
    }

    public static String decryptCaesar(String s, int shift) {
        return shiftAlphabetic(s, 26 - shift);
    }

    private static String shiftAlphabetic(String s, int shift) {
        int normalizedShift = ((shift % 26) + 26) % 26;
        StringBuilder sb = new StringBuilder(s.length());

        for (char c : s.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append((char) ('A' + (c - 'A' + normalizedShift) % 26));
            } else if (c >= 'a' && c <= 'z') {
                sb.append((char) ('a' + (c - 'a' + normalizedShift) % 26));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}