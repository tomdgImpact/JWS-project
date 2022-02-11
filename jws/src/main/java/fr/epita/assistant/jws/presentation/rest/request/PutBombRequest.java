package fr.epita.assistant.jws.presentation.rest.request;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
public class PutBombRequest {
    public int posX;
    public int posY;

    public PutBombRequest(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public PutBombRequest(){

    }
}
