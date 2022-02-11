package fr.epita.assistant.jws.presentation.rest.request;


import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value @With @AllArgsConstructor
public class MovePlayerRequest {
    public int posX;
    public int posY;
}
