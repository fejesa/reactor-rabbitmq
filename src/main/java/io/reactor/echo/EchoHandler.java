package io.reactor.echo;

import io.reactor.echo.config.RabbitMQConfiguration;
import io.reactor.echo.domain.EchoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

@Component
public class EchoHandler {

    private final UnicastProcessor<String> sseFluxProcessor = UnicastProcessor.create();

    private final Flux<String> sseFlux = this.sseFluxProcessor.share();

    private final Logger log = LoggerFactory.getLogger(EchoHandler.class);

    public Flux<ServerResponse> getSeeFromAmqp(ServerRequest request) {
        return null;
        //return this.sseFlux;
    }

    @RabbitListener(queues = RabbitMQConfiguration.OUTGOING_QUEUE)
    public void handleAmqpMessages(String message) {
        log.info("Coming back echo message: {}", message);
        this.sseFluxProcessor.onNext(message);
    }
}
