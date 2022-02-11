package fr.epita.assistant.jws.domain.entity;

import fr.epita.assistant.jws.presentation.rest.response.GameState;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.HashSet;


@Value @With @AllArgsConstructor
public class EntGameEnt {

    public GameState state;
    public long id;
    public HashSet<EntPlayer> players;
    public String startTime;
    public ArrayList<String> map;

}
