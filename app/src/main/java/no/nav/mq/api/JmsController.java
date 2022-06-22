package no.nav.mq.api;

import lombok.RequiredArgsConstructor;
import no.nav.mq.domain.Payload;
import no.nav.mq.gateway.JmsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JmsController {

    private final JmsService service;

    @PostMapping
    public void send(Payload payload) {
        service.send(payload);
    }

    @GetMapping
    public Payload receive() {
        return service.receive();
    }

}
