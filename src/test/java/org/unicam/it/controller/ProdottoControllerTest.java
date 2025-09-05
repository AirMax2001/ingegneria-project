package org.unicam.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.unicam.it.model.Prodotto;
import org.unicam.it.model.StatoProdotto;
import org.unicam.it.model.Venditore;
import org.unicam.it.service.ProdottoService;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProdottoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProdottoService prodottoService;

    @InjectMocks
    private ProdottoController prodottoController;

    private ObjectMapper objectMapper;
    private Prodotto testProdotto;
    private Venditore testVenditore;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(prodottoController).build();
        objectMapper = new ObjectMapper();
        testVenditore = new Venditore("venditore@test.com", "password", "Venditore", "Test");
        testVenditore.setId("venditore123");

        testProdotto = new Prodotto("Test Product", "Description", new BigDecimal("99.99"), 10, testVenditore);
        testProdotto.setId("prodotto123");
        testProdotto.setCategoria("Electronics");
        testProdotto.setStato(StatoProdotto.IN_ATTESA_APPROVAZIONE);
    }

    @Test
    void testPubblicaProdotto_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "venditorId", "venditore123",
            "nome", "Test Product",
            "descrizione", "Description",
            "prezzo", "99.99",
            "quantita", "10",
            "categoria", "Electronics"
        );

        when(prodottoService.pubblicaProdotto(anyString(), anyString(), anyString(), any(BigDecimal.class), anyInt(), anyString()))
            .thenReturn(testProdotto);

        // When & Then
        mockMvc.perform(post("/api/prodotti/pubblica")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto pubblicato e in attesa di approvazione"))
                .andExpect(jsonPath("$.prodottoId").value("prodotto123"))
                .andExpect(jsonPath("$.stato").value("IN_ATTESA_APPROVAZIONE"));
    }

    @Test
    void testModificaProdotto_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "venditorId", "venditore123",
            "nome", "Updated Product",
            "descrizione", "Updated Description",
            "prezzo", "129.99",
            "quantita", "15"
        );

        Prodotto updatedProdotto = new Prodotto("Updated Product", "Updated Description", new BigDecimal("129.99"), 15, testVenditore);
        updatedProdotto.setId("prodotto123");
        updatedProdotto.setCategoria("Electronics");

        when(prodottoService.modificaProdotto(anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class), anyInt()))
            .thenReturn(updatedProdotto);

        // When & Then
        mockMvc.perform(put("/api/prodotti/prodotto123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto modificato con successo"));
    }

    @Test
    void testEliminaProdotto_Success() throws Exception {
        // Given
        doNothing().when(prodottoService).eliminaProdotto(anyString(), anyString());

        // When & Then
        mockMvc.perform(delete("/api/prodotti/prodotto123")
                .param("venditorId", "venditore123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto eliminato con successo"));
    }

    @Test
    void testVisualizzaProdotti_Success() throws Exception {
        // Given
        List<Prodotto> prodotti = Arrays.asList(testProdotto);
        when(prodottoService.visualizzaProdottiApprovati()).thenReturn(prodotti);

        // When & Then
        mockMvc.perform(get("/api/prodotti/visualizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("prodotto123"));
    }

    @Test
    void testCercaProdotto_Success() throws Exception {
        // Given
        List<Prodotto> prodotti = Arrays.asList(testProdotto);
        when(prodottoService.cercaProdotto(anyString())).thenReturn(prodotti);

        // When & Then
        mockMvc.perform(get("/api/prodotti/cerca")
                .param("nome", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("prodotto123"));
    }

    @Test
    void testApprovaProdotto_Success() throws Exception {
        // Given
        testProdotto.setStato(StatoProdotto.APPROVATO);
        when(prodottoService.approvaProdotto(anyString(), anyString()))
            .thenReturn(testProdotto);

        // When & Then
        mockMvc.perform(post("/api/prodotti/prodotto123/approva")
                .param("curatoreId", "curatore123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto approvato con successo"));
    }

    @Test
    void testRifiutaProdotto_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "curatoreId", "curatore123",
            "motivoRifiuto", "Non conforme agli standard"
        );

        testProdotto.setStato(StatoProdotto.RIFIUTATO);
        when(prodottoService.rifiutaProdotto(anyString(), anyString(), anyString()))
            .thenReturn(testProdotto);

        // When & Then
        mockMvc.perform(post("/api/prodotti/prodotto123/rifiuta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prodotto rifiutato"));
    }
}
