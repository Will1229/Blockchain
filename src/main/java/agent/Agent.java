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

import static agent.Message.MESSAGE_TYPE.NEW_BLOCK;
import static agent.Message.MESSAGE_TYPE.READY;

public class Agent {

    private String name;
    private int port;
    private List<Agent> peers;
    private List<Block> blockchain = new ArrayList<>();

    private ServerSocket serverSocket;
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    private boolean listening = true;

    // for jackson
    public Agent() {
    }

    Agent(String name, final int port, Block root, final List<Agent> agents) {
        this.name = name;
        this.port = port;
        this.peers = agents;
        blockchain.add(root);
    }

    public Block createBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }

        Block previousBlock = getLatestBlock();
        if (previousBlock == null) {
            return null;
        }

        final int index = previousBlock.getIndex() + 1;
        final Block block = new Block(index, previousBlock.getHash(), name);
        System.out.println(String.format("%s created new block %s", name, block.toString()));
        broadcast(block);
        return block;
    }

    void addBlock(Block block) {
        if (isBlockValid(block)) {
            blockchain.add(block);
        }
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    private void broadcast(final Block block) {
        for (Agent peer : peers) {
            send("localhost", peer.getPort(), block);
        }
    }

    private Block getLatestBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    private boolean isBlockValid(final Block block) {
        final Block latestBlock = getLatestBlock();
        if (latestBlock == null) {
            return false;
        }
        final int expected = latestBlock.getIndex() + 1;
        if (block.getIndex() != expected) {
            System.out.println(String.format("Invalid index. Expected: %s Actual: %s", expected, block.getIndex()));
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
                listening = true;
                while (listening) {
                    final AgentServerThread thread = new AgentServerThread(this, serverSocket.accept());
                    thread.start();
                }
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not listen to port " + port);
            }
        });
    }

    public void stopHost() {
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String host, int port, Block block) {
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
                        out.writeObject(new Message.MessageBuilder().withType(NEW_BLOCK).withReceiver(port).withSender(this.port).withBlock(block).build());
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }
}
