package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.UserService;
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
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UtenteLoggato testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        testUser = new Acquirente("user@test.com", "password", "Test", "User");
        testUser.setId("user123");
        testUser.setTelefono("123456789");
        testUser.setIndirizzo("Via Test 123");
    }

    @Test
    void testModificaProfilo_Success() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "nome", "Updated Name",
            "cognome", "Updated Surname",
            "telefono", "987654321",
            "indirizzo", "Via Updated 456"
        );

        UtenteLoggato updatedUser = new Acquirente("user@test.com", "password", "Updated Name", "Updated Surname");
        updatedUser.setId("user123");
        updatedUser.setTelefono("987654321");
        updatedUser.setIndirizzo("Via Updated 456");

        when(userService.modificaProfilo(anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/utenti/user123/profilo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profilo modificato con successo"))
                .andExpect(jsonPath("$.user.id").value("user123"))
                .andExpect(jsonPath("$.user.nome").value("Updated Name"))
                .andExpect(jsonPath("$.user.cognome").value("Updated Surname"))
                .andExpect(jsonPath("$.user.telefono").value("987654321"))
                .andExpect(jsonPath("$.user.indirizzo").value("Via Updated 456"));
    }

    @Test
    void testEliminaProfilo_Success() throws Exception {
        // Given
        doNothing().when(userService).eliminaProfilo(anyString());

        // When & Then
        mockMvc.perform(delete("/api/utenti/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profilo eliminato con successo"));
    }

    @Test
    void testGetProfilo_Success() throws Exception {
        // Given
        when(userService.findById(anyString()))
            .thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/utenti/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user123"))
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.nome").value("Test"))
                .andExpect(jsonPath("$.cognome").value("User"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.indirizzo").value("Via Test 123"))
                .andExpect(jsonPath("$.ruolo").value("ACQUIRENTE"))
                .andExpect(jsonPath("$.attivo").value(true));
    }
}
