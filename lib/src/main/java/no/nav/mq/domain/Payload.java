package no.nav.mq.domain;

import java.util.Optional;

public class Payload {

    private long id;
    private String description;
    private Message message;

    public Payload(long id, String description, Message message) {
        this.id = id;
        this.description = description;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Message getMessage() {
        return message;
    }

}
