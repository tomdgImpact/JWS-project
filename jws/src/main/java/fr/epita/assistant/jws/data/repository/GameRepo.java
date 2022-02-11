package fr.epita.assistant.jws.data.repository;

import fr.epita.assistant.jws.data.model.Game;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameRepo implements PanacheRepositoryBase<Game, Long>  {
}
