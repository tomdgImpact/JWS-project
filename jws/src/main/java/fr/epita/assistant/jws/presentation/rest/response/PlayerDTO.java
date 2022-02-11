package fr.epita.assistant.jws.presentation.rest.response;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value @With @AllArgsConstructor
public class PlayerDTO {
    public long id;
    public String name;
    public int lives;
    public int posX;
    public int posY;
}