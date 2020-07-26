package io.reactor.echo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EchoId {

    private final String id;

    @JsonCreator
    public EchoId(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}