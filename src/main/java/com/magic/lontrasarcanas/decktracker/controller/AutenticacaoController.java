package com.magic.lontrasarcanas.decktracker.controller;

import com.magic.lontrasarcanas.decktracker.dto.DadosAutenticacao;
import com.magic.lontrasarcanas.decktracker.model.Usuario;
import com.magic.lontrasarcanas.decktracker.service.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity efetuarLogin(@RequestBody DadosAutenticacao dados) {
        // Empacota o login e senha
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        
        // O Spring vai lá no banco de dados verificar se bate
        var authentication = manager.authenticate(authenticationToken);
        
        // Se bater, gera o crachá e devolve
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        // Devolve o token dentro de um objeto JSON
        return ResponseEntity.ok(new RetornoToken(tokenJWT));
    }

    // Um record interno rápido só para formatar a resposta JSON
    private record RetornoToken(String token) {}
}
