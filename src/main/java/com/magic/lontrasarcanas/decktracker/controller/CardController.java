package com.magic.lontrasarcanas.decktracker.controller;

import com.magic.lontrasarcanas.decktracker.service.ScryfallService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartas")
@CrossOrigin(origins = "*") // Essencial para evitar erros de CORS quando o Angular tentar acessar
public class CardController {

    private final ScryfallService scryfallService;

    // Injeção de dependência do nosso service que tem o cache
    public CardController(ScryfallService scryfallService) {
        this.scryfallService = scryfallService;
    }

    @GetMapping("/imagem/{nomeDaCarta}")
    public ResponseEntity<byte[]> buscarImagemDaCarta(@PathVariable String nomeDaCarta) {
        
        byte[] imagem = scryfallService.buscarImagemDaCarta(nomeDaCarta);

        // Se der algum problema na busca (carta não existe, erro de digitação), retorna 404
        if (imagem == null || imagem.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Avisa quem fez a requisição que o conteúdo é uma imagem JPEG
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imagem, headers, HttpStatus.OK);
    }
}
