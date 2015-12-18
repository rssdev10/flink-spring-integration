package proto.flink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class Server {
    final public static String topicImput = "si-request";
    final public static String topicOutput = "si-response";
    final public static String groupId = "si-flink";
    final public static String zookeeperHost = "localhost:2181";
    final public static String kafkaBrokerHosts = "localhost:9092";

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        DataStreamSource<Tuple2<String, String>> input =
                env.addSource(new KafkaSource());

        WordProcessor.count(input).addSink(new KafkaSink());

        env.execute();
    }
}
