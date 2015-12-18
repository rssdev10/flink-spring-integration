package proto.flink;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;

public class WordProcessor {

    public static DataStream<Tuple2<String, String>> count(
            DataStream<Tuple2<String, String>> input) {
        return input
                .flatMap(new LineSplitter())
                .keyBy(1)
                .sum(2)
                .keyBy(0)
                //.fold(new ArrayList<String>(),new StringsReducer());
                .fold( new Tuple2<String, String>("", ""),
                        new StringsReducer());
    }

    public static class LineSplitter implements
            FlatMapFunction<Tuple2<String, String>,
                            Tuple3<String, String, Integer>> {

        private static final long serialVersionUID = -7440828001060308528L;

        public void flatMap(Tuple2<String, String> sentence,
                Collector<Tuple3<String, String, Integer>> out) throws Exception {
            for (String word : sentence.f1.split(" ")) {
                out.collect(new Tuple3<String, String, Integer>(
                        sentence.f0, word, 1));
            }
        }
    }

    public static class StringsReducer implements
        FoldFunction<Tuple3<String, String, Integer>, Tuple2<String, String>> {

        private static final long serialVersionUID = -393468670700262115L;
/*
        public List<String> fold(List<String> list,
                Tuple2<String, Integer> arg1) throws Exception {
            list.add(arg1.toString());
            return list;
        }
*/

        public Tuple2<String, String> fold(Tuple2<String, String> list,
                Tuple3<String, String, Integer> arg1) throws Exception {
            list.f0 = arg1.f0;
            list.f1 = list.f1
                        .concat(String.format("%s: %d\n", arg1.f1, arg1.f2));
            return list;
        }

    }
}
