package com.magic.lontrasarcanas.decktracker.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int qtdVitorias;
    private int qtdDerrotas;

    private LocalDateTime dataPartida = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "deck_oponente_id")
    @JsonIgnore
    private DeckOponente deckOponente;
}
