package gae.piaz.pattern.events.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DataChangeEvent is a class that represents an event that is triggered when any entity is updated.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DataChangeEvent {

    private String eventName;

    private Integer id;

    private Integer databaseVersion;

    private OperationType operationType;

    private Object body;
}
