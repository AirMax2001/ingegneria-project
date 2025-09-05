package org.unicam.it.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unicam.it.model.Carrello;
import org.unicam.it.service.CarrelloService;

import java.util.Map;

@RestController
@RequestMapping("/api/carrello")
@CrossOrigin(origins = "*")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;

    // Use Case: Aggiungi Al Carrello
    @PostMapping("/aggiungi")
    public ResponseEntity<?> aggiungiProdotto(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String prodottoId = (String) request.get("prodottoId");
            int quantita = Integer.parseInt(request.get("quantita").toString());

            Carrello carrello = carrelloService.aggiungiProdottoAlCarrello(userId, prodottoId, quantita);

            return ResponseEntity.ok(Map.of(
                "message", "Prodotto aggiunto al carrello",
                "carrello", Map.of(
                    "id", carrello.getId(),
                    "numeroProdotti", carrello.getNumeroProdotti(),
                    "totale", carrello.getTotaleOrdine()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Rimuovi Dal Carrello
    @DeleteMapping("/rimuovi")
    public ResponseEntity<?> rimuoviProdotto(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String prodottoId = request.get("prodottoId");

            Carrello carrello = carrelloService.rimuoviProdottoDalCarrello(userId, prodottoId);

            return ResponseEntity.ok(Map.of(
                "message", "Prodotto rimosso dal carrello",
                "carrello", Map.of(
                    "id", carrello.getId(),
                    "numeroProdotti", carrello.getNumeroProdotti(),
                    "totale", carrello.getTotaleOrdine()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Aggiungi Pacchetto Al Carrello
    @PostMapping("/aggiungi-pacchetto")
    public ResponseEntity<?> aggiungiPacchetto(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String pacchettoId = (String) request.get("pacchettoId");
            int quantita = Integer.parseInt(request.get("quantita").toString());

            Carrello carrello = carrelloService.aggiungiPacchettoAlCarrello(userId, pacchettoId, quantita);

            return ResponseEntity.ok(Map.of(
                "message", "Pacchetto aggiunto al carrello",
                "carrello", Map.of(
                    "id", carrello.getId(),
                    "numeroProdotti", carrello.getNumeroProdotti(),
                    "totale", carrello.getTotaleOrdine()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Rimuovi Pacchetto Dal Carrello
    @DeleteMapping("/rimuovi-pacchetto")
    public ResponseEntity<?> rimuoviPacchetto(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String pacchettoId = request.get("pacchettoId");

            Carrello carrello = carrelloService.rimuoviPacchettoDalCarrello(userId, pacchettoId);

            return ResponseEntity.ok(Map.of(
                "message", "Pacchetto rimosso dal carrello",
                "carrello", Map.of(
                    "id", carrello.getId(),
                    "numeroProdotti", carrello.getNumeroProdotti(),
                    "totale", carrello.getTotaleOrdine()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCarrello(@PathVariable String userId) {
        try {
            Carrello carrello = carrelloService.getCarrelloByUser(userId);
            return ResponseEntity.ok(carrello);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Clear cart
    @PostMapping("/{userId}/svuota")
    public ResponseEntity<?> svuotaCarrello(@PathVariable String userId) {
        try {
            carrelloService.svuotaCarrello(userId);
            return ResponseEntity.ok(Map.of("message", "Carrello svuotato con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
