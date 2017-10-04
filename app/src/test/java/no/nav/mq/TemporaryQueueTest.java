package no.nav.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TemporaryQueueTest {

    private static final String MESSAGE = "A simple message.";

    @Autowired
    JmsTemplate template;

    @Test
    public void temporaryQueueSendAndReceive()
            throws Exception {

        new Thread(new ReplyService(template)).start();

        Message reply = template.sendAndReceive(session -> template.getMessageConverter().toMessage(MESSAGE, session));
        String replyText = (String) template.getMessageConverter().fromMessage(reply);

        assertEquals("Reply to '" + MESSAGE + "'", replyText);

    }

    /**
     * Just a simple "service" that receives messages and replies using the specified (temporary) queue.
     */
    private static class ReplyService implements Runnable {

        private static final Logger LOG = LoggerFactory.getLogger(ReplyService.class);

        private final JmsTemplate template;

        private ReplyService(JmsTemplate template) {

            this.template = template;

        }

        @Override
        public void run() {

            try {
                LOG.info("Waiting");
                Message message = template.receive();
                String contents = (String) template.getMessageConverter().fromMessage(message);
                LOG.info("Received '{}'", contents);
                Destination replyTo = message.getJMSReplyTo();
                LOG.info("Replying to (temporary) queue {}", replyTo);
                template.convertAndSend(replyTo, "Reply to '" + contents + "'");
                LOG.info("Done");
            } catch (JMSException e) {
                LOG.error("Failed to complete run", e);
            }

        }
    }

}
