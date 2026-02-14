package com.magic.lontrasarcanas.decktracker.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.magic.lontrasarcanas.decktracker.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Puxa aquela senha secreta que colocamos no application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("lontras-arcanas-api")
                    .withSubject(usuario.getLogin()) // Guarda o login dentro do token
                    .withExpiresAt(dataExpiracao()) // O token expira em 2 horas
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("lontras-arcanas-api")
                    .build()
                    .verify(tokenJWT) // Descriptografa e valida
                    .getSubject(); // Devolve o login do usuário
        } catch (JWTVerificationException exception) {
            return ""; // Se o token for falso ou expirado, retorna vazio
        }
    }

    private Instant dataExpiracao() {
        // Validade do crachá: 24 horas a partir do momento do login (fuso horário de Brasília)
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
    }
}
