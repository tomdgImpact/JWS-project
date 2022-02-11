package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.data.model.Game;
import fr.epita.assistant.jws.data.repository.GameRepo;
import fr.epita.assistant.jws.presentation.rest.response.GameState;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameData {
    @Inject GameRepo gameRepo;

    @Transactional
    public Game addGame(String map, GameState state, LocalDateTime start) {
        Game game  = new Game().withMap(map).withStatus(state.toString()).withStart(start.toString());
        Game.persist(game);
        return game;
    }

    @Transactional
    public Game addGame(String map, GameState state, LocalDateTime start, long id) {
        Game game  = new Game().withId(id).withMap(map).withStatus(state.toString()).withStart(start.toString());
        Game.persist(game);
        return game;
    }

    public Set<Game> getAllGames() {
        if (gameRepo.count() == 0)
            return null;
        return Game.<Game>findAll().stream().collect(Collectors.toSet());
    }

    @Transactional
    public Game removeGame(long givenId) {
        Game mygame = Game.<Game>findAll().stream().filter(game -> game.id == givenId).findFirst().orElse(null);
        Game.deleteById(givenId);
        return mygame;
    }

    @Transactional
    public Game findGame(long givenId) {
        Game mygame = Game.<Game>findAll().stream().filter(game -> game.id == givenId).findFirst().orElse(null);
        return mygame;
    }

    @Transactional
    public Game modifyGame(long gameId, GameState state) {
        Game game = findGame( gameId);
        game.status = state.toString();
        return game;
    }

    @Transactional
    public Game modifyGameMap(long gameId, String map) {
        Game game = findGame(gameId);
        game.map = map;
        return game;
    }
}
