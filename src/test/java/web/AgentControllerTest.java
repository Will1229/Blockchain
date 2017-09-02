package web;

import agent.Agent;
import agent.Block;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AgentControllerTest {

    private static final String AGENT1 = "A1";
    private static final String AGENT2 = "A2";
    private static final String AGENT3 = "A3";

    @LocalServerPort
    private int port;
    private URL baseUrl;

    @Autowired
    private TestRestTemplate template;

    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void before() throws Exception {
        baseUrl = new URL("http://localhost:" + port + "/agent");
    }

    @After
    public void after() throws Exception {
        Thread.sleep(500);
        deleteAllAgents();
    }

    @Test
    public void getEmptyAgent() throws IOException {
        final Agent a = getAgent("NOT_EXIST");
        assertThat(a, is(nullValue()));
    }

    @Test
    public void addGetDeleteAgent() throws Exception {
        final String name = AGENT1;
        final int port = 1001;
        Agent a = createAgent(name, port);
        assert a != null;
        assertThat(a.getName(), is(name));
        assertThat(a.getPort(), is(port));
        assertThat(a.getBlockchain().size(), is(1));
        a = getAgent(name);
        assert a != null;
        assertThat(a.getName(), is(name));
        assertThat(a.getPort(), is(port));
        assertThat(a.getBlockchain().size(), is(1));
        Thread.sleep(500);
        deleteAgent(name);
        a = getAgent(name);
        assertThat(a, is(nullValue()));
    }

    @Test
    public void getDeleteAllAgents() throws Exception {
        createAgent(AGENT1, 1001);
        createAgent(AGENT2, 1002);
        createAgent(AGENT3, 1003);
        List<Agent> agents = getAllAgents();
        assert agents != null;
        assertThat(agents.size(), is(3));
        deleteAllAgents();
        Thread.sleep(500);
        agents = getAllAgents();
        assert agents != null;
        assertThat(agents.size(), is(0));
    }

    @Test
    public void createBlockSingleAgent() throws Exception {
        final String name = AGENT1;
        final int port = 1001;
        createAgent(name, port);
        final Block b = mine(name);
        assert b != null;
        assertThat(b.getIndex(), is(1));
        assertThat(b.getCreator(), is(name));
        Agent a = getAgent(name);
        assert a != null;
        assertThat(a.getName(), is(name));
        assertThat(a.getPort(), is(port));
        assertThat(a.getBlockchain().size(), is(2));
        deleteAgent(name);
        a = getAgent(name);
        assertThat(a, is(nullValue()));
    }


    @Test
    public void createBlockMultiAgent() throws Exception {
        createAgent(AGENT1, 1001);
        createAgent(AGENT2, 1002);
        createAgent(AGENT3, 1003);

        mine(AGENT1);
        final Agent a1 = getAgent(AGENT1);
        assert a1 != null;
        final String hash = a1.getBlockchain().get(1).getHash();
        final Agent a2 = getAgent(AGENT2);
        assert a2 != null;
        assertThat(a2.getBlockchain().get(1).getHash(), is(hash));
        final Agent a3 = getAgent(AGENT3);
        assert a3 != null;
        assertThat(a3.getBlockchain().get(1).getHash(), is(hash));

        mine(AGENT2);
        mine(AGENT3);

        List<Agent> agents = getAllAgents();
        assert agents != null;
        assertThat(agents.size(), is(3));
        for (Agent a : agents) {
            assertThat(a.getBlockchain().size(), is(4));
        }

        deleteAllAgents();
    }

    @Test
    public void sendBlockchainToNewAgent() throws Exception {
        createAgent(AGENT1, 1001);
        createAgent(AGENT2, 1002);
        IntStream.range(0, 2).forEach(value -> mine(AGENT1));
        IntStream.range(0, 2).forEach(value -> mine(AGENT2));
        final Agent a1 = getAgent(AGENT1);
        assert a1 != null;
        assertThat(a1.getBlockchain().size(), is(5));
        final Agent a2 = getAgent(AGENT2);
        assert a2 != null;
        assertThat(a2.getBlockchain().size(), is(5));
        final Agent a3 = createAgent(AGENT3, 1003);
        assert a3 != null;
        assertThat(a3.getBlockchain().size(), is(5));
        assertThat(a1.getBlockchain().equals(a3.getBlockchain()), is(true));
        assertThat(a2.getBlockchain().equals(a3.getBlockchain()), is(true));
    }


    private Block mine(final String name) {
        final ResponseEntity<String> response = template.postForEntity(String.format("%s/mine?agent=%s", baseUrl.toString(), name), null, String.class);
        try {
            return response.getBody() == null ? null : mapper.readValue(response.getBody(), Block.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Agent getAgent(final String name) throws IOException {
        final ResponseEntity<String> response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), name), String.class);
        return response.getBody() == null ? null : mapper.readValue(response.getBody(), Agent.class);
    }

    private Agent createAgent(final String name, final int port) throws IOException {
        final ResponseEntity<String> response = template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), name, port), null, String.class);
        return response.getBody() == null ? null : mapper.readValue(response.getBody(), Agent.class);
    }

    private List<Agent> getAllAgents() throws IOException {
        final ResponseEntity<String> response = template.getForEntity(String.format("%s/all", baseUrl.toString()), String.class);
        JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Agent.class);
        return response.getBody() == null ? null : mapper.readValue(response.getBody(), type);
    }

    private void deleteAllAgents() {
        template.delete(String.format("%s/all", baseUrl.toString()));
    }

    private void deleteAgent(final String name) {
        template.delete(String.format("%s?name=%s", baseUrl.toString(), name));
    }
}