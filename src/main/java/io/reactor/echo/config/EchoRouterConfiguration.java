package io.reactor.echo.config;

import io.reactor.echo.EchoHandler;
import io.reactor.echo.EchoSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EchoRouterConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(EchoSender echoSender, EchoHandler echoHandler) {
        return route()
                .POST("/echo", accept(APPLICATION_JSON), echoSender::echo)
                .GET("/echo-stream", echoHandler::getEchoStream)
                .build();
    }

}
