package no.nav.mq;

import no.nav.mq.config.JmsService;
import no.nav.mq.domain.Message;
import no.nav.mq.domain.Payload;
import no.nav.mq.domain.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsOperations;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JmsServiceTest {

    private static final String QUEUE = "DEV.QUEUE.2";

    @Autowired
    private JmsService service;

    @Test
    public void complexSendAndReceive() {

        Message outgoingMessage = new Message(1, Type.POSITIVE, "content");
        Payload outgoingPayload = new Payload(1, "description", outgoingMessage);

        service.send(QUEUE, outgoingPayload);
        Payload incomingPayload = service.receive(QUEUE);

        assertEquals(outgoingPayload.getId(), incomingPayload.getId());
        assertEquals(outgoingPayload.getDescription(), incomingPayload.getDescription());

        Message incomingMessage = incomingPayload.getMessage();
        assertEquals(outgoingMessage.getId(), incomingMessage.getId());
        assertEquals(outgoingMessage.getType(), incomingMessage.getType());
        assertEquals(outgoingMessage.getContent(), incomingMessage.getContent());

    }

}
