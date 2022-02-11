package fr.epita.assistant.jws.presentation.rest.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.With;

@With
public class CreateGameRequest {
    public String name;

    public CreateGameRequest(String name) {
        this.name = name;
    }

    public CreateGameRequest(){

    }
}
