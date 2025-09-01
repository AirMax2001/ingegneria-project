package org.example.controller;

import org.example.model.PacchettoProdotti;
import org.example.service.PacchettoProdottiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacchetti")
@CrossOrigin(origins = "*")
public class PacchettoProdottiController {

    @Autowired
    private PacchettoProdottiService pacchettoProdottiService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Use Case: Creazione Pacchetto Prodotti
    @PostMapping("/crea")
    public ResponseEntity<?> creaPacchetto(@RequestBody Map<String, Object> request) {
        try {
            String distributoreId = (String) request.get("distributoreId");
            String nome = (String) request.get("nome");
            String descrizione = (String) request.get("descrizione");

            @SuppressWarnings("unchecked")
            List<String> prodottiIds = (List<String>) request.get("prodottiIds");

            BigDecimal percentualeSconto = request.get("percentualeSconto") != null ?
                new BigDecimal(request.get("percentualeSconto").toString()) : BigDecimal.ZERO;

            LocalDateTime dataScadenza = request.get("dataScadenza") != null ?
                LocalDateTime.parse((String) request.get("dataScadenza"), formatter) : null;

            int quantitaDisponibile = Integer.parseInt(request.get("quantitaDisponibile").toString());

            PacchettoProdotti pacchetto = pacchettoProdottiService.creaPacchetto(
                distributoreId, nome, descrizione, prodottiIds,
                percentualeSconto, dataScadenza, quantitaDisponibile);

            return ResponseEntity.ok(Map.of(
                "message", "Pacchetto prodotti creato con successo e in attesa di approvazione",
                "pacchetto", Map.of(
                    "id", pacchetto.getId(),
                    "nome", pacchetto.getNome(),
                    "prezzoOriginale", pacchetto.getPrezzoTotaleOriginale(),
                    "prezzoScontato", pacchetto.getPrezzoScontato(),
                    "risparmio", pacchetto.getRisparmio(),
                    "stato", pacchetto.getStato(),
                    "numeroProdotti", pacchetto.getProdotti().size()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Modifica pacchetto prodotti
    @PutMapping("/{pacchettoId}")
    public ResponseEntity<?> modificaPacchetto(@PathVariable String pacchettoId,
                                             @RequestBody Map<String, Object> request) {
        try {
            String distributoreId = (String) request.get("distributoreId");
            String nome = (String) request.get("nome");
            String descrizione = (String) request.get("descrizione");

            BigDecimal percentualeSconto = request.get("percentualeSconto") != null ?
                new BigDecimal(request.get("percentualeSconto").toString()) : BigDecimal.ZERO;

            LocalDateTime dataScadenza = request.get("dataScadenza") != null ?
                LocalDateTime.parse((String) request.get("dataScadenza"), formatter) : null;

            int quantitaDisponibile = Integer.parseInt(request.get("quantitaDisponibile").toString());

            PacchettoProdotti pacchetto = pacchettoProdottiService.modificaPacchetto(
                pacchettoId, distributoreId, nome, descrizione,
                percentualeSconto, dataScadenza, quantitaDisponibile);

            return ResponseEntity.ok(Map.of(
                "message", "Pacchetto modificato con successo",
                "pacchetto", pacchetto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Elimina pacchetto prodotti
    @DeleteMapping("/{pacchettoId}")
    public ResponseEntity<?> eliminaPacchetto(@PathVariable String pacchettoId,
                                            @RequestParam String distributoreId) {
        try {
            pacchettoProdottiService.eliminaPacchetto(pacchettoId, distributoreId);
            return ResponseEntity.ok(Map.of("message", "Pacchetto eliminato con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Visualizza pacchetti approvati
    @GetMapping("/visualizza")
    public ResponseEntity<?> visualizzaPacchetti() {
        try {
            List<PacchettoProdotti> pacchetti = pacchettoProdottiService.visualizzaPacchettiApprovati();
            return ResponseEntity.ok(pacchetti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Cerca pacchetti per nome
    @GetMapping("/cerca")
    public ResponseEntity<?> cercaPacchetti(@RequestParam String nome) {
        try {
            List<PacchettoProdotti> pacchetti = pacchettoProdottiService.cercaPacchetti(nome);
            return ResponseEntity.ok(pacchetti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Ottieni pacchetti per distributore
    @GetMapping("/distributore/{distributoreId}")
    public ResponseEntity<?> getPacchettiByDistributore(@PathVariable String distributoreId) {
        try {
            List<PacchettoProdotti> pacchetti = pacchettoProdottiService.getPacchettiByDistributore(distributoreId);
            return ResponseEntity.ok(pacchetti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Ottieni pacchetti in attesa di approvazione (per curatori)
    @GetMapping("/pending")
    public ResponseEntity<?> getPacchettiInAttesa() {
        try {
            List<PacchettoProdotti> pacchetti = pacchettoProdottiService.getPacchettiInAttesaApprovazione();
            return ResponseEntity.ok(pacchetti);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Approva pacchetto (per curatori)
    @PostMapping("/{pacchettoId}/approva")
    public ResponseEntity<?> approvaPacchetto(@PathVariable String pacchettoId,
                                            @RequestParam String curatoreId) {
        try {
            PacchettoProdotti pacchetto = pacchettoProdottiService.approvaPacchetto(pacchettoId, curatoreId);
            return ResponseEntity.ok(Map.of(
                "message", "Pacchetto approvato con successo",
                "pacchetto", pacchetto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Rifiuta pacchetto (per curatori)
    @PostMapping("/{pacchettoId}/rifiuta")
    public ResponseEntity<?> rifiutaPacchetto(@PathVariable String pacchettoId,
                                            @RequestParam String curatoreId) {
        try {
            PacchettoProdotti pacchetto = pacchettoProdottiService.rifiutaPacchetto(pacchettoId, curatoreId);
            return ResponseEntity.ok(Map.of(
                "message", "Pacchetto rifiutato",
                "pacchetto", pacchetto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Ottieni dettagli pacchetto specifico
    @GetMapping("/{pacchettoId}")
    public ResponseEntity<?> getPacchetto(@PathVariable String pacchettoId) {
        try {
            PacchettoProdotti pacchetto = pacchettoProdottiService.findById(pacchettoId);

            Map<String, Object> distributoreInfo = Map.of(
                "id", pacchetto.getDistributore().getId(),
                "nome", pacchetto.getDistributore().getNome(),
                "cognome", pacchetto.getDistributore().getCognome()
            );

            Map<String, Object> response = Map.of(
                "id", pacchetto.getId(),
                "nome", pacchetto.getNome(),
                "descrizione", pacchetto.getDescrizione(),
                "prodotti", pacchetto.getProdotti(),
                "prezzoOriginale", pacchetto.getPrezzoTotaleOriginale(),
                "prezzoScontato", pacchetto.getPrezzoScontato(),
                "percentualeSconto", pacchetto.getPercentualeSconto(),
                "risparmio", pacchetto.getRisparmio(),
                "distributore", distributoreInfo,
                "stato", pacchetto.getStato()
            );

            // Aggiungi altre informazioni in una seconda mappa
            Map<String, Object> additionalInfo = Map.of(
                "dataCreazione", pacchetto.getDataCreazione(),
                "dataScadenza", pacchetto.getDataScadenza(),
                "quantitaDisponibile", pacchetto.getQuantitaDisponibile(),
                "attivo", pacchetto.isAttivo(),
                "disponibile", pacchetto.isDisponibile(),
                "scaduto", pacchetto.isScaduto()
            );

            // Unisci le due mappe
            Map<String, Object> finalResponse = new java.util.HashMap<>(response);
            finalResponse.putAll(additionalInfo);

            return ResponseEntity.ok(finalResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint per disattivare pacchetti scaduti (può essere chiamato da uno scheduler)
    @PostMapping("/cleanup-scaduti")
    public ResponseEntity<?> disattivaPacchettiScaduti() {
        try {
            pacchettoProdottiService.disattivaPacchettiScaduti();
            return ResponseEntity.ok(Map.of("message", "Pacchetti scaduti disattivati"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
