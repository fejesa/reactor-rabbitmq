# Spring Webflux and RabbitMQ sample

The example application demonstrates the usage [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
and [RabbitMQ](https://www.rabbitmq.com/) together.

The client can send POST request - for example using [curl](https://curl.haxx.se/) - like
```
curl -d '{"message":"Hello"}' -H "Content-Type: application/json" -X POST http://localhost:8080/echo
```
and the ``EchoSender`` puts the message in an AMQP queue.

The ``EchoReceiver`` gets the message and echos it using another AMQP queue.

The ``EchoHandler`` listens to the echoed messages and push them to all the clients that
subscribed by calling
```
curl -N http://localhost:8080/echo-stream
```

## Requirements
* Java 11+
* RabbitMQ 3.8.5 or above (you can use the Docker container for convenience)
* Maven

## How to build
Execute the following command
```
mvn clean package
```

## How to run
Execute the following command
```
mvn spring-boot:run
```