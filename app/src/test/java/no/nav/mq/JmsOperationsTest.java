package no.nav.mq;

import generated.MessageType;
import generated.PayloadType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JmsOperationsTest {

    private static final Logger LOG = LoggerFactory.getLogger(JmsOperationsTest.class);
    private static final String MESSAGE = "Simple message.";

    @Autowired
    private JmsOperations jmsOperations;

    /**
     * Send and receive a simple {@code String}.
     */
    @Test
    public void simpleSendAndReceive() {

        jmsOperations.convertAndSend(MESSAGE);
        assertEquals(MESSAGE, jmsOperations.receiveAndConvert());

    }

    @Test
    public void multipleSendAndReceive() {

        for (int i = 1; i < 6; i++) {
            jmsOperations.convertAndSend("Message #" + i);
            LOG.info("Sent message " + i);
        }
        for (int i = 1; i < 6; i++) {
            assertEquals("Message #" + i, jmsOperations.receiveAndConvert());
            LOG.info("Received message " + i);
        }

    }

    /**
     * Send and receive objects generated from XSD.
     */
    @Test
    public void complexSendAndReceive() {

        MessageType outgoingMessage = new MessageType();
        outgoingMessage.setId(1);
        outgoingMessage.setType("positive");
        outgoingMessage.setContent("content");

        PayloadType outgoingPayload = new PayloadType();
        outgoingPayload.setId(1);
        outgoingPayload.setDescription("description");
        outgoingPayload.setMessage(outgoingMessage);

        jmsOperations.convertAndSend(outgoingPayload);
        PayloadType incomingPayload = (PayloadType) jmsOperations.receiveAndConvert();

        assertEquals(outgoingPayload.getId(), incomingPayload.getId());
        assertEquals(outgoingPayload.getDescription(), incomingPayload.getDescription());

        MessageType incomingMessage = incomingPayload.getMessage();
        assertEquals(incomingMessage.getId(), outgoingMessage.getId());
        assertEquals(incomingMessage.getType(), outgoingMessage.getType());
        assertEquals(incomingMessage.getContent(), outgoingMessage.getContent());

    }

    @Test
    @Transactional(value = "jmsTransactionManager")
    public void transactionalSendAndReceive() {

        jmsOperations.convertAndSend(MESSAGE);
        assertEquals(MESSAGE, jmsOperations.receiveAndConvert());

    }

}
