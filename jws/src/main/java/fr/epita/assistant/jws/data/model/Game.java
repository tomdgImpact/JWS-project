package fr.epita.assistant.jws.data.model;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;

@Entity @Table(name = "game")
@AllArgsConstructor
@NoArgsConstructor
@With
@ToString
public class Game extends PanacheEntityBase {
    public @Column(name = "start_time") String start;
    public @Column(name = "state") String status;
    public String map;
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
}