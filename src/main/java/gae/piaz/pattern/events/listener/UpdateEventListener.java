package gae.piaz.pattern.events.listener;

import gae.piaz.pattern.events.service.DataChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateEventListener {

    @Async
    @EventListener
    public void on(DataChangeEvent event) {
        log.info("Received DataChangeEvent: {}", event);
    }
}
