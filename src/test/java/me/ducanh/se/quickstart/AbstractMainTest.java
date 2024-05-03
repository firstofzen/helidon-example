package me.ducanh.se.quickstart;


import io.helidon.http.Status;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import io.helidon.webclient.api.ClientResponseTyped;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.testing.junit5.SetUpRoute;

import me.ducanh.se.quickstart.kafka.KafkaEntry;
import me.ducanh.se.quickstart.router.Router;
import org.junit.jupiter.api.Test;
import jakarta.json.JsonObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.containsString;

abstract class AbstractMainTest {

    private final Http1Client client;

    protected AbstractMainTest(Http1Client client) {
        this.client = client;
    }

    @SetUpRoute
    static void routing(HttpRouting.Builder builder) {
        Router.routing(builder);
    }

    @Test
    void testFoo() {
        Thread.startVirtualThread(() -> {
            var connector = KafkaConnector.create();
            var kafkaEntry = new KafkaEntry();
            Messaging.builder().connector(connector).listener(kafkaEntry.publisherChannel("topic1"), mess -> {
                System.out.println("message: "+mess);
            }).build().start();
        });
    }

    @Test
    void testGreeting() {
        ClientResponseTyped<JsonObject> response = client.get("/greet").request(JsonObject.class);
        assertThat(response.status(), is(Status.OK_200));
        assertThat(response.entity().getString("message"), is("Hello World!"));
    }

    
    @Test
    void testMetricsObserver() {
        try (Http1ClientResponse response = client.get("/observe/metrics").request()) {
            assertThat(response.status(), is(Status.OK_200));
        }
    }

    
    @Test
    void testSimpleGreet() {
        ClientResponseTyped<String> response = client.get("/simple-greet").request(String.class);
        assertThat(response.status(), is(Status.OK_200));
        assertThat(response.entity(), is("Hello World!"));
    }

}
