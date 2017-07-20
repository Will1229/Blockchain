package agent;

public class Protocol {

    public static String ACK = "Ack";
    public static String NACK = "Nack";
    public static String SUCCESS = "Success";

    public String processInput(Object input) {
        String output = "Success";
        if (input instanceof String) {

            if ("Ack".equals(input)) {
                output = "Nack";
            } else if ("Nack".equals(input)) {
                output = "Success";
            } else {
                output = (String) input;
//                System.err.println("Unknown contents of input: " + input);
            }
        } else if (input instanceof Block) {
            System.out.println("Received block: " + input.toString());
            output = "Success";
        } else {
            System.err.println("Unknown type of input: " + input.getClass().getSimpleName());
        }
        return output;
    }
}
