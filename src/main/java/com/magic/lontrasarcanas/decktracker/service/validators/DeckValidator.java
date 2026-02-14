package com.magic.lontrasarcanas.decktracker.service.validators; // O pacote agora inclui .validators

import com.magic.lontrasarcanas.decktracker.dto.DeckDTO;
import com.magic.lontrasarcanas.decktracker.dto.CartaDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DeckValidator {

    // Lista de cartas que podem ter qualquer número de cópias no deck
    private static final Set<String> CARTAS_ILIMITADAS = Set.of(
        "Rat Colony", 
        "Persistent Petitioners",
        "Relentless Rats",
        "Shadowborn Apostle",
        "Dragon's Approach",
        "Slime Against Humanity"
    );

    // Lista de Terrenos Básicos (incluindo da Neve) que ignoram a regra de 4 cópias
    private static final Set<String> TERRENOS_BASICOS = Set.of(
        "Plains", "Island", "Swamp", "Mountain", "Forest",
        "Snow-Covered Plains", "Snow-Covered Island", "Snow-Covered Swamp", 
        "Snow-Covered Mountain", "Snow-Covered Forest",
        "Wastes", "Snow-Covered Wastes"
    );

    public void validar(DeckDTO deck) {
        if (deck == null) {
            throw new IllegalArgumentException("Deck não pode ser nulo.");
        }
        validarQuantidades(deck);
        validarRegraDeQuatroCopias(deck);
    }

    private void validarQuantidades(DeckDTO deck) {
        // Contagem do Maindeck
        long totalMain = calcularTotalCartas(deck.getMainDeck());

        if (totalMain < 60) {
            throw new IllegalArgumentException(
                String.format("Deck ilegal: O Maindeck deve ter exatamente 60 cartas. Encontrado: %d", totalMain)
            );
        }

        long totalSide = calcularTotalCartas(deck.getSideDeck());

        if (totalSide > 15) {
            throw new IllegalArgumentException(
                String.format("Deck ilegal: O Sideboard não pode exceder 15 cartas. Encontrado: %d", totalSide)
            );
        }
    }

    private void validarRegraDeQuatroCopias(DeckDTO deck) {
        Map<String, Integer> contagemGlobal = new HashMap<>();

        // Agrupa cartas do Main
        somarCartasNoMapa(deck.getMainDeck(), contagemGlobal);

        // Agrupa cartas do Side (se existir)
        if (deck.getSideDeck() != null) {
            somarCartasNoMapa(deck.getSideDeck(), contagemGlobal);
        }

        // Verifica violações
        for (Map.Entry<String, Integer> entry : contagemGlobal.entrySet()) {
            String nomeCarta = entry.getKey();
            Integer quantidadeTotal = entry.getValue();

            if (quantidadeTotal > 4) {
                if (!isExcecaoARegra(nomeCarta)) {
                    throw new IllegalArgumentException(
                        String.format("Deck ilegal: A carta '%s' possui %d cópias (Limite é 4).", nomeCarta, quantidadeTotal)
                    );
                }
            }
        }
    }

    private long calcularTotalCartas(List<CartaDTO> cartas) {
        if (cartas == null) return 0;
        return cartas.stream()
                .mapToInt(CartaDTO::getQuantidade)
                .sum();
    }

    private void somarCartasNoMapa(List<CartaDTO> cartas, Map<String, Integer> mapa) {
        if (cartas == null) return;
        
        for (CartaDTO carta : cartas) {
            String nome = carta.getNome(); 
            mapa.put(nome, mapa.getOrDefault(nome, 0) + carta.getQuantidade());
        }
    }

    private boolean isExcecaoARegra(String nomeCarta) {
        return TERRENOS_BASICOS.contains(nomeCarta) || CARTAS_ILIMITADAS.contains(nomeCarta);
    }
}
