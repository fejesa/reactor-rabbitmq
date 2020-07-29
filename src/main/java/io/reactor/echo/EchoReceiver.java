package io.reactor.echo;

import io.reactor.echo.config.RabbitMQConfiguration;
import io.reactor.echo.domain.EchoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

/**
 * Listens to the AMQP queue and sends it back - echo - using another queue.
 */
@Component
public class EchoReceiver {

    private final Logger log = LoggerFactory.getLogger(EchoReceiver.class);

    private final Sender sender;

    public EchoReceiver(Sender sender) {
        this.sender = sender;
    }

    @RabbitListener(queues = RabbitMQConfiguration.REQUEST_QUEUE)
    public void handleMessage(String message) {
        log.info("Received echo message: {}", message);

        var echoMessage = EchoMessage.of(message);
        log.info("Sent back echo: {}", echoMessage);
        sendEcho(echoMessage).subscribe();
    }

    private Mono<Void> sendEcho(EchoMessage message) {
        var outboundMessage = Mono.fromSupplier(() -> createOutboundMessage(message));
        return sender
                .send(outboundMessage)
                .doOnError(e -> log.error("Delivery error", e));
    }

    private OutboundMessage createOutboundMessage(EchoMessage message) {
        return new OutboundMessage("", RabbitMQConfiguration.RESPONSE_QUEUE, message.getPayload());
    }
}