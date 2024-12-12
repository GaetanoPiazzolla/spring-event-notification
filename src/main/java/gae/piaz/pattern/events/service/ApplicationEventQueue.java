package gae.piaz.pattern.events.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
public class ApplicationEventQueue {

    private static final ThreadLocal<Set<DataChangeEvent>> events =
            ThreadLocal.withInitial(HashSet::new);

    public void enqueue(DataChangeEvent event) {
        events.get().add(event);
    }

    public void clear() {
        events.remove();
    }

    public Set<DataChangeEvent> consumeEvents() {
        Set<DataChangeEvent> allEvents = filterByLatestDatabaseVersion(events.get());
        this.clear();
        return allEvents;
    }

    private Set<DataChangeEvent> filterByLatestDatabaseVersion(
            Set<DataChangeEvent> dataChangeEventSet) {
        return new HashSet<>(
                dataChangeEventSet.stream()
                        .collect(
                                Collectors.toMap(
                                        entity ->
                                                new GroupKey(
                                                        entity.getEventName(),
                                                        entity.getId(),
                                                        entity.getOperationType()),
                                        Function.identity(),
                                        (existing, replacement) ->
                                                replacement.getDatabaseVersion()
                                                                > existing.getDatabaseVersion()
                                                        ? replacement
                                                        : existing))
                        .values());
    }

    private record GroupKey(String eventName, Integer id, OperationType operationType) {}
}
