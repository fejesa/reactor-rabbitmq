package io.reactor.echo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EchoMessage {

    private final String id;

    private final String message;

    @JsonCreator
    public EchoMessage(@JsonProperty("id") String id, @JsonProperty("message") String message) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public byte[] getPayload() {
        try {
            return new ObjectMapper().writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static EchoMessage of(String s) {
        try {
            return new ObjectMapper().readValue(s, EchoMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
