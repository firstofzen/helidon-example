package me.ducanh.se.quickstart.handler.ws;

import io.helidon.config.Config;
import io.helidon.websocket.WsListener;
import io.helidon.websocket.WsSession;
import me.ducanh.se.quickstart.kafka.KafkaEntry;

public record Ws() implements WsListener {
    public void onMessage(WsSession session, String message, boolean last) {
        System.out.println("Received message: " + message);
        var kafka =  new KafkaEntry();
        kafka.sendTo("topic1", message, Config.create());
    }

}