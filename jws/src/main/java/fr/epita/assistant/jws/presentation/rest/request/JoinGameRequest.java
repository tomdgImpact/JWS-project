package fr.epita.assistant.jws.presentation.rest.request;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value @With @AllArgsConstructor
public class JoinGameRequest {
    public String name;
}

