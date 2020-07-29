package io.reactor.echo;

import io.reactor.echo.config.RabbitMQConfiguration;
import io.reactor.echo.domain.Echo;
import io.reactor.echo.domain.EchoId;
import io.reactor.echo.domain.EchoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.UUID;

/**
 * Puts the incoming message to an AMQP queue and returns a request id to the client.
 */
@Component
public class EchoSender {

    private final Logger log = LoggerFactory.getLogger(EchoSender.class);

    private final Sender sender;

    public EchoSender(Sender sender) {
        this.sender = sender;
    }

    public Mono<ServerResponse> echo(ServerRequest request) {
        return request
                .bodyToMono(Echo.class)
                .map(Echo::getMessage)
                .flatMap(this::send)
                .flatMap(ServerResponse.ok()::bodyValue)
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    private Mono<EchoId> send(String message) {
        log.info("Client sent echo message: {}", message);

        var echoId = createEchoId();
        log.info("Delivers echo with id: {}", echoId);

        send(message, echoId).subscribe();

        return Mono.just(new EchoId(echoId));
    }

    private Mono<Void> send(String message, String id) {
        var outboundMessage = Mono.fromSupplier(() -> createOutboundMessage(message, id));
        return sender
                .send(outboundMessage)
                .doOnError(e -> log.error("Echo dispatch error", e));
    }

    private String createEchoId() {
        return UUID.randomUUID().toString();
    }

    private OutboundMessage createOutboundMessage(String message, String id) {
        return new OutboundMessage("", RabbitMQConfiguration.REQUEST_QUEUE, new EchoMessage(id, message).getPayload());
    }
}