package no.nav.mq;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TemporaryQueueTest {

    private static final String MESSAGE = "A simple message.";

    @Autowired
    private JmsTemplate template;

    @Test
    void temporaryQueueSendAndReceive()
            throws Exception {

        new Thread(new ReplyService(template)).start();

        assertNotNull(template.getMessageConverter());
        Message reply = template.sendAndReceive(session -> template.getMessageConverter().toMessage(MESSAGE, session));
        assertNotNull(reply);
        String replyText = (String) template.getMessageConverter().fromMessage(reply);

        assertEquals("Reply to '" + MESSAGE + "'", replyText);

    }

    /**
     * Just a simple "service" that receives messages and replies using the specified (temporary) queue.
     */
    @Slf4j
    private record ReplyService(JmsTemplate template) implements Runnable {

        @Override
        public void run() {

            try {
                log.info("Waiting");
                Message message = template.receive();
                if (message == null) {
                    return;
                }
                if (template.getMessageConverter() == null) {
                    throw new NullPointerException("Message converter is null");
                }
                String contents = (String) template.getMessageConverter().fromMessage(message);
                log.info("Received '{}'", contents);
                Destination replyTo = message.getJMSReplyTo();
                log.info("Replying to (temporary) queue {}", replyTo);
                template.convertAndSend(replyTo, "Reply to '" + contents + "'");
                log.info("Done");
            } catch (JMSException e) {
                log.error("Failed to complete run", e);
            }

        }
    }

}
