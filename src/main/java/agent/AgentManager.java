package agent;

import java.util.ArrayList;
import java.util.List;

public class AgentManager {

    private List<Agent> agents = new ArrayList<>();
    private static final Block root = new Block(0, "ROOT_HASH", "ROOT");

    public Agent addAgent(String name, int port) {
        Agent a = new Agent(name, "localhost", port, root, agents);
        a.startHost();
        agents.add(a);
        return a;
    }

    public Agent getAgent(String name) {
        for (Agent a : agents) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public List<Agent> getAllAgents() {
        return agents;
    }

    public void deleteAgent(String name) {
        final Agent a = getAgent(name);
        if (a != null) {
            a.stopHost();
            agents.remove(a);
        }
    }

    public List<Block> getAgentBlockchain(String name) {
        final Agent agent = getAgent(name);
        if (agent != null) {
            return agent.getBlockchain();
        }
        return null;
    }

    public void deleteAllAgents() {
        for (Agent a : agents) {
            a.stopHost();
        }
        agents.clear();
    }

    public Block createBlock(final String name) {
        final Agent agent = getAgent(name);
        if (agent != null) {
            return agent.createBlock();
        }
        return null;
    }
}
