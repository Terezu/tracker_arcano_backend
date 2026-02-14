package com.magic.lontrasarcanas.decktracker.repository;

import com.magic.lontrasarcanas.decktracker.model.DeckOponente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DeckOponenteRepository extends JpaRepository<DeckOponente, Long> {
    // Busca um oponente específico enfrentado por um deck específico
    Optional<DeckOponente> findByDeckIdAndNomeArquetipo(Long deckId, String nomeArquetipo);
}
