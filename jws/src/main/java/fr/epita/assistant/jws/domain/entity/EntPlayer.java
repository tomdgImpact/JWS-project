package fr.epita.assistant.jws.domain.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With @AllArgsConstructor @NoArgsConstructor
public class EntPlayer {
    public long id;
    public String lastBomb;
    public String lastMovement;
    public int lives;
    public String name;
    public int posX;
    public int posY;
    public long gameId;
    public boolean canPlaceBombs;
    public int initX;
    public int initY;
}
