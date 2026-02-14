package com.magic.lontrasarcanas.decktracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeckDTO {
    private String nome;
    private List<CartaDTO> mainDeck;
    private List<CartaDTO> sideDeck;
}
