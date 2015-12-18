package proto.flink;

import java.util.Properties;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.integration.kafka.serializer.avro.AvroReflectDatumBackedKafkaEncoder;

public class KafkaSink implements SinkFunction<Tuple2<String, String>> {
    private static final long serialVersionUID = -2464020392738269064L;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void invoke(Tuple2<String, String> value) throws Exception {
        //Producer<byte[], byte[]> producer;
        kafka.javaapi.producer.Producer<byte[], byte[]> producer;

        final String topic = Server.topicOutput;
        final Properties props = new Properties();

        props.setProperty("zookeeper.connect", Server.zookeeperHost);
        props.setProperty("bootstrap.servers", Server.kafkaBrokerHosts);
        props.setProperty("group.id", Server.groupId);
        props.setProperty("auto.commit.enable", "false");
        props.setProperty("auto.offset.reset", "largest");

        props.put("metadata.broker.list", "localhost:9092");

//        props.put("serializer.class",
//                "org.apache.kafka.common.serialization.ByteArraySerializer");
//
//        props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

  //      producer = new KafkaProducer<byte[],byte[]>(props);
        producer = new kafka.javaapi.producer.Producer<byte[],byte[]>(
                new ProducerConfig(props));

        AvroReflectDatumBackedKafkaEncoder encoder =
                new AvroReflectDatumBackedKafkaEncoder(java.lang.String.class);

        producer.send(
                new KeyedMessage<byte[], byte[]>(
//                new ProducerRecord<byte[], byte[]>(
                        topic,
                        encoder.toBytes(value.f0),
                        value.f0,
                        encoder.toBytes(value.f1)));
                //new KeyedMessage<Integer, List<String>>(topic, value));
        producer.close();
    }
}
