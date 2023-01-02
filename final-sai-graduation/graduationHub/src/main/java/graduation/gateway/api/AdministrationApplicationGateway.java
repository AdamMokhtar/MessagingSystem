package graduation.gateway.api;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class AdministrationApplicationGateway {
    private WebTarget serviceTarget;

    public AdministrationApplicationGateway()
    {
        //create connection
        ClientConfig config = new ClientConfig();
        javax.ws.rs.client.Client client = ClientBuilder.newClient(config);
        URI baseURI = UriBuilder.fromUri("http://localhost:9091/administration/students").build();
        serviceTarget = client.target(baseURI);
    }

    public int getEcs(int studentNumber)
    {
        Invocation.Builder requestBuilder = serviceTarget.path(String.valueOf(studentNumber)).request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            String entity = response.readEntity(String.class);
            JSONObject jsonObject = new JSONObject(entity);
            int ecs = jsonObject.getInt("graduationPhaseECs");
            return ecs;
        } else {
            System.err.println(response);
            return -1;
        }
    }
}
