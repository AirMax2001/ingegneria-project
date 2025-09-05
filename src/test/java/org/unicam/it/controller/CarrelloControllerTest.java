package org.unicam.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.unicam.it.model.Acquirente;
import org.unicam.it.model.Carrello;
import org.unicam.it.service.CarrelloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CarrelloControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CarrelloService carrelloService;

    @InjectMocks
    private CarrelloController carrelloController;

    private ObjectMapper objectMapper;
    private Carrello testCarrello;
    private Acquirente testAcquirente;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(carrelloController).build();
        objectMapper = new ObjectMapper();
        testAcquirente = new Acquirente("acquirente@test.com", "password", "Acquirente", "Test");
        testAcquirente.setId("acquirente123");

        testCarrello = new Carrello(testAcquirente);
        testCarrello.setId("carrello123");
    }

    @Test
    void testAggiungiProdotto_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "userId", "acquirente123",
            "prodottoId", "prodotto123",
            "quantita", "2"
        );

        when(carrelloService.aggiungiProdottoAlCarrello(anyString(), anyString(), anyInt()))
            .thenReturn(testCarrello);

        // When & Then
        mockMvc.perform(post("/api/carrello/aggiungi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto aggiunto al carrello"))
                .andExpect(jsonPath("$.carrello.id").value("carrello123"));
    }

    @Test
    void testAggiungiPacchetto_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "userId", "acquirente123",
            "pacchettoId", "pacchetto123",
            "quantita", "1"
        );

        when(carrelloService.aggiungiPacchettoAlCarrello(anyString(), anyString(), anyInt()))
            .thenReturn(testCarrello);

        // When & Then
        mockMvc.perform(post("/api/carrello/aggiungi-pacchetto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto aggiunto al carrello"))
                .andExpect(jsonPath("$.carrello.id").value("carrello123"));
    }

    @Test
    void testRimuoviProdotto_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "userId", "acquirente123",
            "prodottoId", "prodotto123"
        );

        when(carrelloService.rimuoviProdottoDalCarrello(anyString(), anyString()))
            .thenReturn(testCarrello);

        // When & Then
        mockMvc.perform(delete("/api/carrello/rimuovi")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto rimosso dal carrello"));
    }

    @Test
    void testRimuoviPacchetto_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "userId", "acquirente123",
            "pacchettoId", "pacchetto123"
        );

        when(carrelloService.rimuoviPacchettoDalCarrello(anyString(), anyString()))
            .thenReturn(testCarrello);

        // When & Then
        mockMvc.perform(delete("/api/carrello/rimuovi-pacchetto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto rimosso dal carrello"));
    }

    @Test
    void testGetCarrello_Success() throws Exception {
        // Given
        when(carrelloService.getCarrelloByUser(anyString()))
            .thenReturn(testCarrello);

        // When & Then
        mockMvc.perform(get("/api/carrello/acquirente123"))
                .andExpect(status().isOk());
    }

    @Test
    void testSvuotaCarrello_Success() throws Exception {
        // Given
        doNothing().when(carrelloService).svuotaCarrello(anyString());

        // When & Then
        mockMvc.perform(post("/api/carrello/acquirente123/svuota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Carrello svuotato con successo"));
    }
}
