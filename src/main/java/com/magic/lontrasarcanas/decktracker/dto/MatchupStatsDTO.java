package com.magic.lontrasarcanas.decktracker.dto;

import lombok.Data;

@Data
public class MatchupStatsDTO {
    private String nomeArquetipo;
    private int totalMatches;
    private int matchWins;
    private int matchLosses;
    private double matchWinRate;

    private int totalGames;
    private int gameWins;
    private int gameLosses;
    private double gameWinRate;
}
