package org.example.controller;

import org.example.model.Prodotto;
import org.example.service.ProdottoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prodotti")
@CrossOrigin(origins = "*")
public class ProdottoController {

    @Autowired
    private ProdottoService prodottoService;

    // Use Case: Pubblicazione Prodotto
    @PostMapping("/pubblica")
    public ResponseEntity<?> pubblicaProdotto(@RequestBody Map<String, Object> request) {
        try {
            String venditorId = (String) request.get("venditorId");
            String nome = (String) request.get("nome");
            String descrizione = (String) request.get("descrizione");
            BigDecimal prezzo = new BigDecimal(request.get("prezzo").toString());
            int quantita = Integer.parseInt(request.get("quantita").toString());
            String categoria = (String) request.get("categoria");

            Prodotto prodotto = prodottoService.pubblicaProdotto(venditorId, nome, descrizione, prezzo, quantita, categoria);

            return ResponseEntity.ok(Map.of(
                "message", "Prodotto pubblicato e in attesa di approvazione",
                "prodottoId", prodotto.getId(),
                "stato", prodotto.getStato()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Modifica Prodotto
    @PutMapping("/{prodottoId}")
    public ResponseEntity<?> modificaProdotto(@PathVariable String prodottoId,
                                            @RequestBody Map<String, Object> request) {
        try {
            String venditorId = (String) request.get("venditorId");
            String nome = (String) request.get("nome");
            String descrizione = (String) request.get("descrizione");
            BigDecimal prezzo = new BigDecimal(request.get("prezzo").toString());
            int quantita = Integer.parseInt(request.get("quantita").toString());

            Prodotto prodotto = prodottoService.modificaProdotto(prodottoId, venditorId, nome, descrizione, prezzo, quantita);

            return ResponseEntity.ok(Map.of(
                "message", "Prodotto modificato con successo",
                "prodotto", prodotto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Elimina Prodotto
    @DeleteMapping("/{prodottoId}")
    public ResponseEntity<?> eliminaProdotto(@PathVariable String prodottoId,
                                           @RequestParam String venditorId) {
        try {
            prodottoService.eliminaProdotto(prodottoId, venditorId);
            return ResponseEntity.ok(Map.of("message", "Prodotto eliminato con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Visualizza Prodotti
    @GetMapping("/visualizza")
    public ResponseEntity<?> visualizzaProdotti() {
        try {
            List<Prodotto> prodotti = prodottoService.visualizzaProdottiApprovati();
            return ResponseEntity.ok(prodotti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Cerca Prodotto
    @GetMapping("/cerca")
    public ResponseEntity<?> cercaProdotto(@RequestParam String nome) {
        try {
            List<Prodotto> prodotti = prodottoService.cercaProdotto(nome);
            return ResponseEntity.ok(prodotti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Accetta Richiesta Di Pubblicazione Del Prodotto
    @PostMapping("/{prodottoId}/approva")
    public ResponseEntity<?> approvaProdotto(@PathVariable String prodottoId,
                                           @RequestParam String curatoreId) {
        try {
            Prodotto prodotto = prodottoService.approvaProdotto(prodottoId, curatoreId);
            return ResponseEntity.ok(Map.of(
                "message", "Prodotto approvato con successo",
                "prodotto", prodotto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Rifiuta Richiesta Di Pubblicazione Del Prodotto
    @PostMapping("/{prodottoId}/rifiuta")
    public ResponseEntity<?> rifiutaProdotto(@PathVariable String prodottoId,
                                           @RequestBody Map<String, String> request) {
        try {
            String curatoreId = request.get("curatoreId");
            String motivoRifiuto = request.get("motivoRifiuto");

            Prodotto prodotto = prodottoService.rifiutaProdotto(prodottoId, curatoreId, motivoRifiuto);
            return ResponseEntity.ok(Map.of(
                "message", "Prodotto rifiutato",
                "prodotto", prodotto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get products pending approval
    @GetMapping("/pending")
    public ResponseEntity<?> getProdottiInAttesa() {
        try {
            List<Prodotto> prodotti = prodottoService.getProdottiInAttesaApprovazione();
            return ResponseEntity.ok(prodotti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get products by seller
    @GetMapping("/venditore/{venditorId}")
    public ResponseEntity<?> getProdottiByVenditore(@PathVariable String venditorId) {
        try {
            List<Prodotto> prodotti = prodottoService.getProdottiByVenditore(venditorId);
            return ResponseEntity.ok(prodotti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
