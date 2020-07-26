package io.reactor.echo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactor.echo.config.RabbitMQConfiguration;
import io.reactor.echo.domain.EchoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Component
public class EchoReceiver {

    private final Logger log = LoggerFactory.getLogger(EchoReceiver.class);

    private final Sender sender;

    public EchoReceiver(Sender sender) {
        this.sender = sender;
    }

    @RabbitListener(queues = RabbitMQConfiguration.INCOMING_QUEUE)
    public void handleMessage(String message) {
        log.info("Received echo message: {}", message);
        try {
            EchoMessage echoMessage = new ObjectMapper().readValue(message, EchoMessage.class);
            sendEcho(echoMessage).subscribe();
            log.info("Sent back echo id: {}", echoMessage.getId());
        } catch (JsonProcessingException e) {
            log.error("Message parse error", e);
        }
    }

    private Mono<Void> sendEcho(EchoMessage message) {
        Mono<OutboundMessage> outboundMessage = Mono.fromSupplier(() -> createOutboundMessage(message));
        return sender
                .send(outboundMessage)
                .doOnError(e -> log.error("Delivery error", e));
    }

    private OutboundMessage createOutboundMessage(EchoMessage message) {
        return new OutboundMessage("", RabbitMQConfiguration.OUTGOING_QUEUE, message.getPayload());
    }
}
