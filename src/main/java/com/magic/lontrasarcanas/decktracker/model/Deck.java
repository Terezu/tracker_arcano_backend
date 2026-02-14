package com.magic.lontrasarcanas.decktracker.model;

import com.magic.lontrasarcanas.decktracker.model.Usuario;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data // O Lombok cria automaticamente os getters, setters, equals, hashCode e toString
@Entity // Diz ao Srping: "Isso aqui é uma tabela no banco de dados"
@Table(name = "decks")
public class Deck {
    @Id // Indica que esse campo é a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O banco de dados vai gerar o ID automaticamente
    private Long id;

    private String name; // Nome do deck
    private String format; // Formato do deck (Standard, Modern, etc.)

    @Column(columnDefinition = "TEXT")
    private String deckList; // Lista de cartas do deck, armazenada como texto (pode ser JSON ou outro formato)

    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeckOponente> oponentes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Aqui nós dizemos que esse Deck pertence a um Usuário
}
