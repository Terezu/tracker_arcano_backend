package com.magic.lontrasarcanas.decktracker.repository;

import com.magic.lontrasarcanas.decktracker.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByDeckId(Long deckId); // Método para encontrar partidas por ID do deck
}
