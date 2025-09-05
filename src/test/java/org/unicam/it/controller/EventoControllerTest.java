package org.unicam.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.unicam.it.model.Acquirente;
import org.unicam.it.model.Animatore;
import org.unicam.it.model.Evento;
import org.unicam.it.model.UtenteLoggato;
import org.unicam.it.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    private ObjectMapper objectMapper;
    private Evento testEvento;
    private Animatore testAnimatore;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
        objectMapper = new ObjectMapper();
        testAnimatore = new Animatore("animatore@test.com", "password", "Animatore", "Test");
        testAnimatore.setId("animatore123");

        testEvento = new Evento();
        testEvento.setId("evento123");
        testEvento.setNome("Test Event");
        testEvento.setDescrizione("Test Description");
        testEvento.setDataInizio(LocalDateTime.of(2025, 8, 1, 10, 0));
        testEvento.setDataFine(LocalDateTime.of(2025, 8, 1, 18, 0));
        testEvento.setLuogo("Test Location");
        testEvento.setMaxPartecipanti(50);
        testEvento.setAnimatore(testAnimatore);
        testEvento.setStato(Evento.StatoEvento.PROGRAMMATO);
    }

    @Test
    void testPubblicaEvento_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "animatoreId", "animatore123",
            "nome", "Test Event",
            "descrizione", "Test Description",
            "dataInizio", "2025-08-01T10:00:00",
            "dataFine", "2025-08-01T18:00:00",
            "luogo", "Test Location",
            "maxPartecipanti", "50"
        );

        when(eventoService.creaEvento(anyString(), anyString(), anyString(),
            any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyInt()))
            .thenReturn(testEvento);

        // When & Then
        mockMvc.perform(post("/api/eventi/pubblica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento creato con successo"))
                .andExpect(jsonPath("$.evento.id").value("evento123"))
                .andExpect(jsonPath("$.evento.nome").value("Test Event"));
    }

    @Test
    void testModificaEvento_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "animatoreId", "animatore123",
            "nome", "Updated Event",
            "descrizione", "Updated Description",
            "dataInizio", "2025-08-02T10:00:00",
            "dataFine", "2025-08-02T18:00:00",
            "luogo", "Updated Location",
            "maxPartecipanti", "75"
        );

        Evento updatedEvento = new Evento();
        updatedEvento.setId("evento123");
        updatedEvento.setNome("Updated Event");

        when(eventoService.modificaEvento(anyString(), anyString(), anyString(), anyString(),
            any(LocalDateTime.class), any(LocalDateTime.class), anyString(), anyInt()))
            .thenReturn(updatedEvento);

        // When & Then
        mockMvc.perform(put("/api/eventi/evento123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento modificato con successo"));
    }

    @Test
    void testEliminaEvento_Success() throws Exception {
        // Given
        doNothing().when(eventoService).eliminaEvento(anyString(), anyString());

        // When & Then
        mockMvc.perform(delete("/api/eventi/evento123")
                .param("animatoreId", "animatore123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento eliminato con successo"));
    }

    @Test
    void testPrenotaEvento_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of("userId", "user123");

        // Crea un utente per i partecipanti
        UtenteLoggato partecipante = new Acquirente("user@test.com", "password", "User", "Test");
        partecipante.setId("user123");

        testEvento.setPartecipanti(Arrays.asList(partecipante));
        when(eventoService.prenotaEvento(anyString(), anyString()))
            .thenReturn(testEvento);

        // When & Then
        mockMvc.perform(post("/api/eventi/evento123/prenota")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento prenotato con successo"));
    }
}
