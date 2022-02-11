package fr.epita.assistant.jws.data.repository;

import fr.epita.assistant.jws.data.model.Player;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public class PlayerRepo  implements PanacheRepositoryBase<Player, Long> {
}
