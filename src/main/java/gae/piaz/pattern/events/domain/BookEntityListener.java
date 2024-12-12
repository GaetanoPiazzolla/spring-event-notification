package gae.piaz.pattern.events.domain;

import gae.piaz.pattern.events.service.ApplicationEventQueue;
import gae.piaz.pattern.events.service.DataChangeEvent;
import gae.piaz.pattern.events.service.OperationType;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookEntityListener {
    private final ApplicationEventQueue applicationEventQueue;

    @PostUpdate
    public void postUpdate(AbstractEntity entity) {
        log.trace(
                "PostUpdate in BookEntityListener for Class {}, id: {}",
                entity.getClass().getSimpleName(),
                entity.getId());

        switch (entity) {
            case Book book -> publishBookEvent(book, OperationType.UPDATE);
            default ->
                    log.trace(
                            "Entity {} is not supported by BookEntityListener PostUpdate",
                            entity.getClass());
        }
    }

    @PostRemove
    public void postRemove(AbstractEntity entity) {
        log.trace(
                "PostRemove in BookEntityListener for Class {}, id: {}",
                entity.getClass().getSimpleName(),
                entity.getId());
        switch (entity) {
            case Book book -> publishBookEvent(book, OperationType.DELETE);
            default ->
                    log.trace(
                            "Entity {} is not supported by BookEntityListener PostRemove",
                            entity.getClass());
        }
    }

    @PostPersist
    public void postPersist(AbstractEntity entity) {
        log.trace(
                "PostPersist in BookEntityListener for Class {}, id: {}",
                entity.getClass().getSimpleName(),
                entity.getId());
        switch (entity) {
            case Book book -> publishBookEvent(book, OperationType.CREATE);
            default ->
                    log.trace(
                            "Entity {} is not supported by BookEntityListener PostPersist",
                            entity.getClass());
        }
    }

    private void publishBookEvent(Book book, OperationType operationType) {
        DataChangeEvent entityUpdated =
                DataChangeEvent.builder()
                        .eventName("book")
                        .id(book.getId())
                        .operationType(operationType)
                        .databaseVersion(book.getDatabaseVersion())
                        .build();
        applicationEventQueue.enqueue(entityUpdated);
    }
}
