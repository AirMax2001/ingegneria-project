package org.unicam.it.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unicam.it.model.MetodoPagamento;
import org.unicam.it.model.Ordine;
import org.unicam.it.model.StatoOrdine;
import org.unicam.it.service.OrdineService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordini")
@CrossOrigin(origins = "*")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;

    // Use Case: Acquista Prodotto
    @PostMapping("/crea")
    public ResponseEntity<?> creaOrdine(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String indirizzoSpedizione = (String) request.get("indirizzoSpedizione");

            // Create a simple payment method for this demo
            MetodoPagamento metodoPagamento = new MetodoPagamento();
            metodoPagamento.setTipo(MetodoPagamento.TipoPagamento.CARTA_CREDITO);

            Ordine ordine = ordineService.creaDaCarrello(userId, indirizzoSpedizione, metodoPagamento);

            return ResponseEntity.ok(Map.of(
                "message", "Ordine creato con successo",
                "ordine", Map.of(
                    "id", ordine.getId(),
                    "totale", ordine.getTotale(),
                    "stato", ordine.getStato(),
                    "dataCreazione", ordine.getDataCreazione()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get orders by user
    @GetMapping("/utente/{userId}")
    public ResponseEntity<?> getOrdiniByUtente(@PathVariable String userId) {
        try {
            List<Ordine> ordini = ordineService.getOrdiniByAcquirente(userId);
            return ResponseEntity.ok(ordini);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get order by ID
    @GetMapping("/{ordineId}")
    public ResponseEntity<?> getOrdine(@PathVariable String ordineId) {
        try {
            Ordine ordine = ordineService.findById(ordineId);
            return ResponseEntity.ok(ordine);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Update order status
    @PutMapping("/{ordineId}/stato")
    public ResponseEntity<?> aggiornaStato(@PathVariable String ordineId,
                                         @RequestBody Map<String, String> request) {
        try {
            StatoOrdine nuovoStato = StatoOrdine.valueOf(request.get("stato"));
            Ordine ordine = ordineService.aggiornaStatoOrdine(ordineId, nuovoStato);

            return ResponseEntity.ok(Map.of(
                "message", "Stato ordine aggiornato",
                "ordine", ordine
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all orders (for admin/curator)
    @GetMapping("/tutti")
    public ResponseEntity<?> getAllOrdini() {
        try {
            List<Ordine> ordini = ordineService.getAllOrdini();
            return ResponseEntity.ok(ordini);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
