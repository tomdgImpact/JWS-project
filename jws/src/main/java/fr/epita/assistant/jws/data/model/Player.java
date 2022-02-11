package fr.epita.assistant.jws.data.model;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;

@Entity @Table(name = "player")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class Player extends PanacheEntityBase {
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
    public String Bomb;
    public String movement;
    public int lives;
    public String name;
    public int posX;
    public int posY;
    public long gameid;
    public boolean placeBomb;
    public int initPosX;
    public int initPosY;
}

