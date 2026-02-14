package com.magic.lontrasarcanas.decktracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "matches")
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String opponentDeck;

    private String result; // "vitória", "derrota" ou "empate"

    private LocalDate date = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false) // Cria uma coluna deck_id na tabela matches para a relação ManyToOne
    @JsonIgnore
    private Deck deck;

}
