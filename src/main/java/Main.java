import agent.Agent;
import agent.Block;

public class Main {

    private static final Block<String> root = new Block<>(0, "ROOT_HASH", "ROOT");

    public static void main(String[] args) throws Exception {

        Agent a1 = new Agent("Agent1", 1001, root);
        Agent a2 = new Agent("Agent2", 1002, root);
        Agent a3 = new Agent("Agent3", 1003, root);

        a1.startHost();
        a2.startHost();
        a3.startHost();

        Thread.sleep(1000);

        a1.addBlock();

        Thread.sleep(1000);
        a1.stopHost();
        a2.stopHost();
        a3.stopHost();
    }

}
