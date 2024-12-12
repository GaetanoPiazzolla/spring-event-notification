package gae.piaz.pattern.events.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "books")
@Getter
@Setter
@EntityListeners(BookEntityListener.class)
@ToString
public class Book extends AbstractEntity {

    private String description;

    private String title;
}
