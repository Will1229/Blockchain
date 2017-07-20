package agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static agent.Message.MESSAGE_TYPE.READY;
import static agent.Message.MESSAGE_TYPE.RESPONSE;

public class Agent implements Runnable {

    private String name;
    private int port;

    private ServerSocket serverSocket;
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    private boolean listening = true;

    private List<Block<String>> blockchain = new ArrayList<>();

    public Agent(String name, final int port, Block<String> root) {
        this.name = name;
        this.port = port;
        blockchain.add(root);
    }


    @Override
    public void run() {
        addBlock();
    }

    public void addBlock() {
        if (blockchain.isEmpty()) {
            return;
        }

        Block<String> previousBlock = getLatestBlock();
        if (previousBlock == null) {
            return;
        }

        final int index = previousBlock.getIndex() + 1;
        final Block<String> block = new Block<>(index, previousBlock.getHash(), String.valueOf(index) + "-payload");
        System.out.println(String.format("%s created new block %s", name, block.toString()));
        if (isBlockValid(block)) {
            blockchain.add(block);
            broadcast(block);
        }
    }

    private void broadcast(final Block<String> block) {
//        send("localhost", 1001, block);
        send("localhost", 1002, block);
        send("localhost", 1003, block);
    }

    private Block<String> getLatestBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    private boolean isBlockValid(final Block<String> block) {
        final Block<String> latestBlock = getLatestBlock();
        if (latestBlock == null) {
            return false;
        }
        if (block.getIndex() != latestBlock.getIndex() + 1) {
            System.out.println(String.format("Invalid index. Expected: %s Actual: %s", latestBlock.getIndex() + 1, block.getIndex()));
            return false;
        }
        if (!Objects.equals(block.getPreviousHash(), latestBlock.getHash())) {
            System.out.println("Unmatched hash code");
            return false;
        }


        return true;
    }

    public void startHost() {
        executor.execute(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println(String.format("Server %s started", serverSocket.getLocalPort()));

                while (listening) {
                    final AgentServerThread thread = new AgentServerThread(name, port, serverSocket.accept());
                    thread.start();
                }
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not listen on port " + port);
                System.exit(-1);
            }
        });
    }

    public void stopHost() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String host, int port, Block<?> block) {
        try (
                final Socket peer = new Socket(host, port);
                final ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(peer.getInputStream())) {
            Object fromPeer;
            while ((fromPeer = in.readObject()) != null) {
                if (fromPeer instanceof Message) {
                    final Message msg = (Message) fromPeer;
                    System.out.println(String.format("%d received: %s", this.port, msg.toString()));
                    if (READY == msg.type) {
                        out.writeObject(new Message.MessageBuilder().withType(RESPONSE).withReceiver(port).withSender(this.port).withBlock(block).build());
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
