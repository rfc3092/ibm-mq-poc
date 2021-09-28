package no.nav.mq.gateway;

import generated.MessageType;
import generated.PayloadType;
import no.nav.mq.domain.Message;
import no.nav.mq.domain.Payload;
import no.nav.mq.domain.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JmsService {

    private final JmsTemplate template;

    @Autowired
    public JmsService(JmsTemplate template) {

        this.template = template;

    }

    public void send(Payload payload) {

        template.convertAndSend(convert(payload));

    }

    public Payload receive() {

        return convert((PayloadType) template.receiveAndConvert());

    }

    private static PayloadType convert(Payload payload) {

        Message message = payload.message();

        MessageType convertedMessage = new MessageType();
        convertedMessage.setId(message.id());
        convertedMessage.setType(message.type().toString());
        convertedMessage.setContent(message.content());

        PayloadType convertedPayload = new PayloadType();
        convertedPayload.setId(payload.id());
        convertedPayload.setDescription(payload.description());
        convertedPayload.setMessage(convertedMessage);

        return convertedPayload;

    }

    private static Payload convert(PayloadType payload) {

        if (payload == null) {
            return null;
        }
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

        template.convertAndSend(message);
        return (String) template.receiveAndConvert();

    }

}
