import java.util.Scanner;

public class CypherTool{
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        System.out.println("==========================================");
        System.out.println("Welcome to the Cypher Tool!");
        System.out.println("==========================================");

        //Main application loop
        while(true){
            InputData inputData = getInputData();

            //Exit if requested
            if(inputData==null){
                break;
            }

            String result = processRequest(inputData);

            displayResult( inputData, result);

            System.out.println();
        }
        System.out.println("Thank you for using the Cypher Tool. Goodbye!");
    }

    //Collects and validates user input

    public static InputData  getInputData(){

        return null;
    }

        //determines which cypher to use.

        public static String processRequest(InputData inputData){{

            return "";

            public static void displayResult(InputData inputData, String result){
                //Encryption with ROT13 cypher
                public static String encryptRot13(String s){
                    return "";
                }
                // Decryption with ROT13 cypher
                public static String decryptRot13(String s){

                    return "";
                }

                //Encryption with Atbash cypher

                public static String encryptAtbash(String s){

                    return "";
                }
                // Decryption with Atbash cypher
                public static String decryptAtbash(String s){

                    return "";
                }
                //Encryption with Caesar cypher

                public static String encryptCaesar(String s){

                    return "";
                }
                // Decryption with Caesar cypher
                public static String decryptCaesar(String s){

                    return "";
                }
            }
        }
    }
}