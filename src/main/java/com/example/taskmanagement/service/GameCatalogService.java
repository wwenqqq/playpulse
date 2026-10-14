package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Game;
import com.example.taskmanagement.entity.LibraryEntry;
import com.example.taskmanagement.dto.GameCreateDTO;
import com.example.taskmanagement.repository.GameRepository;
import com.example.taskmanagement.repository.LibraryEntryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @ConditionalOnExpression("'${app.role:all}' == 'feedback' or '${app.role:all}' == 'all'")
public class GameCatalogService {
    private final GameRepository games;
    private final LibraryEntryRepository library;

    @PostConstruct
    void seedCatalogue() {
        seed("Signal Lost", "Northstar Assembly", 2025, "Narrative, Adventure", "lime", "A maintenance technician follows a fading broadcast through an abandoned orbital station.");
        seed("Rook & River", "Cairn Studio", 2024, "Strategy, Roguelite", "amber", "Build a tiny kingdom one uncertain season at a time.");
        seed("After the Rain", "Mallow Works", 2026, "Cozy, Simulation", "violet", "Restore a rain-soaked coastal town, one small kindness at a time.");
        seed("Hollow Circuit", "Vectorline", 2023, "Action, Sci-fi", "cyan", "Dash through a fractured simulation where every arena changes the rules.");
        seed("Paper Wilds", "Kinfolk Games", 2025, "Exploration, Puzzle", "rose", "Map a folded world that opens differently for every traveller.");
        seed("Frostline", "Blue Lantern", 2024, "Survival, Co-op", "ice", "Keep a remote research crew alive through a long polar night.");
    }
    private void seed(String title, String studio, int year, String genres, String tone, String summary) {
        if (games.existsByTitle(title)) return;
        try { Game game = new Game(); game.setTitle(title); game.setStudio(studio); game.setReleaseYear(year); game.setGenres(genres); game.setCoverTone(tone); game.setSummary(summary); games.saveAndFlush(game); }
        catch (DataIntegrityViolationException ignored) { /* another replica seeded this game first */ }
    }
    public List<Game> list(String search) { return search == null || search.isBlank() ? games.findAll() : games.findByTitleContainingIgnoreCaseOrStudioContainingIgnoreCase(search, search); }
    public Game get(Long id) { return games.findById(id).orElseThrow(() -> new RuntimeException("Game not found")); }
    @Transactional public Game create(Long userId, GameCreateDTO data) {
        if (games.existsByTitle(data.getTitle().trim())) throw new RuntimeException("A game with this title already exists. Search the catalogue first.");
        Game game = new Game();
        game.setTitle(data.getTitle().trim()); game.setStudio(data.getStudio().trim());
        game.setReleaseYear(data.getReleaseYear()); game.setGenres(data.getGenres().trim());
        game.setCoverTone(data.getCoverTone()); game.setSummary(data.getSummary().trim()); game.setCreatedBy(userId);
        try { return games.saveAndFlush(game); }
        catch (DataIntegrityViolationException ignored) { throw new RuntimeException("A game with this title already exists. Search the catalogue first."); }
    }
    @Transactional public LibraryEntry saveLibrary(Long userId, Long gameId, LibraryEntry.Status status) {
        get(gameId);
        LibraryEntry entry = library.findByUserIdAndGameId(userId, gameId).orElseGet(() -> { LibraryEntry next = new LibraryEntry(); next.setUserId(userId); next.setGameId(gameId); return next; });
        entry.setStatus(status == null ? LibraryEntry.Status.WISHLIST : status);
        return library.save(entry);
    }
    public List<Map<String,Object>> library(Long userId) {
        return library.findByUserIdOrderByCreatedAtDesc(userId).stream().map(entry -> {
            Game game = get(entry.getGameId()); Map<String,Object> item = new LinkedHashMap<>();
            item.put("id", entry.getId()); item.put("status", entry.getStatus()); item.put("game", game); return item;
        }).toList();
    }
}
