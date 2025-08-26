package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.OrdineService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrdineControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrdineService ordineService;

    @InjectMocks
    private OrdineController ordineController;

    private ObjectMapper objectMapper;
    private Ordine testOrdine;
    private Acquirente testAcquirente;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ordineController).build();
        objectMapper = new ObjectMapper();
        testAcquirente = new Acquirente("acquirente@test.com", "password", "Acquirente", "Test");
        testAcquirente.setId("acquirente123");

        testOrdine = new Ordine();
        testOrdine.setId("ordine123");
        testOrdine.setAcquirente(testAcquirente);
        testOrdine.setTotale(new BigDecimal("199.99"));
        testOrdine.setStato(StatoOrdine.CONFERMATO);
        testOrdine.setDataCreazione(LocalDateTime.now());
        testOrdine.setIndirizzoSpedizione("Via Test 123");
    }

    @Test
    void testCreaOrdine_Success() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
            "userId", "acquirente123",
            "indirizzoSpedizione", "Via Test 123"
        );

        when(ordineService.creaDaCarrello(anyString(), anyString(), any(MetodoPagamento.class)))
            .thenReturn(testOrdine);

        // When & Then
        mockMvc.perform(post("/api/ordini/crea")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ordine creato con successo"))
                .andExpect(jsonPath("$.ordine.id").value("ordine123"))
                .andExpect(jsonPath("$.ordine.totale").value(199.99))
                .andExpect(jsonPath("$.ordine.stato").value("CONFERMATO"));
    }

    @Test
    void testGetOrdiniByUtente_Success() throws Exception {
        // Given
        List<Ordine> ordini = Arrays.asList(testOrdine);
        when(ordineService.getOrdiniByAcquirente(anyString()))
            .thenReturn(ordini);

        // When & Then
        mockMvc.perform(get("/api/ordini/utente/acquirente123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("ordine123"));
    }

    @Test
    void testGetOrdine_Success() throws Exception {
        // Given
        when(ordineService.findById(anyString()))
            .thenReturn(testOrdine);

        // When & Then
        mockMvc.perform(get("/api/ordini/ordine123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ordine123"));
    }

    @Test
    void testAggiornaStato_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of("stato", "SPEDITO");

        testOrdine.setStato(StatoOrdine.SPEDITO);
        when(ordineService.aggiornaStatoOrdine(anyString(), any(StatoOrdine.class)))
            .thenReturn(testOrdine);

        // When & Then
        mockMvc.perform(put("/api/ordini/ordine123/stato")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stato ordine aggiornato"));
    }

    @Test
    void testGetAllOrdini_Success() throws Exception {
        // Given
        List<Ordine> ordini = Arrays.asList(testOrdine);
        when(ordineService.getAllOrdini())
            .thenReturn(ordini);

        // When & Then
        mockMvc.perform(get("/api/ordini/tutti"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("ordine123"));
    }
}
