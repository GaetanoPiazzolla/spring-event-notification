package gae.piaz.pattern.events.publisher;

import gae.piaz.pattern.events.service.ApplicationEventQueue;
import gae.piaz.pattern.events.service.DataChangeEvent;
import java.lang.reflect.Method;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "events.notification-enabled", havingValue = "true")
public class TransactionSynchronizationAspect {

    private final ApplicationEventQueue applicationEventQueue;
    private final ApplicationEventPublisher springEventPublisher;

    @Before(
            "execution(* (@org.springframework.transaction.annotation.Transactional *).*(..)) || "
                    + "@annotation(org.springframework.transaction.annotation.Transactional)")
    public void beforeWriteEndpoint(JoinPoint joinPoint) throws Throwable {
        if (isReadOnlyTransaction(joinPoint)) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        boolean alreadyRegistered =
                TransactionSynchronizationManager.getSynchronizations().stream()
                        .anyMatch(DataChangeEventSynchronization.class::isInstance);
        if (alreadyRegistered) {
            return;
        }
        log.trace("Registering synchronization for data change event");
        TransactionSynchronizationManager.registerSynchronization(
                new DataChangeEventSynchronization());
    }

    private boolean isReadOnlyTransaction(JoinPoint joinPoint) throws NoSuchMethodException {
        Method method = getTargetMethod(joinPoint);
        Transactional transactional = method.getAnnotation(Transactional.class);
        if (transactional == null) {
            transactional = joinPoint.getTarget().getClass().getAnnotation(Transactional.class);
        }
        return transactional != null && transactional.readOnly();
    }

    private Method getTargetMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        Method signatureMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return joinPoint
                .getTarget()
                .getClass()
                .getMethod(signatureMethod.getName(), signatureMethod.getParameterTypes());
    }

    private class DataChangeEventSynchronization implements TransactionSynchronization {

        private void publishEvents() {
            Set<DataChangeEvent> eventsToPublish = applicationEventQueue.consumeEvents();
            if (CollectionUtils.isEmpty(eventsToPublish)) {
                log.trace("No events to publish");
                return;
            }
            for (DataChangeEvent event : eventsToPublish) {
                springEventPublisher.publishEvent(event);
                log.trace("Published event: {}", event);
            }
        }

        @Override
        public void afterCommit() {
            publishEvents();
        }
    }
}
