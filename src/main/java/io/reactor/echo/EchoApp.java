package io.reactor.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EchoApp {

    public static void main(String[] args) {
        SpringApplication.run(EchoApp.class, args);
    }
}
