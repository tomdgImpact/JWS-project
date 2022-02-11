package fr.epita.assistant.jws.presentation.rest.response;

import lombok.Value;
import lombok.With;

import java.util.ArrayList;
import java.util.HashSet;

@Value @With
public class GameDetailResponse {
    public String startTime;
    public String state;
    public HashSet<PlayerDTO> players;
    public ArrayList<String> map;
    public long id;
    public GameDetailResponse(String startTime, String state, HashSet<PlayerDTO> players, ArrayList<String> map,
                              long id){
        this.startTime = startTime;
        this.state = state;
        this.players = players;
        this.map = map;
        this.id = id;
    }
}
