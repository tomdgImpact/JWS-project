package fr.epita.assistant.jws.presentation.rest.response;

public enum GameState {

    RUNNING("RUNNING"),
    FINISHED("FINISHED"),
    STARTING("STARTING");

    private final String status;

    GameState(final String text){
        this.status = text;
    }

    @Override
    public String toString(){
        return this.status;
    }
}
