package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.PacchettoProdottiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PacchettoProdottiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PacchettoProdottiService pacchettoProdottiService;

    @InjectMocks
    private PacchettoProdottiController pacchettoProdottiController;

    private ObjectMapper objectMapper;
    private PacchettoProdotti testPacchetto;
    private Distributore testDistributore;
    private List<Prodotto> testProdotti;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pacchettoProdottiController).build();
        objectMapper = new ObjectMapper();
        testDistributore = new Distributore("distributore@test.com", "password", "Distributore", "Test");
        testDistributore.setId("distributore123");

        // Crea prodotti per il pacchetto
        Venditore venditore = new Venditore("venditore@test.com", "password", "Venditore", "Test");
        venditore.setId("venditore123");

        Prodotto prodotto1 = new Prodotto("Prodotto 1", "Descrizione 1", new BigDecimal("50.00"), 10, venditore);
        prodotto1.setId("prodotto1");
        prodotto1.setCategoria("Electronics");
        prodotto1.setStato(StatoProdotto.APPROVATO);

        Prodotto prodotto2 = new Prodotto("Prodotto 2", "Descrizione 2", new BigDecimal("30.00"), 15, venditore);
        prodotto2.setId("prodotto2");
        prodotto2.setCategoria("Electronics");
        prodotto2.setStato(StatoProdotto.APPROVATO);

        testProdotti = Arrays.asList(prodotto1, prodotto2);

        testPacchetto = new PacchettoProdotti("Pacchetto Test", "Pacchetto di prova", testDistributore);
        testPacchetto.setId("pacchetto123");
        testPacchetto.setProdotti(testProdotti);
        testPacchetto.setPrezzoTotaleOriginale(new BigDecimal("80.00"));
        testPacchetto.setPrezzoScontato(new BigDecimal("70.00"));
        testPacchetto.setPercentualeSconto(new BigDecimal("12.5"));
        testPacchetto.setQuantitaDisponibile(5);
        testPacchetto.setDataScadenza(LocalDateTime.of(2025, 12, 31, 23, 59));
        testPacchetto.setStato(StatoProdotto.IN_ATTESA_APPROVAZIONE);
    }

    @Test
    void testCreaPacchetto_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "distributoreId", "distributore123",
            "nome", "Pacchetto Test",
            "descrizione", "Pacchetto di prova",
            "prodottiIds", Arrays.asList("prodotto1", "prodotto2"),
            "percentualeSconto", "12.5",
            "dataScadenza", "2025-12-31T23:59:00",
            "quantitaDisponibile", "5"
        );

        when(pacchettoProdottiService.creaPacchetto(anyString(), anyString(), anyString(),
            anyList(), any(BigDecimal.class), any(LocalDateTime.class), anyInt()))
            .thenReturn(testPacchetto);

        // When & Then
        mockMvc.perform(post("/api/pacchetti/crea")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto prodotti creato con successo e in attesa di approvazione"))
                .andExpect(jsonPath("$.pacchetto.id").value("pacchetto123"))
                .andExpect(jsonPath("$.pacchetto.nome").value("Pacchetto Test"))
                .andExpect(jsonPath("$.pacchetto.prezzoOriginale").value(80.00))
                .andExpect(jsonPath("$.pacchetto.prezzoScontato").value(70.00))
                .andExpect(jsonPath("$.pacchetto.risparmio").value(10.00))
                .andExpect(jsonPath("$.pacchetto.stato").value("IN_ATTESA_APPROVAZIONE"))
                .andExpect(jsonPath("$.pacchetto.numeroProdotti").value(2));
    }

    @Test
    void testModificaPacchetto_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "distributoreId", "distributore123",
            "nome", "Pacchetto Aggiornato",
            "descrizione", "Descrizione aggiornata",
            "percentualeSconto", "15.0",
            "dataScadenza", "2025-12-31T23:59:00",
            "quantitaDisponibile", "10"
        );

        PacchettoProdotti updatedPacchetto = new PacchettoProdotti("Pacchetto Aggiornato", "Descrizione aggiornata", testDistributore);
        updatedPacchetto.setId("pacchetto123");

        when(pacchettoProdottiService.modificaPacchetto(anyString(), anyString(), anyString(), anyString(),
            any(BigDecimal.class), any(LocalDateTime.class), anyInt()))
            .thenReturn(updatedPacchetto);

        // When & Then
        mockMvc.perform(put("/api/pacchetti/pacchetto123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto modificato con successo"));
    }

    @Test
    void testEliminaPacchetto_Success() throws Exception {
        // Given
        doNothing().when(pacchettoProdottiService).eliminaPacchetto(anyString(), anyString());

        // When & Then
        mockMvc.perform(delete("/api/pacchetti/pacchetto123")
                .param("distributoreId", "distributore123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto eliminato con successo"));
    }

    @Test
    void testVisualizzaPacchetti_Success() throws Exception {
        // Given
        testPacchetto.setStato(StatoProdotto.APPROVATO);
        List<PacchettoProdotti> pacchetti = Arrays.asList(testPacchetto);
        when(pacchettoProdottiService.visualizzaPacchettiApprovati()).thenReturn(pacchetti);

        // When & Then
        mockMvc.perform(get("/api/pacchetti/visualizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("pacchetto123"));
    }

    @Test
    void testCercaPacchetti_Success() throws Exception {
        // Given
        testPacchetto.setStato(StatoProdotto.APPROVATO);
        List<PacchettoProdotti> pacchetti = Arrays.asList(testPacchetto);
        when(pacchettoProdottiService.cercaPacchetti(anyString())).thenReturn(pacchetti);

        // When & Then
        mockMvc.perform(get("/api/pacchetti/cerca")
                .param("nome", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("pacchetto123"));
    }

    @Test
    void testApprovaPacchetto_Success() throws Exception {
        // Given
        testPacchetto.setStato(StatoProdotto.APPROVATO);
        when(pacchettoProdottiService.approvaPacchetto(anyString(), anyString()))
            .thenReturn(testPacchetto);

        // When & Then
        mockMvc.perform(post("/api/pacchetti/pacchetto123/approva")
                .param("curatoreId", "curatore123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto approvato con successo"));
    }

    @Test
    void testRifiutaPacchetto_Success() throws Exception {
        // Given
        testPacchetto.setStato(StatoProdotto.RIFIUTATO);
        when(pacchettoProdottiService.rifiutaPacchetto(anyString(), anyString()))
            .thenReturn(testPacchetto);

        // When & Then
        mockMvc.perform(post("/api/pacchetti/pacchetto123/rifiuta")
                .param("curatoreId", "curatore123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pacchetto rifiutato"));
    }

    @Test
    void testGetPacchetto_Success() throws Exception {
        // Given
        when(pacchettoProdottiService.findById(anyString()))
            .thenReturn(testPacchetto);

        // When & Then
        mockMvc.perform(get("/api/pacchetti/pacchetto123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("pacchetto123"))
                .andExpect(jsonPath("$.nome").value("Pacchetto Test"))
                .andExpect(jsonPath("$.distributore.id").value("distributore123"));
    }
}
