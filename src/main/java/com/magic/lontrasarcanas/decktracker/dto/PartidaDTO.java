package com.magic.lontrasarcanas.decktracker.dto;
import lombok.Data;

@Data
public class PartidaDTO {
    private int qtdVitorias;
    private int qtdDerrotas;

    private String arquetipoAdversario; // Opcional
}
