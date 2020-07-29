package io.reactor.echo.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class RabbitMQConfiguration {

    public static final String REQUEST_QUEUE = "echo-request";

    public static final String RESPONSE_QUEUE = "echo-response";

    @Autowired
    private Mono<Connection> connectionMono;

    private final AmqpAdmin amqpAdmin;

    public RabbitMQConfiguration(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @Bean
    public Sender sender(Mono<Connection> connectionMono) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
    }

    @Bean
    public Receiver receiver(Mono<Connection> connectionMono) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
    }

    @Bean
    public Mono<Connection> connectionMono(RabbitProperties rabbitProperties) {
        var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        return Mono.fromCallable(() -> connectionFactory.newConnection("reactor-rabbit")).cache();
    }

    @PostConstruct
    public void init() {
        amqpAdmin.declareQueue(new Queue(REQUEST_QUEUE, false, false, true));
        amqpAdmin.declareQueue(new Queue(RESPONSE_QUEUE, false, false, true));
    }

    @PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }
}