package no.nav.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IbmMqPocApplicationTests {

    private static final String QUEUE = "DEV.QUEUE.1";

    @Autowired
    private JmsOperations jmsOperations;

	@Test
	public void contextLoads() {
	}

	@Test
    public void send() {
	    jmsOperations.convertAndSend(QUEUE, "hello world");
    }

    @Test
    public void receive() {
	    System.out.println(jmsOperations.receiveAndConvert(QUEUE));
    }

}
