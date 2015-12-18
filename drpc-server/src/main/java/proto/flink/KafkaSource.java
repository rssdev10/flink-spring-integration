package proto.flink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.springframework.integration.kafka.serializer.avro.AvroSpecificDatumBackedKafkaDecoder;

public class KafkaSource implements SourceFunction<Tuple2<String, String>>{

    private static final long serialVersionUID = 314085207690604712L;

    private volatile boolean isRunning = true;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void run(
            SourceFunction.SourceContext<Tuple2<String, String>> ctx)
            throws Exception {

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(Server.topicImput, new Integer(1));

        ConsumerConnector consumerQueries = kafka.consumer.Consumer
                .createJavaConsumerConnector(createConsumerConfig());

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap =
                consumerQueries.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream =
                consumerMap.get(Server.topicImput).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();

        while (isRunning) {
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                AvroSpecificDatumBackedKafkaDecoder decoder =
                        new AvroSpecificDatumBackedKafkaDecoder(String.class);

                ctx.collect(
                        new Tuple2(
                                decoder.fromBytes(msg.key()).toString(),
                                decoder.fromBytes(msg.message()).toString())
                        );
            }
        }
    }

    public void cancel() {
        isRunning = false;
    }

    private ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", Server.zookeeperHost);
        props.put("group.id", Server.groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);
    }
}
