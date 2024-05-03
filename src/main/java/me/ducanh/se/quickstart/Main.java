
package me.ducanh.se.quickstart;


import io.helidon.logging.common.LogConfig;
import io.helidon.config.Config;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.websocket.WsRouting;
import me.ducanh.se.quickstart.handler.rest.GreetService;
import me.ducanh.se.quickstart.handler.ws.Ws;
import me.ducanh.se.quickstart.kafka.KafkaEntry;
import me.ducanh.se.quickstart.router.Router;

public class Main {

    private Main() {
    }

    public static void main(String[] args) {

        LogConfig.configureRuntime();
        Config config = Config.create();
        Config.global(config);

        Thread.startVirtualThread(() -> {
            var kafka = new KafkaEntry();
            var channel = kafka.publisherChannel("topic1");
            var connector =  KafkaConnector.create();
            Messaging.builder()
                    .connector(connector)
                    .listener(channel, payload -> {
                        System.out.println("message from topic: " + payload);
                    })
                    .build().start();
        });
        Router.init(config);
    }

}