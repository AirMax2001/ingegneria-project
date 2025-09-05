package org.unicam.it.service;

import org.unicam.it.model.Carrello;
import org.unicam.it.model.PacchettoProdotti;
import org.unicam.it.model.Prodotto;
import org.unicam.it.model.UtenteLoggato;
import org.unicam.it.model.UserRole;
import org.unicam.it.repository.CarrelloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarrelloService {

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProdottoService prodottoService;

    @Autowired
    private PacchettoProdottiService pacchettoProdottiService;

    // Use Case: Aggiungi Al Carrello
    public Carrello aggiungiProdottoAlCarrello(String userId, String prodottoId, int quantita) {
        UtenteLoggato user = userService.findById(userId);
        if (user.getRuolo() != UserRole.ACQUIRENTE) {
            throw new IllegalArgumentException("L'utente non è un acquirente");
        }

        Prodotto prodotto = prodottoService.findById(prodottoId);
        if (!prodotto.isDisponibile()) {
            throw new IllegalStateException("Prodotto non disponibile");
        }

        if (prodotto.getQuantitaDisponibile() < quantita) {
            throw new IllegalArgumentException("Quantità richiesta non disponibile");
        }

        Carrello carrello = carrelloRepository.findByProprietario(user)
                .orElse(new Carrello(user));

        carrello.aggiungiProdotto(prodotto, quantita);

        return carrelloRepository.save(carrello);
    }

    // Use Case: Aggiungi Pacchetto Al Carrello
    public Carrello aggiungiPacchettoAlCarrello(String userId, String pacchettoId, int quantita) {
        UtenteLoggato user = userService.findById(userId);
        if (user.getRuolo() != UserRole.ACQUIRENTE) {
            throw new IllegalArgumentException("L'utente non è un acquirente");
        }

        PacchettoProdotti pacchetto = pacchettoProdottiService.findById(pacchettoId);
        if (!pacchetto.isDisponibile()) {
            throw new IllegalStateException("Pacchetto non disponibile");
        }

        if (pacchetto.getQuantitaDisponibile() < quantita) {
            throw new IllegalArgumentException("Quantità richiesta non disponibile per il pacchetto");
        }

        Carrello carrello = carrelloRepository.findByProprietario(user)
                .orElse(new Carrello(user));

        carrello.aggiungiPacchetto(pacchetto, quantita);

        return carrelloRepository.save(carrello);
    }

    // Use Case: Rimuovi Dal Carrello
    public Carrello rimuoviProdottoDalCarrello(String userId, String prodottoId) {
        UtenteLoggato user = userService.findById(userId);
        Carrello carrello = carrelloRepository.findByProprietario(user)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        carrello.rimuoviProdotto(prodottoId);

        return carrelloRepository.save(carrello);
    }

    // Use Case: Rimuovi Pacchetto Dal Carrello
    public Carrello rimuoviPacchettoDalCarrello(String userId, String pacchettoId) {
        UtenteLoggato user = userService.findById(userId);
        Carrello carrello = carrelloRepository.findByProprietario(user)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        carrello.rimuoviPacchetto(pacchettoId);

        return carrelloRepository.save(carrello);
    }

    // Business method to get user's cart
    public Carrello getCarrelloByUser(String userId) {
        UtenteLoggato user = userService.findById(userId);
        return carrelloRepository.findByProprietario(user)
                .orElse(new Carrello(user));
    }

    // Business method to clear cart
    public void svuotaCarrello(String userId) {
        UtenteLoggato user = userService.findById(userId);
        Carrello carrello = carrelloRepository.findByProprietario(user)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato"));

        carrello.svuotaCarrello();
        carrelloRepository.save(carrello);
    }
}
