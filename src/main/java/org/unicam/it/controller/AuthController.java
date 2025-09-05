package org.unicam.it.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unicam.it.model.UserRole;
import org.unicam.it.model.UtenteLoggato;
import org.unicam.it.service.UserService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    // Use Case: Registrazione (senza ruolo - default Acquirente)
    @PostMapping("/registrazione")
    public ResponseEntity<?> registrazione(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String nome = request.get("nome");
            String cognome = request.get("cognome");

            // Se non viene specificato un ruolo, l'utente diventa Acquirente (default)
            UtenteLoggato utente = userService.registraUtente(email, password, nome, cognome);

            return ResponseEntity.ok(Map.of(
                "message", "Registrazione completata con successo come " + utente.getRuolo().getDisplayName(),
                "userId", utente.getId(),
                "email", utente.getEmail(),
                "ruolo", utente.getRuolo()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Registrazione (con ruolo specificato)
    @PostMapping("/registrazione-con-ruolo")
    public ResponseEntity<?> registrazioneConRuolo(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String nome = request.get("nome");
            String cognome = request.get("cognome");
            UserRole ruolo = UserRole.valueOf(request.get("ruolo"));

            UtenteLoggato utente = userService.registraUtente(email, password, nome, cognome, ruolo);

            return ResponseEntity.ok(Map.of(
                "message", "Registrazione completata con successo come " + ruolo.getDisplayName(),
                "userId", utente.getId(),
                "email", utente.getEmail(),
                "ruolo", utente.getRuolo()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            Optional<UtenteLoggato> utente = userService.login(email, password);

            if (utente.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Login effettuato con successo",
                    "userId", utente.get().getId(),
                    "email", utente.get().getEmail(),
                    "nome", utente.get().getNome(),
                    "cognome", utente.get().getCognome(),
                    "ruolo", utente.get().getRuolo()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Credenziali non valide"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
