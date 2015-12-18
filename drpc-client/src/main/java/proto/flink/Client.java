package proto.flink;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.concurrent.ListenableFuture;

public class Client {

    static final String testText = "Words count: A AA AAA AA A A";

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("flink-integration-context.xml");

        WordProcessor gw = context.getBean(WordProcessor.class);

        //ListenableFuture<StringList> result = gw.count(testText);
        ListenableFuture<String> result = gw.count(testText);

        for (int timeout = 0;
                !result.isDone() && !result.isCancelled() && timeout < 50;
                timeout++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

        if (result.isDone())
            try {
                //result.get().stream().forEach(System.out::println);
                System.out.println("********************");
                System.out.println(result.get());

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
    }
}
