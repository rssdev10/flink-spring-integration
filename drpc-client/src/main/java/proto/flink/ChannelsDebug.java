package proto.flink;

import java.util.concurrent.ExecutionException;

import org.springframework.messaging.Message;

public class ChannelsDebug {

    public ChannelsDebug() {
    }

    public Message processMessage(Message<?> message)
            throws InterruptedException, ExecutionException {
        try {
            System.out.println("Headers :" + message.getHeaders().toString());
            System.out.println("Payload :" + message.getPayload().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public Message processOutMessage(Message<?> message)
            throws InterruptedException, ExecutionException {
        System.out.println("Out:");
        return processMessage(message);
    }

    public Message processInMessage(Message<?> message)
            throws InterruptedException, ExecutionException {
        System.out.println("In:");
        return processMessage(message);
    }
}
