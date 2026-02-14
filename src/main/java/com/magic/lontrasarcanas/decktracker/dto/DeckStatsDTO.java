package com.magic.lontrasarcanas.decktracker.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DeckStatsDTO {
    private String deckName;
    private String listaCartas;

    private int totalMatches;
    private int matchWins;
    private int matchLosses;
    private double matchWinRate;

    private int totalGames;
    private int gameWins;
    private int gameLosses;
    private double gameWinRate;

    private List<MatchupStatsDTO> matchups = new ArrayList<>();
}
