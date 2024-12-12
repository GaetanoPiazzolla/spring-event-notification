package gae.piaz.pattern.events.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EventNotificationConfigValidator {

    @Value("${event.notification-response-enabled:false}")
    private boolean eventNotificationResponseEnabled;

    @Value("${event.notification-enabled:true}")
    private boolean eventNotificationEnabled;

    @PostConstruct
    public void validateConfig() {
        if (eventNotificationResponseEnabled && eventNotificationEnabled) {
            throw new IllegalStateException(
                    "Only one of 'event-notification-response-enabled' or 'event-notification-enabled' can be true.");
        }
        // if both false, log an error
        if (!eventNotificationResponseEnabled && !eventNotificationEnabled) {
            log.error(
                    "Both 'event-notification-response-enabled' and 'event-notification-enabled' are false. "
                            + "This will disable event notifications. "
                            + "Please enable at least one of them.");
        }
    }
}
