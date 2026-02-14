package com.magic.lontrasarcanas.decktracker.service;

import com.magic.lontrasarcanas.decktracker.model.DeckOponente;
import com.magic.lontrasarcanas.decktracker.dto.CartaDTO;
import com.magic.lontrasarcanas.decktracker.dto.DeckDTO;
import com.magic.lontrasarcanas.decktracker.dto.DeckStatsDTO;
import com.magic.lontrasarcanas.decktracker.dto.PartidaDTO;
import com.magic.lontrasarcanas.decktracker.model.Deck;
import com.magic.lontrasarcanas.decktracker.model.Partida;
import com.magic.lontrasarcanas.decktracker.model.Usuario;
import com.magic.lontrasarcanas.decktracker.repository.DeckRepository;
import com.magic.lontrasarcanas.decktracker.service.validators.DeckValidator;
import com.magic.lontrasarcanas.decktracker.repository.PartidaRepository;
import com.magic.lontrasarcanas.decktracker.model.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.magic.lontrasarcanas.decktracker.repository.DeckOponenteRepository;
import com.magic.lontrasarcanas.decktracker.dto.MatchupStatsDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeckService {

    private final DeckRepository deckRepository;
    private final PartidaRepository partidaRepository;
    private final ScryfallService scryfallService;
    private final DeckValidator deckValidator;
    private final DeckOponenteRepository deckOponenteRepository;

    public DeckService(DeckRepository deckRepository, 
                       ScryfallService scryfallService, 
                       DeckValidator deckValidator,
                       PartidaRepository partidaRepository,
                       DeckOponenteRepository deckOponenteRepository) {
        this.deckRepository = deckRepository;
        this.scryfallService = scryfallService;
        this.deckValidator = deckValidator;
        this.partidaRepository = partidaRepository;
        this.deckOponenteRepository = deckOponenteRepository;
    }

    // 1. MÉTODO AUXILIAR PARA PEGAR QUEM ESTÁ LOGADO
    private Usuario getUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    public List<Deck> listarTodos() {
        // 1. Pega o crachá do usuário que fez a requisição
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var usuarioLogado = (com.magic.lontrasarcanas.decktracker.model.Usuario) authentication.getPrincipal();
        
        // 2. Vai no banco e busca APENAS os decks daquele ID específico
        return deckRepository.findByUsuarioId(usuarioLogado.getId()); 
    }

    // 3. ATUALIZE O MÉTODO DE SALVAR
    public Deck salvarDeck(Deck deck) {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        var usuarioLogado = (com.magic.lontrasarcanas.decktracker.model.Usuario) authentication.getPrincipal();

        deck.setUsuario(usuarioLogado); // Associa o deck ao usuário logado

        return deckRepository.save(deck);
    }

    public Deck atualizarDeck(Long id, Deck deckAtualizado) {
        // 1. Busca o deck existente no banco para não perder o ID e as partidas já associadas
        Deck deckExistente = deckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado para edição."));

        System.out.println("Editando deck: " + deckExistente.getName() + " -> " + deckAtualizado.getName());

        // 2. Atualiza apenas os campos permitidos
        deckExistente.setName(deckAtualizado.getName());
        deckExistente.setDeckList(deckAtualizado.getDeckList());
        // Não mexemos no Formato (Pauper) nem nas Partidas

        // 3. Converte para DTO para passar na sua validação lógica (60 cartas, 4 cópias, etc)
        DeckDTO deckDTO = converterParaDTO(deckExistente);
        deckValidator.validar(deckDTO);

        // 4. Validação de Legalidade na API do Scryfall
        scryfallService.verificarLegalidadePauper(deckExistente.getDeckList());

        // 5. Salva a alteração no banco (como o objeto já tem um ID, o Hibernate faz um UPDATE automático)
        return deckRepository.save(deckExistente);
    }

    @Transactional
    public void registrarPartida(Long deckId, PartidaDTO dto) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado"));

        // Magia aqui: Busca se o oponente já existe para este deck. Se não existir, cria e salva na hora!
        DeckOponente oponente = deckOponenteRepository.findByDeckIdAndNomeArquetipo(deckId, dto.getArquetipoAdversario())
                .orElseGet(() -> {
                    DeckOponente novoOponente = new DeckOponente();
                    novoOponente.setNomeArquetipo(dto.getArquetipoAdversario());
                    novoOponente.setDeck(deck);
                    return deckOponenteRepository.save(novoOponente);
                });

        Partida partida = new Partida();
        partida.setQtdVitorias(dto.getQtdVitorias());
        partida.setQtdDerrotas(dto.getQtdDerrotas());
        partida.setDeckOponente(oponente); // A partida agora é vinculada ao Oponente!

        partidaRepository.save(partida);
    }

    @Transactional(readOnly = true)
    public DeckStatsDTO calcularEstatisticas(Long deckId) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado"));

        int totalMatchesGeral = 0;
        int matchWinsGeral = 0;
        int totalGamesGeral = 0;
        int gameWinsGeral = 0;
        int gameLossesGeral = 0;

        List<MatchupStatsDTO> matchupsStats = new ArrayList<>();
        // Navega pelos Oponentes enfrentados por este Deck
        for (DeckOponente oponente : deck.getOponentes()) {
            int matchWinsOponente = 0;
            int totalMatchesOponente = oponente.getPartidas().size();
            
            // Variáveis novas para os games deste oponente
            int gameWinsOponente = 0;
            int gameLossesOponente = 0;

            // Navega pelas partidas APENAS contra este oponente específico
            for (Partida p : oponente.getPartidas()) {
                if (p.getQtdVitorias() > p.getQtdDerrotas()) {
                    matchWinsOponente++;
                    matchWinsGeral++; // Soma também no geral
                }
                
                // Soma os games específicos do oponente
                gameWinsOponente += p.getQtdVitorias();
                gameLossesOponente += p.getQtdDerrotas();
                
                // Soma os games gerais
                totalGamesGeral += (p.getQtdVitorias() + p.getQtdDerrotas());
                gameWinsGeral += p.getQtdVitorias();
                gameLossesGeral += p.getQtdDerrotas();
            }
            totalMatchesGeral += totalMatchesOponente;

            // Monta as estatísticas desse Matchup
            if (totalMatchesOponente > 0) {
                MatchupStatsDTO matchStat = new MatchupStatsDTO();
                matchStat.setNomeArquetipo(oponente.getNomeArquetipo());
                matchStat.setTotalMatches(totalMatchesOponente);
                matchStat.setMatchWins(matchWinsOponente);
                matchStat.setMatchLosses(totalMatchesOponente - matchWinsOponente);
                matchStat.setMatchWinRate((double) matchWinsOponente / totalMatchesOponente * 100);
                
                // Salva as estatísticas de game deste oponente
                int totalGamesOponente = gameWinsOponente + gameLossesOponente;
                matchStat.setTotalGames(totalGamesOponente);
                matchStat.setGameWins(gameWinsOponente);
                matchStat.setGameLosses(gameLossesOponente);
                matchStat.setGameWinRate(totalGamesOponente > 0 ? (double) gameWinsOponente / totalGamesOponente * 100 : 0.0);
                
                matchupsStats.add(matchStat);
            }
        }

        int matchLossesGeral = totalMatchesGeral - matchWinsGeral;
        double winRateGeral = totalMatchesGeral > 0 ? (double) matchWinsGeral / totalMatchesGeral * 100 : 0.0;
        double gameWinRateGeral = totalGamesGeral > 0 ? (double) gameWinsGeral / totalGamesGeral * 100 : 0.0;

        DeckStatsDTO stats = new DeckStatsDTO();
        stats.setDeckName(deck.getName());
        stats.setListaCartas(deck.getDeckList());

        // Estatísticas Gerais
        stats.setTotalMatches(totalMatchesGeral);
        stats.setMatchWins(matchWinsGeral);
        stats.setMatchLosses(matchLossesGeral);
        stats.setMatchWinRate(winRateGeral);
        stats.setTotalGames(totalGamesGeral);
        stats.setGameWins(gameWinsGeral);
        stats.setGameLosses(gameLossesGeral);
        stats.setGameWinRate(gameWinRateGeral);

        // A Cereja do Bolo: A lista detalhada por oponente!
        stats.setMatchups(matchupsStats);

        return stats;
    }

    /**
     * Converte a String crua (ex: "4 Lightning Bolt\n2 Mountain") 
     * para o formato estruturado que o Validador entende.
     */
    private DeckDTO converterParaDTO(Deck deck) {
        DeckDTO dto = new DeckDTO();
        dto.setNome(deck.getName());

        List<CartaDTO> main = new ArrayList<>();
        List<CartaDTO> side = new ArrayList<>();
        boolean isSideboard = false;

        if (deck.getDeckList() == null || deck.getDeckList().isBlank()) {
            return dto; // Retorna vazio se não houver lista
        }

        // Quebra o texto em linhas
        String[] linhas = deck.getDeckList().split("\\r?\\n");

        for (String linha : linhas) {
            linha = linha.trim();
            
            // Pula linhas vazias
            if (linha.isEmpty()) continue;

            // Detecta cabeçalho de Sideboard (comum em exportações do Arena/MTGO)
            if (linha.equalsIgnoreCase("Sideboard") || linha.equalsIgnoreCase("Sideboard:")) {
                isSideboard = true;
                continue;
            }

            // Lógica de Parsing: "4 Lightning Bolt" -> Qtd: 4, Nome: "Lightning Bolt"
            try {
                int primeiroEspaco = linha.indexOf(' ');
                
                if (primeiroEspaco > 0) {
                    // Pega tudo antes do primeiro espaço como número
                    String qtdStr = linha.substring(0, primeiroEspaco).replace("x", ""); // Trata "4x"
                    int quantidade = Integer.parseInt(qtdStr);
                    
                    // O resto é o nome da carta
                    String nomeCarta = linha.substring(primeiroEspaco + 1).trim();

                    CartaDTO carta = new CartaDTO();
                    carta.setNome(nomeCarta);
                    carta.setQuantidade(quantidade);
                    
                    if (isSideboard) {
                        side.add(carta);
                    } else {
                        main.add(carta);
                    }
                }
            } catch (NumberFormatException e) {
                // Se a linha não começar com número, ignoramos (pode ser comentário ou cabeçalho inválido)
                System.out.println("Linha ignorada no parser: " + linha);
            }
        }
        
        dto.setMainDeck(main);
        dto.setSideDeck(side);

        return dto;
    }

    public void excluir(Long Id) {
        if (deckRepository.existsById(Id)) {
            deckRepository.deleteById(Id);
        } else {
            throw new IllegalArgumentException("Deck não encontrado para exclusão");
        }
    }
}
