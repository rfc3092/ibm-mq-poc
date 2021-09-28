package no.nav.mq;

import generated.MessageType;
import generated.PayloadType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
class TemplateTest {

    private static final String MESSAGE = "Simple message.";

    @Autowired
    private JmsTemplate template;

    /**
     * Send and receive a simple {@code String}.
     */
    @Test
    void simpleSendAndReceive() {

        template.convertAndSend(MESSAGE);
        assertEquals(MESSAGE, template.receiveAndConvert());

    }

    @Test
    void multipleSendAndReceive() {

        for (int i = 1; i < 6; i++) {
            template.convertAndSend("Message #" + i);
            log.info("Sent message " + i);
        }
        for (int i = 1; i < 6; i++) {
            assertEquals("Message #" + i, template.receiveAndConvert());
            log.info("Received message " + i);
        }

    }

    /**
     * Send and receive objects generated from XSD.
     */
    @Test
    void complexSendAndReceive() {

        MessageType outgoingMessage = new MessageType();
        outgoingMessage.setId(1);
        outgoingMessage.setType("positive");
        outgoingMessage.setContent("content");

        PayloadType outgoingPayload = new PayloadType();
        outgoingPayload.setId(1);
        outgoingPayload.setDescription("description");
        outgoingPayload.setMessage(outgoingMessage);

        template.convertAndSend(outgoingPayload);
        PayloadType incomingPayload = (PayloadType) template.receiveAndConvert();

        assertNotNull(incomingPayload);
        assertEquals(outgoingPayload.getId(), incomingPayload.getId());
        assertEquals(outgoingPayload.getDescription(), incomingPayload.getDescription());

        MessageType incomingMessage = incomingPayload.getMessage();
        assertEquals(incomingMessage.getId(), outgoingMessage.getId());
        assertEquals(incomingMessage.getType(), outgoingMessage.getType());
        assertEquals(incomingMessage.getContent(), outgoingMessage.getContent());

    }

    @Test
    @Transactional(value = "jmsTransactionManager")
    void transactionalSendAndReceive() {

        template.convertAndSend(MESSAGE);
        assertEquals(MESSAGE, template.receiveAndConvert());

    }

}
