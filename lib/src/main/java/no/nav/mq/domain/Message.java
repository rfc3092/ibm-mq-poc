package no.nav.mq.domain;

import java.util.Optional;

public class Message {

    private final long id;
    private final Type type;
    private final String content;

    public Message(long id, Type type, String content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

}
