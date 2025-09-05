package org.unicam.it.service;

import org.unicam.it.model.*;
import org.unicam.it.repository.OrdineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdineService {

    @Autowired
    private OrdineRepository ordineRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CarrelloService carrelloService;

    @Autowired
    private ProdottoService prodottoService;

    // Use Case: Acquista Prodotto
    public Ordine creaDaCarrello(String userId, String indirizzoSpedizione, MetodoPagamento metodoPagamento) {
        UtenteLoggato acquirente = userService.findById(userId);
        if (acquirente.getRuolo() != UserRole.ACQUIRENTE) {
            throw new IllegalArgumentException("L'utente non è un acquirente");
        }

        Carrello carrello = carrelloService.getCarrelloByUser(userId);

        if (carrello.getProdotti().isEmpty()) {
            throw new IllegalStateException("Carrello vuoto");
        }

        if (!carrello.disponibile()) {
            throw new IllegalStateException("Alcuni prodotti nel carrello non sono più disponibili");
        }

        // Reduce product quantities
        for (Carrello.ElementoCarrello elemento : carrello.getProdotti()) {
            Prodotto prodotto = elemento.getProdotto();
            prodotto.riduciQuantita(elemento.getQuantita());
            prodottoService.modificaProdotto(prodotto.getId(), prodotto.getVenditore().getId(),
                    prodotto.getNome(), prodotto.getDescrizione(),
                    prodotto.getPrezzo(), prodotto.getQuantitaDisponibile());
        }

        Ordine ordine = new Ordine(acquirente, carrello.getProdotti(), carrello.getTotaleOrdine());
        ordine.setIndirizzoSpedizione(indirizzoSpedizione);
        ordine.setMetodoPagamento(metodoPagamento);
        ordine.confermaOrdine();

        // Clear cart after order
        carrelloService.svuotaCarrello(userId);

        return ordineRepository.save(ordine);
    }

    // Use Case: Visualizza Ordini
    public List<Ordine> getOrdiniByAcquirente(String userId) {
        UtenteLoggato acquirente = userService.findById(userId);
        return ordineRepository.findByAcquirenteOrderByDataCreazioneDesc(acquirente);
    }

    public Ordine findById(String ordineId) {
        return ordineRepository.findById(ordineId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato"));
    }

    public List<Ordine> getAllOrdini() {
        return ordineRepository.findAll();
    }

    public Ordine aggiornaStatoOrdine(String ordineId, StatoOrdine nuovoStato) {
        Ordine ordine = findById(ordineId);

        switch (nuovoStato) {
            case SPEDITO -> ordine.spedisci();
            case CONSEGNATO -> ordine.consegna();
            case ANNULLATO -> ordine.annulla();
            default -> ordine.setStato(nuovoStato);
        }

        return ordineRepository.save(ordine);
    }
}
