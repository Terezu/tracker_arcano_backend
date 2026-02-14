package com.magic.lontrasarcanas.decktracker.service;

import com.magic.lontrasarcanas.decktracker.integration.ScryfallCardDTO;
import com.magic.lontrasarcanas.decktracker.integration.ScryfallResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ScryfallService {

    private final String SCRYFALL_API_URL_COLLECTION = "https://api.scryfall.com/cards/collection";
    private final String SCRYFALL_API_URL_NAMED = "https://api.scryfall.com/cards/named";

    // 1. Nosso Cache em memória para não sobrecarregar a API
    private final Map<String, byte[]> cacheDeImagens = new ConcurrentHashMap<>();

    // 2. Método principal que será chamado pelo seu Controller
    public byte[] buscarImagemDaCarta(String nomeDaCarta) {
        // Padroniza o nome para evitar que "Lightning Bolt" e "lightning bolt" gerem dois downloads
        String nomeFormatado = nomeDaCarta.trim().toLowerCase();

        // Verifica se a imagem já está salva na memória
        if (cacheDeImagens.containsKey(nomeFormatado)) {
            System.out.println("Servindo do CACHE: " + nomeFormatado);
            return cacheDeImagens.get(nomeFormatado);
        }

        // Se não encontrou, faz a requisição pro Scryfall
        System.out.println("Baixando do SCRYFALL: " + nomeFormatado);
        byte[] imagemBaixada = baixarImagemDoScryfall(nomeFormatado);

        // Salva no cache para as próximas requisições do Angular (se o download não falhou)
        if (imagemBaixada != null && imagemBaixada.length > 0) {
            cacheDeImagens.put(nomeFormatado, imagemBaixada);
        }

        return imagemBaixada;
    }

    // 3. Método privado que efetivamente faz a requisição HTTP
    private byte[] baixarImagemDoScryfall(String nomeDaCarta) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Montamos a URL pedindo a versão 'normal' e o formato de imagem
            String url = SCRYFALL_API_URL_NAMED + "?exact=" + nomeDaCarta + "&format=image&version=normal";
            
            // O RestTemplate segue os redirecionamentos do Scryfall e já baixa os bytes
            return restTemplate.getForObject(url, byte[].class);
        } catch (Exception e) {
            System.err.println("Erro ao baixar imagem da carta " + nomeDaCarta + ": " + e.getMessage());
            // Em caso de erro (carta não encontrada, por exemplo), retorna null ou um byte array vazio
            return null; 
        }
    }

    public void verificarLegalidadePauper(String deckListTexto) {
        if (deckListTexto == null || deckListTexto.trim().isEmpty()) {
            throw new RuntimeException("A lista do deck é obrigatória!");
        }

        List<String> nomesCartas = extrairNomesCartas(deckListTexto);

        if (nomesCartas.isEmpty()) {
             throw new RuntimeException("A lista do deck está vazia ou ilegível.");
        }

        Map<String, List<Map<String, String>>> requestBody = new HashMap<>();
        List<Map<String, String>> identifiers = nomesCartas.stream()
                .map(nome -> Map.of("name", nome))
                .collect(Collectors.toList());
        requestBody.put("identifiers", identifiers);

        RestTemplate restTemplate = new RestTemplate();
        ScryfallResponseDTO resposta = restTemplate.postForObject(SCRYFALL_API_URL_COLLECTION, requestBody, ScryfallResponseDTO.class);

        if (resposta != null) {
            if (resposta.getNot_found() != null && !resposta.getNot_found().isEmpty()) {
                throw new RuntimeException("Cartas não encontradas no Scryfall: " + resposta.getNot_found());
            }

            List<String> cartasIlegais = new ArrayList<>();
            for (ScryfallCardDTO carta : resposta.getData()) {
                String legalidadePauper = carta.getLegalities().get("pauper");
                if (!"legal".equals(legalidadePauper)) {
                    cartasIlegais.add(carta.getName() + " (" + legalidadePauper + ")");
                }
            }

            if (!cartasIlegais.isEmpty()) {
                throw new RuntimeException("Deck ilegal no Pauper! Cartas inválidas: " + cartasIlegais);
            }
        }
    }

    private List<String> extrairNomesCartas(String texto) {
        List<String> nomes = new ArrayList<>();
        String[] linhas = texto.split("\\r?\\n");
        Pattern pattern = Pattern.compile("^\\d+\\s+(.*)$"); 

        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("Sideboard")) continue;

            Matcher matcher = pattern.matcher(linha);
            if (matcher.find()) {
                nomes.add(matcher.group(1));
            } else {
                nomes.add(linha);
            }
        }
        return nomes;
    }
}
