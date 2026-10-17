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
        migrateSeed("Signal Lost", "Minecraft", "Mojang Studios", 2011, "Sandbox, Survival", "lime", "Explore an endless block-built world, gather resources and create anything from a first shelter to a vast shared city.");
        migrateSeed("Rook & River", "Stardew Valley", "ConcernedApe", 2016, "Farming, Simulation", "amber", "Restore an inherited farm, get to know Pelican Town and shape a gentle routine across changing seasons.");
        migrateSeed("After the Rain", "Terraria", "Re-Logic", 2011, "Sandbox, Adventure", "violet", "Dig, build and battle through a layered pixel world filled with hidden biomes, bosses and improbable equipment.");
        migrateSeed("Hollow Circuit", "Elden Ring", "FromSoftware", 2022, "Action RPG, Fantasy", "rose", "Cross the Lands Between, master demanding combat and piece together a vast dark fantasy shaped by exploration.");
        migrateSeed("Paper Wilds", "Hollow Knight", "Team Cherry", 2017, "Metroidvania, Action", "cyan", "Descend into Hallownest to uncover forgotten paths, precise battles and the history of a fallen insect kingdom.");
        migrateSeed("Frostline", "Hades", "Supergiant Games", 2020, "Roguelike, Action", "ice", "Fight out of the Underworld through fast repeatable runs where every defeat reveals new powers, characters and stories.");
    }
    private void migrateSeed(String legacyTitle, String title, String studio, int year, String genres, String tone, String summary) {
        if (games.existsByTitle(title)) return;
        Optional<Game> legacy = games.findByTitle(legacyTitle);
        if (legacy.isEmpty()) { seed(title, studio, year, genres, tone, summary); return; }
        Game game = legacy.get();
        game.setTitle(title); game.setStudio(studio); game.setReleaseYear(year);
        game.setGenres(genres); game.setCoverTone(tone); game.setSummary(summary);
        try { games.saveAndFlush(game); }
        catch (DataIntegrityViolationException ignored) { /* another replica migrated this game first */ }
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
