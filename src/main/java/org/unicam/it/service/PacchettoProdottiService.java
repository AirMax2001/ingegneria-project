package org.unicam.it.service;

import org.unicam.it.model.*;
import org.unicam.it.repository.PacchettoProdottiRepository;
import org.unicam.it.repository.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacchettoProdottiService {

    @Autowired
    private PacchettoProdottiRepository pacchettoProdottiRepository;

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private UserService userService;

    // Use Case: Creazione Pacchetto Prodotti
    public PacchettoProdotti creaPacchetto(String distributoreId, String nome, String descrizione,
                                           List<String> prodottiIds, BigDecimal percentualeSconto,
                                           LocalDateTime dataScadenza, int quantitaDisponibile) {

        // Verifica che l'utente sia un distributore
        UtenteLoggato utente = userService.findById(distributoreId);
        if (utente.getRuolo() != UserRole.DISTRIBUTORE) {
            throw new RuntimeException("Solo i distributori possono creare pacchetti prodotti");
        }

        Distributore distributore = (Distributore) utente;

        // Verifica che ci siano almeno 2 prodotti nel pacchetto
        if (prodottiIds.size() < 2) {
            throw new RuntimeException("Un pacchetto deve contenere almeno 2 prodotti");
        }

        // Recupera i prodotti
        List<Prodotto> prodotti = prodottiIds.stream()
            .map(id -> prodottoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato: " + id)))
            .collect(Collectors.toList());

        // Verifica che tutti i prodotti siano approvati
        boolean tuttiApprovati = prodotti.stream()
            .allMatch(p -> p.getStato() == StatoProdotto.APPROVATO);

        if (!tuttiApprovati) {
            throw new RuntimeException("Tutti i prodotti nel pacchetto devono essere approvati");
        }

        // Crea il pacchetto
        PacchettoProdotti pacchetto = new PacchettoProdotti(nome, descrizione, distributore);
        pacchetto.setProdotti(prodotti);
        pacchetto.setDataScadenza(dataScadenza);
        pacchetto.setQuantitaDisponibile(quantitaDisponibile);

        // Applica lo sconto
        if (percentualeSconto != null && percentualeSconto.compareTo(BigDecimal.ZERO) > 0) {
            pacchetto.applicaSconto(percentualeSconto);
        } else {
            pacchetto.setPrezzoScontato(pacchetto.getPrezzoTotaleOriginale());
        }

        return pacchettoProdottiRepository.save(pacchetto);
    }

    // Modifica pacchetto esistente
    public PacchettoProdotti modificaPacchetto(String pacchetto, String distributoreId,
                                             String nome, String descrizione,
                                             BigDecimal percentualeSconto,
                                             LocalDateTime dataScadenza,
                                             int quantitaDisponibile) {

        PacchettoProdotti pacchettoProdotti = findById(pacchetto);

        // Verifica che il distributore sia il proprietario
        if (!pacchettoProdotti.getDistributore().getId().equals(distributoreId)) {
            throw new RuntimeException("Non hai i permessi per modificare questo pacchetto");
        }

        // Non permettere modifiche se il pacchetto è già approvato
        if (pacchettoProdotti.getStato() == StatoProdotto.APPROVATO) {
            throw new RuntimeException("Non è possibile modificare un pacchetto già approvato");
        }

        pacchettoProdotti.setNome(nome);
        pacchettoProdotti.setDescrizione(descrizione);
        pacchettoProdotti.setDataScadenza(dataScadenza);
        pacchettoProdotti.setQuantitaDisponibile(quantitaDisponibile);

        if (percentualeSconto != null && percentualeSconto.compareTo(BigDecimal.ZERO) > 0) {
            pacchettoProdotti.applicaSconto(percentualeSconto);
        }

        return pacchettoProdottiRepository.save(pacchettoProdotti);
    }

    // Elimina pacchetto
    public void eliminaPacchetto(String pacchetto, String distributoreId) {
        PacchettoProdotti pacchettoProdotti = findById(pacchetto);

        if (!pacchettoProdotti.getDistributore().getId().equals(distributoreId)) {
            throw new RuntimeException("Non hai i permessi per eliminare questo pacchetto");
        }

        pacchettoProdottiRepository.delete(pacchettoProdotti);
    }

    // Approva pacchetto (per curatori)
    public PacchettoProdotti approvaPacchetto(String pacchetto, String curatoreId) {
        UtenteLoggato curatore = userService.findById(curatoreId);
        if (curatore.getRuolo() != UserRole.CURATORE) {
            throw new RuntimeException("Solo i curatori possono approvare pacchetti");
        }

        PacchettoProdotti pacchettoProdotti = findById(pacchetto);
        pacchettoProdotti.setStato(StatoProdotto.APPROVATO);

        return pacchettoProdottiRepository.save(pacchettoProdotti);
    }

    // Rifiuta pacchetto (per curatori)
    public PacchettoProdotti rifiutaPacchetto(String pacchetto, String curatoreId) {
        UtenteLoggato curatore = userService.findById(curatoreId);
        if (curatore.getRuolo() != UserRole.CURATORE) {
            throw new RuntimeException("Solo i curatori possono rifiutare pacchetti");
        }

        PacchettoProdotti pacchettoProdotti = findById(pacchetto);
        pacchettoProdotti.setStato(StatoProdotto.RIFIUTATO);

        return pacchettoProdottiRepository.save(pacchettoProdotti);
    }

    // Visualizza pacchetti approvati
    public List<PacchettoProdotti> visualizzaPacchettiApprovati() {
        return pacchettoProdottiRepository.findByStatoAndAttivoTrueAndQuantitaDisponibileGreaterThan(
            StatoProdotto.APPROVATO, 0);
    }

    // Ottieni pacchetti per distributore
    public List<PacchettoProdotti> getPacchettiByDistributore(String distributoreId) {
        UtenteLoggato distributore = userService.findById(distributoreId);
        return pacchettoProdottiRepository.findByDistributore((Distributore) distributore);
    }

    // Ottieni pacchetti in attesa di approvazione
    public List<PacchettoProdotti> getPacchettiInAttesaApprovazione() {
        return pacchettoProdottiRepository.findByStato(StatoProdotto.IN_ATTESA_APPROVAZIONE);
    }

    // Cerca pacchetti per nome
    public List<PacchettoProdotti> cercaPacchetti(String nome) {
        return pacchettoProdottiRepository.findByNomeContainingIgnoreCase(nome)
            .stream()
            .filter(p -> p.getStato() == StatoProdotto.APPROVATO && p.isDisponibile())
            .collect(Collectors.toList());
    }

    // Trova pacchetto per ID
    public PacchettoProdotti findById(String id) {
        Optional<PacchettoProdotti> pacchetto = pacchettoProdottiRepository.findById(id);
        if (pacchetto.isEmpty()) {
            throw new RuntimeException("Pacchetto non trovato");
        }
        return pacchetto.get();
    }

    // Disattiva pacchetto scaduto
    public void disattivaPacchettiScaduti() {
        List<PacchettoProdotti> tuttiPacchetti = pacchettoProdottiRepository.findByAttivoTrueOrderByDataCreazioneDesc();

        for (PacchettoProdotti pacchetto : tuttiPacchetti) {
            if (pacchetto.isScaduto()) {
                pacchetto.setAttivo(false);
                pacchettoProdottiRepository.save(pacchetto);
            }
        }
    }

    // Decrementa quantità disponibile dopo acquisto
    public void decrementaQuantita(String pacchetto, int quantita) {
        PacchettoProdotti pacchettoProdotti = findById(pacchetto);

        if (pacchettoProdotti.getQuantitaDisponibile() < quantita) {
            throw new RuntimeException("Quantità non disponibile");
        }

        pacchettoProdotti.setQuantitaDisponibile(
            pacchettoProdotti.getQuantitaDisponibile() - quantita);

        pacchettoProdottiRepository.save(pacchettoProdotti);
    }
}
