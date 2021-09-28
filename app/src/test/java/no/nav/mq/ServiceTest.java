package no.nav.mq;

import no.nav.mq.domain.Message;
import no.nav.mq.domain.Payload;
import no.nav.mq.domain.Type;
import no.nav.mq.gateway.JmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ServiceTest {

    private static final String MESSAGE = "Some message.";

    @Autowired
    private JmsService service;

    @Test
    void complexSendAndReceive() {

        Message outgoingMessage = new Message(1, Type.POSITIVE, "content");
        Payload outgoingPayload = new Payload(1, "description", outgoingMessage);

        service.send(outgoingPayload);
        Payload incomingPayload = service.receive();

        assertEquals(outgoingPayload.id(), incomingPayload.id());
        assertEquals(outgoingPayload.description(), incomingPayload.description());

        Message incomingMessage = incomingPayload.message();
        assertEquals(outgoingMessage.id(), incomingMessage.id());
        assertEquals(outgoingMessage.type(), incomingMessage.type());
        assertEquals(outgoingMessage.content(), incomingMessage.content());

    }

    @Test
    void transactionalSendAndReceive() {
        assertEquals(MESSAGE, service.transactionalSendAndReceive(MESSAGE));
    }

}
