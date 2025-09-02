package org.example.controller;

import org.example.model.Evento;
import org.example.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventi")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Use Case: Pubblica Evento
    @PostMapping("/pubblica")
    public ResponseEntity<?> pubblicaEvento(@RequestBody Map<String, Object> request) {
        try {
            String animatoreId = (String) request.get("animatoreId");
            String nome = (String) request.get("nome");
            String descrizione = (String) request.get("descrizione");
            LocalDateTime dataInizio = LocalDateTime.parse((String) request.get("dataInizio"), formatter);
            LocalDateTime dataFine = LocalDateTime.parse((String) request.get("dataFine"), formatter);
            String luogo = (String) request.get("luogo");
            int maxPartecipanti = Integer.parseInt(request.get("maxPartecipanti").toString());

            Evento evento = eventoService.creaEvento(animatoreId, nome, descrizione, dataInizio, dataFine, luogo, maxPartecipanti);

            return ResponseEntity.ok(Map.of(
                "message", "Evento creato con successo",
                "evento", Map.of(
                    "id", evento.getId(),
                    "nome", evento.getNome(),
                    "dataInizio", evento.getDataInizio(),
                    "stato", evento.getStato()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Modifica Evento
    @PutMapping("/{eventoId}")
    public ResponseEntity<?> modificaEvento(@PathVariable String eventoId,
                                          @RequestBody Map<String, Object> request) {
        try {
            String animatoreId = (String) request.get("animatoreId");
            String nome = (String) request.get("nome");
            String descrizione = (String) request.get("descrizione");
            LocalDateTime dataInizio = LocalDateTime.parse((String) request.get("dataInizio"), formatter);
            LocalDateTime dataFine = LocalDateTime.parse((String) request.get("dataFine"), formatter);
            String luogo = (String) request.get("luogo");
            int maxPartecipanti = Integer.parseInt(request.get("maxPartecipanti").toString());

            Evento evento = eventoService.modificaEvento(eventoId, animatoreId, nome, descrizione, dataInizio, dataFine, luogo, maxPartecipanti);

            return ResponseEntity.ok(Map.of(
                "message", "Evento modificato con successo",
                "evento", evento
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Elimina Evento
    @DeleteMapping("/{eventoId}")
    public ResponseEntity<?> eliminaEvento(@PathVariable String eventoId,
                                         @RequestParam String animatoreId) {
        try {
            eventoService.eliminaEvento(eventoId, animatoreId);
            return ResponseEntity.ok(Map.of("message", "Evento eliminato con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Use Case: Prenota Evento
    @PostMapping("/{eventoId}/prenota")
    public ResponseEntity<?> prenotaEvento(@PathVariable String eventoId,
                                         @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            Evento evento = eventoService.prenotaEvento(eventoId, userId);

            return ResponseEntity.ok(Map.of(
                "message", "Evento prenotato con successo",
                "evento", Map.of(
                    "id", evento.getId(),
                    "nome", evento.getNome(),
                    "partecipanti", evento.getPartecipanti().size(),
                    "maxPartecipanti", evento.getMaxPartecipanti()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get available events
    @GetMapping("/disponibili")
    public ResponseEntity<?> getEventiDisponibili() {
        try {
            List<Evento> eventi = eventoService.getEventiDisponibili();
            return ResponseEntity.ok(eventi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get events by animator
    @GetMapping("/animatore/{animatoreId}")
    public ResponseEntity<?> getEventiByAnimatore(@PathVariable String animatoreId) {
        try {
            List<Evento> eventi = eventoService.getEventiByAnimatore(animatoreId);
            return ResponseEntity.ok(eventi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get booked events by user
    @GetMapping("/prenotati/{userId}")
    public ResponseEntity<?> getEventiPrenotati(@PathVariable String userId) {
        try {
            List<Evento> eventi = eventoService.getEventiPrenotatiByUser(userId);
            return ResponseEntity.ok(eventi);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get event by ID
    @GetMapping("/{eventoId}")
    public ResponseEntity<?> getEvento(@PathVariable String eventoId) {
        try {
            Evento evento = eventoService.findById(eventoId);
            return ResponseEntity.ok(evento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
