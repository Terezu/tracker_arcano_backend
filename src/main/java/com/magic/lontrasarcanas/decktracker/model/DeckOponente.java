package com.magic.lontrasarcanas.decktracker.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "deck_oponente")
public class DeckOponente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Substitui o antigo campo que ficava solto na Partida
    private String nomeArquetipo; 

    // Relação com o seu deck (O "dono" desse matchup)
    @ManyToOne
    @JoinColumn(name = "deck_id")
    @JsonIgnore // Evita loop infinito na hora de transformar em JSON para o Angular
    private Deck deck;

    // Um Oponente pode ter várias partidas registradas contra ele
    @OneToMany(mappedBy = "deckOponente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Partida> partidas = new ArrayList<>();
}
