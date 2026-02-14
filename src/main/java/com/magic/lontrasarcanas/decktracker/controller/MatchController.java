package com.magic.lontrasarcanas.decktracker.controller;

import com.magic.lontrasarcanas.decktracker.model.Match;
import com.magic.lontrasarcanas.decktracker.model.Deck;
import com.magic.lontrasarcanas.decktracker.repository.MatchRepository;
import com.magic.lontrasarcanas.decktracker.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/matches")
@CrossOrigin("*")
public class MatchController {
    
    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private DeckRepository deckRepository;

    @PostMapping("/{deckId}")
    public Match adicionarPartida(@PathVariable Long deckId, @RequestBody Match match) {
        // Busca o deck pelo ID e associa à partida
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck não encontrado"));
       
        // Associa o deck à partida e salva a partida
        match.setDeck(deck);

        // Salva a partida no banco de dados e retorna a entidade salva
        return matchRepository.save(match);
    }

    @GetMapping("/{deckId}")
    public List<Match> listarPartidasDoDeck(@PathVariable Long deckId) {
        return matchRepository.findByDeckId(deckId);
    }
    
}
