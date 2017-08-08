package web;

import agent.Agent;
import agent.Block;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AgentControllerTest {

    @LocalServerPort
    private int port;
    private URL baseUrl;

    @Autowired
    private TestRestTemplate template;

    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        baseUrl = new URL("http://localhost:" + port + "/agent");
    }

    @Test
    public void getEmptyAgent() {
        ResponseEntity<String> response = template.getForEntity(baseUrl.toString() + "?name=NOT_EXIST", String.class);
        assertThat(response.getBody(), is(nullValue()));
    }

    @Test
    public void addGetDeleteAgent() throws Exception {
        final String name = "A1";
        final int port = 1001;
        ResponseEntity<String> response = template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), name, port), null, String.class);
        Agent a = mapper.readValue(response.getBody(), Agent.class);
        assertThat(a.getName(), is(name));
        assertThat(a.getPort(), is(port));
        assertThat(a.getBlockchain().size(), is(1));
        response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), name), String.class);
        a = mapper.readValue(response.getBody(), Agent.class);
        assertThat(a.getName(), is(name));
        assertThat(a.getPort(), is(port));
        assertThat(a.getBlockchain().size(), is(1));
        Thread.sleep(1000);
        template.delete(String.format("%s?name=%s", baseUrl.toString(), name));
        response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), name), String.class);
        assertThat(response.getBody(), is(nullValue()));
    }

    @Test
    public void getDeleteAllAgents() throws Exception {
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), "A1", 1001), null, String.class);
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), "A2", 1002), null, String.class);
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), "A3", 1003), null, String.class);
        ResponseEntity<String> response = template.getForEntity(String.format("%s/all", baseUrl.toString()), String.class);
        JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Agent.class);
        List<Agent> a = mapper.readValue(response.getBody(), type);
        assertThat(a.size(), is(3));
        template.delete(String.format("%s/all", baseUrl.toString()));
        response = template.getForEntity(String.format("%s/all", baseUrl.toString()), String.class);
        a = mapper.readValue(response.getBody(), type);
        assertThat(a.size(), is(0));
    }

    @Test
    public void createBlockSingleAgent() throws Exception {
        final String name = "A1";
        final int port = 1001;
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), name, port), null, String.class);
        ResponseEntity<String> response = template.postForEntity(String.format("%s/mine?agent=%s", baseUrl.toString(), name), null, String.class);
        final Block b = mapper.readValue(response.getBody(), Block.class);
        assertThat(b.getIndex(), is(1));
        response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), name), String.class);
        final Agent a = mapper.readValue(response.getBody(), Agent.class);
        assertThat(a.getName(), is(name));
        assertThat(a.getPort(), is(port));
        assertThat(a.getBlockchain().size(), is(2));
        template.delete(String.format("%s?name=%s", baseUrl.toString(), name));
        response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), name), String.class);
        assertThat(response.getBody(), is(nullValue()));

    }

    @Test
    public void createBlockMultiAgent() throws Exception {
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), "A1", 1001), null, String.class);
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), "A2", 1002), null, String.class);
        template.postForEntity(String.format("%s?name=%s&port=%d", baseUrl.toString(), "A3", 1003), null, String.class);

        template.postForEntity(String.format("%s/mine?agent=%s", baseUrl.toString(), "A1"), null, String.class);
        ResponseEntity<String> response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), "A1"), String.class);
        final Agent a1 = mapper.readValue(response.getBody(), Agent.class);
        final String hash = a1.getBlockchain().get(1).getHash();
        response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), "A2"), String.class);
        final Agent a2 = mapper.readValue(response.getBody(), Agent.class);
        assertThat(a2.getBlockchain().get(1).getHash(), is(hash));
        response = template.getForEntity(String.format("%s?name=%s", baseUrl.toString(), "A3"), String.class);
        final Agent a3 = mapper.readValue(response.getBody(), Agent.class);
        assertThat(a3.getBlockchain().get(1).getHash(), is(hash));

        template.postForEntity(String.format("%s/mine?agent=%s", baseUrl.toString(), "A2"), null, String.class);
        template.postForEntity(String.format("%s/mine?agent=%s", baseUrl.toString(), "A3"), null, String.class);

        response = template.getForEntity(String.format("%s/all", baseUrl.toString()), String.class);
        JavaType type = mapper.getTypeFactory().constructParametricType(List.class, Agent.class);
        List<Agent> agents = mapper.readValue(response.getBody(), type);
        assertThat(agents.size(), is(3));
        for (Agent a : agents) {
            assertThat(a.getBlockchain().size(), is(4));
        }

        template.delete(String.format("%s/all", baseUrl.toString()));
    }

}