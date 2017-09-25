package no.nav.mq;

import generated.MessageType;
import generated.PayloadType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsOperations;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JmsOperationsTest {

    private static final String QUEUE = "DEV.QUEUE.1";
    private static final String MESSAGE = "Simple message.";

    @Autowired
    private JmsOperations jmsOperations;

    @Test
    public void simpleSendAndReceive() {

        jmsOperations.convertAndSend(QUEUE, MESSAGE);
        assertEquals(MESSAGE, jmsOperations.receiveAndConvert(QUEUE));

    }

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

        jmsOperations.convertAndSend(QUEUE, outgoingPayload);
        PayloadType incomingPayload = (PayloadType) jmsOperations.receiveAndConvert(QUEUE);

        assertEquals(outgoingPayload.getId(), incomingPayload.getId());
        assertEquals(outgoingPayload.getDescription(), incomingPayload.getDescription());

        MessageType incomingMessage = incomingPayload.getMessage();
        assertEquals(incomingMessage.getId(), outgoingMessage.getId());
        assertEquals(incomingMessage.getType(), outgoingMessage.getType());
        assertEquals(incomingMessage.getContent(), outgoingMessage.getContent());

    }

}
