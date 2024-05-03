package me.ducanh.se.quickstart.kafka;

import io.helidon.common.reactive.Single;
import io.helidon.config.Config;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConfigBuilder;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.reactive.messaging.Message;

public class KafkaEntry {
    private final String bts;
    private final KafkaConnector connector;

    public KafkaEntry() {
        this(Config.global().get("app").get("kafka"));
    }

    KafkaEntry(Config config) {
        this.bts = config.get("bootstrap-servers").asString().get();
        this.connector = KafkaConnector.create();
    }

    public void sendTo(String topic, String mess, Config config) {
        var channel = subcriberChannel(topic);
        Messaging.builder().config(config).publisher(channel, Single.just(mess).map(Message::of))
                .connector(connector)
                .build().start();
    }


    public Channel<String> subcriberChannel(String topic) {
        return Channel.<String>builder()
                .subscriberConfig(KafkaConnector.configBuilder()
                        .bootstrapServers(bts)
                        .topic(topic)
                        .keyDeserializer(StringDeserializer.class)
                        .keySerializer(StringSerializer.class)
                        .valueSerializer(StringSerializer.class)
                        .valueDeserializer(StringDeserializer.class)
                        .build())

                .build();
    }
    public Channel<String> publisherChannel(String topic) {
       return Channel.<String>builder()
               .publisherConfig(KafkaConnector.configBuilder()
                       .bootstrapServers(bts)
                       .groupId("group1")
                       .topic(topic)
                       .autoOffsetReset(KafkaConfigBuilder.AutoOffsetReset.LATEST)
                       .enableAutoCommit(true)
                       .keyDeserializer(StringDeserializer.class)
                       .keySerializer(StringSerializer.class)
                       .valueSerializer(StringSerializer.class)
                       .valueDeserializer(StringDeserializer.class)
                       .build())
               .build();
    }
}
