package no.nav.mq.gateway;

import generated.MessageType;
import generated.PayloadType;
import no.nav.mq.domain.Message;
import no.nav.mq.domain.Payload;
import no.nav.mq.domain.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JmsService {

    private final JmsOperations jmsOperations;

    public JmsService(@Autowired JmsOperations jmsOperations) {
        this.jmsOperations = jmsOperations;
    }

    public void send(Payload payload) {

        Message message = payload.getMessage();

        MessageType convertedMessage = new MessageType();
        convertedMessage.setId(message.getId());
        convertedMessage.setType(message.getType().toString());
        convertedMessage.setContent(message.getContent());

        PayloadType convertedPayload = new PayloadType();
        convertedPayload.setId(payload.getId());
        convertedPayload.setDescription(payload.getDescription().orElse(null));
        convertedPayload.setMessage(convertedMessage);

        jmsOperations.convertAndSend(convertedPayload);

    }

    public Payload receive() {

        PayloadType payload = (PayloadType) jmsOperations.receiveAndConvert();
        MessageType message = payload.getMessage();
        return new Payload(
                payload.getId(),
                payload.getDescription(),
                new Message(
                        message.getId(),
                        Type.fromString(message.getType()),
                        message.getContent()
                )
        );

    }

    @Transactional(value = "jmsTransactionManager")
    public String transactionalSendAndReceive(String message) {
        jmsOperations.convertAndSend(message);
        return (String) jmsOperations.receiveAndConvert();
    }

}
