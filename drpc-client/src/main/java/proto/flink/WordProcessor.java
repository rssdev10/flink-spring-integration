package proto.flink;

import org.springframework.util.concurrent.ListenableFuture;

public interface WordProcessor {
    //ListenableFuture<StringList> count(String text);
    ListenableFuture<String> count(String text);
}
