package com.magic.lontrasarcanas.decktracker.dto;

import lombok.Data;

@Data
public class CartaDTO {
    private String nome;
    private Integer quantidade;
    private boolean isTerrenoBasico;
}
