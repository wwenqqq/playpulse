package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.GameCreateDTO;
import com.example.taskmanagement.entity.Game;
import com.example.taskmanagement.repository.GameRepository;
import com.example.taskmanagement.repository.LibraryEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameCatalogServiceTest {

    @Mock private GameRepository games;
    @Mock private LibraryEntryRepository library;
    @InjectMocks private GameCatalogService service;

    @Test
    void createStoresTrimmedCatalogueEntryAndCreator() {
        GameCreateDTO data = game("  Lantern Coast  ");
        when(games.existsByTitle("Lantern Coast")).thenReturn(false);
        when(games.saveAndFlush(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game created = service.create(42L, data);

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        verify(games).saveAndFlush(captor.capture());
        assertEquals("Lantern Coast", captor.getValue().getTitle());
        assertEquals("Small Hours", created.getStudio());
        assertEquals(42L, created.getCreatedBy());
    }

    @Test
    void createRejectsDuplicateTitleBeforeSaving() {
        GameCreateDTO data = game("Lantern Coast");
        when(games.existsByTitle("Lantern Coast")).thenReturn(true);

        RuntimeException error = assertThrows(RuntimeException.class, () -> service.create(42L, data));

        assertEquals("A game with this title already exists. Search the catalogue first.", error.getMessage());
        verify(games, never()).saveAndFlush(any(Game.class));
    }

    private GameCreateDTO game(String title) {
        GameCreateDTO data = new GameCreateDTO();
        data.setTitle(title);
        data.setStudio("  Small Hours  ");
        data.setReleaseYear(2026);
        data.setGenres("  Adventure, Puzzle  ");
        data.setCoverTone("amber");
        data.setSummary("  Follow a quiet signal across a tidal town.  ");
        return data;
    }
}
