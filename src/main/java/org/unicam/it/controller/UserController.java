package org.unicam.it.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unicam.it.model.UtenteLoggato;
import org.unicam.it.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/utenti")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Use Case: Modifica Profilo
    @PutMapping("/{userId}/profilo")
    public ResponseEntity<?> modificaProfilo(@PathVariable String userId,
                                           @RequestBody Map<String, String> request) {
        try {
            String nome = request.get("nome");
            String cognome = request.get("cognome");
            String telefono = request.get("telefono");
            String indirizzo = request.get("indirizzo");

            UtenteLoggato utente = userService.modificaProfilo(userId, nome, cognome, telefono, indirizzo);

            return ResponseEntity.ok(Map.of(
                "message", "Profilo modificato con successo",
                "user", Map.of(
                    "id", utente.getId(),
                    "nome", utente.getNome(),
                    "cognome", utente.getCognome(),
                    "telefono", utente.getTelefono() != null ? utente.getTelefono() : "",
                    "indirizzo", utente.getIndirizzo() != null ? utente.getIndirizzo() : ""
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Elimina Profilo
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> eliminaProfilo(@PathVariable String userId) {
        try {
            userService.eliminaProfilo(userId);
            return ResponseEntity.ok(Map.of("message", "Profilo eliminato con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user profile
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfilo(@PathVariable String userId) {
        try {
            UtenteLoggato utente = userService.findById(userId);
            return ResponseEntity.ok(Map.of(
                "id", utente.getId(),
                "email", utente.getEmail(),
                "nome", utente.getNome(),
                "cognome", utente.getCognome(),
                "telefono", utente.getTelefono() != null ? utente.getTelefono() : "",
                "indirizzo", utente.getIndirizzo() != null ? utente.getIndirizzo() : "",
                "ruolo", utente.getRuolo(),
                "attivo", utente.isAttivo()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
