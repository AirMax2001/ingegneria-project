package org.unicam.it.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.unicam.it.model.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private UtenteLoggato testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        testUser = new Acquirente("test@example.com", "password", "Test", "User");
        testUser.setId("user123");
    }

    @Test
    void testRegistrazione_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "email", "test@example.com",
            "password", "password",
            "nome", "Test",
            "cognome", "User"
        );

        when(userService.registraUtente(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/auth/registrazione")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registrazione completata con successo come Acquirente"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testRegistrazioneConRuolo_Success() throws Exception {
        // Given
        Venditore venditore = new Venditore("venditore@example.com", "password", "Venditore", "Test");
        venditore.setId("venditore123");

        Map<String, String> request = Map.of(
            "email", "venditore@example.com",
            "password", "password",
            "nome", "Venditore",
            "cognome", "Test",
            "ruolo", "VENDITORE"
        );

        when(userService.registraUtente(anyString(), anyString(), anyString(), anyString(), eq(UserRole.VENDITORE)))
            .thenReturn(venditore);

        // When & Then
        mockMvc.perform(post("/api/auth/registrazione-con-ruolo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registrazione completata con successo come Venditore"))
                .andExpect(jsonPath("$.userId").value("venditore123"));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "email", "test@example.com",
            "password", "password"
        );

        when(userService.login(anyString(), anyString()))
            .thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login effettuato con successo"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "email", "test@example.com",
            "password", "wrongpassword"
        );

        when(userService.login(anyString(), anyString()))
            .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Credenziali non valide"));
    }
}
