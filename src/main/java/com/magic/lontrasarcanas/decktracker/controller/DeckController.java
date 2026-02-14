package com.magic.lontrasarcanas.decktracker.controller;

import com.magic.lontrasarcanas.decktracker.dto.DeckStatsDTO;
import com.magic.lontrasarcanas.decktracker.model.Deck;
import com.magic.lontrasarcanas.decktracker.service.DeckService;
import com.magic.lontrasarcanas.decktracker.dto.PartidaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/decks")
@CrossOrigin(origins = "*") // Permite que o Angular (localhost:4200) acesse o Java
public class DeckController {

    private final DeckService deckService;

    // Injeção via Construtor
    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping
    public ResponseEntity<?> criarDeck(@RequestBody Deck deck) {
        try {
            Deck novoDeck = deckService.salvarDeck(deck);
            return ResponseEntity.ok(novoDeck);
        } catch (IllegalArgumentException e) {
            // Captura os erros de validação (ex: "Deck deve ter 60 cartas")
            // Retorna erro 400 (Bad Request) com a mensagem clara para o front-end
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Captura erros inesperados (banco fora do ar, Scryfall falhou, etc)
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro interno ao salvar o deck: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/partidas")
    public ResponseEntity<?> registrarPartida(@PathVariable Long id, @RequestBody PartidaDTO partidaDTO) {
        try {
            deckService.registrarPartida(id, partidaDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<DeckStatsDTO> getEstatisticas(@PathVariable Long id) {
        try {
            DeckStatsDTO stats = deckService.calcularEstatisticas(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Deck>> listarTodos() {
        return ResponseEntity.ok(deckService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        deckService.excluir(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content para indicar que a exclusão foi bem-sucedida, sem corpo de resposta.
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDeck(@PathVariable Long id, @RequestBody Deck deckAtualizado) {
        try {
            Deck deckSalvo = deckService.atualizarDeck(id, deckAtualizado);
            return ResponseEntity.ok(deckSalvo);
        } catch (RuntimeException e) {
            // Captura tanto os erros do seu DeckValidator (IllegalArgument) 
            // quanto os do ScryfallService (RuntimeException) e devolve 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro interno ao atualizar o deck: " + e.getMessage());
        }
    }
}
