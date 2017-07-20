package agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static agent.Message.MESSAGE_TYPE.READY;

public class AgentServerThread extends Thread {
    private int port;
    private Socket client;

    public AgentServerThread(final String name, final int serverPort, final Socket client) {
        super(name + System.currentTimeMillis());
        this.port = serverPort;
        this.client = client;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
            Message message = new Message.MessageBuilder().withSender(port).withType(READY).build();
            out.writeObject(message);
            Object fromClient;
            while ((fromClient = in.readObject()) != null) {
                if (fromClient instanceof Message) {
                    System.out.println(String.format("%d received: %s%n", this.port, fromClient.toString()));
                    break;
                }
            }
            client.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
