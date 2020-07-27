package io.reactor.echo;

import io.reactor.echo.config.RabbitMQConfiguration;
import io.reactor.echo.domain.EchoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

/**
 * Listens to the echo messages and forwards them as stream to the clients/subscribers.
 */
@Component
public class EchoHandler {

    private EmitterProcessor<EchoMessage> emitterProcessor;
    private Flux<EchoMessage> messages;
    private FluxSink<EchoMessage> sink;

    private final Logger log = LoggerFactory.getLogger(EchoHandler.class);

    public Mono<ServerResponse> getEchoStream(ServerRequest request) {
        log.info("New echo subscription");
        return ok().contentType(APPLICATION_STREAM_JSON)
                .body(fromPublisher(messages, EchoMessage.class));
    }

    @PostConstruct
    private void init() {
        this.emitterProcessor = EmitterProcessor.create();
        this.messages = emitterProcessor.share();
        this.sink = emitterProcessor.sink();
    }

    @RabbitListener(queues = RabbitMQConfiguration.OUTGOING_QUEUE)
    private void handleMessage(String message) {
        log.info("Coming back echo message: {}", message);
        sink.next(EchoMessage.of(message));
    }
}