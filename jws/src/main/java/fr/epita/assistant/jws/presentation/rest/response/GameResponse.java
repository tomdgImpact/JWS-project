package fr.epita.assistant.jws.presentation.rest.response;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value @With @AllArgsConstructor
public class GameResponse {
    public long id;
    public int players;
    public String state;
}
