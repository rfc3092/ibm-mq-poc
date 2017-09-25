package no.nav.mq.config;

import generated.MessageType;
import generated.PayloadType;
import no.nav.mq.domain.Message;
import no.nav.mq.domain.Payload;
import no.nav.mq.domain.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

    @Autowired
    private JmsOperations jmsOperations;

    public void send(String queue, Payload payload) {

        Message message = payload.getMessage();

        MessageType convertedMessage = new MessageType();
        convertedMessage.setId(message.getId());
        convertedMessage.setType(message.getType().toString());
        convertedMessage.setContent(message.getContent());

        PayloadType convertedPayload = new PayloadType();
        convertedPayload.setId(payload.getId());
        convertedPayload.setDescription(payload.getDescription().orElse(null));
        convertedPayload.setMessage(convertedMessage);

        jmsOperations.convertAndSend(queue, convertedPayload);

    }

    public Payload receive(String queue) {

        PayloadType payload = (PayloadType) jmsOperations.receiveAndConvert(queue);
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

}
