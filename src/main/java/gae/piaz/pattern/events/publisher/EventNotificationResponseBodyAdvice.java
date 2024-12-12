package gae.piaz.pattern.events.publisher;

import gae.piaz.pattern.events.service.ApplicationEventQueue;
import gae.piaz.pattern.events.service.DataChangeEvent;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "events.notification-response-enabled", havingValue = "true")
public class EventNotificationResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ApplicationEventQueue applicationEventQueue;
    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public boolean supports(
            MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    // we need a new transaction to be able to write in "EVENT_PUBLICATION" table.
    // in here we are outside the transaction that is handling the request.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (isWriteMethod(request.getMethod())) {
            try {
                this.publishEvents(body);
            } catch (Exception e) {
                log.error("Error while sending spring events", e);
            }
        }

        return body;
    }

    private boolean isWriteMethod(HttpMethod method) {
        return method.equals(HttpMethod.POST)
                || method.equals(HttpMethod.PUT)
                || method.equals(HttpMethod.PATCH)
                || method.equals(HttpMethod.DELETE);
    }

    private void publishEvents(Object body) {

        Set<DataChangeEvent> eventsToPublish = applicationEventQueue.consumeEvents();
        if (eventsToPublish.isEmpty()) {
            return;
        }
        for (DataChangeEvent event : eventsToPublish) {
            event.setBody(body);
            springEventPublisher.publishEvent(event);
            log.trace("Published event: {}", event);
        }
    }
}
