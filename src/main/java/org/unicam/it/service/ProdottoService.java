package org.unicam.it.service;

import org.unicam.it.model.Prodotto;
import org.unicam.it.model.StatoProdotto;
import org.unicam.it.model.UtenteLoggato;
import org.unicam.it.model.UserRole;
import org.unicam.it.repository.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdottoService {

    @Autowired
    private ProdottoRepository prodottoRepository;

    @Autowired
    private UserService userService;

    // Use Case: Pubblicazione Prodotto
    public Prodotto pubblicaProdotto(String venditorId, String nome, String descrizione,
                                   BigDecimal prezzo, int quantita, String categoria) {
        UtenteLoggato venditore = userService.findById(venditorId);
        if (venditore.getRuolo() != UserRole.VENDITORE) {
            throw new IllegalArgumentException("L'utente non è un venditore");
        }

        Prodotto prodotto = new Prodotto(nome, descrizione, prezzo, quantita, venditore);
        prodotto.setCategoria(categoria);
        prodotto.setStato(StatoProdotto.IN_ATTESA_APPROVAZIONE);

        return prodottoRepository.save(prodotto);
    }

    // Use Case: Modifica Prodotto
    public Prodotto modificaProdotto(String prodottoId, String venditorId, String nome,
                                   String descrizione, BigDecimal prezzo, int quantita) {
        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        if (!prodotto.getVenditore().getId().equals(venditorId)) {
            throw new IllegalArgumentException("Non autorizzato a modificare questo prodotto");
        }

        prodotto.setNome(nome);
        prodotto.setDescrizione(descrizione);
        prodotto.setPrezzo(prezzo);
        prodotto.setQuantitaDisponibile(quantita);

        return prodottoRepository.save(prodotto);
    }

    // Use Case: Elimina Prodotto
    public void eliminaProdotto(String prodottoId, String venditorId) {
        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        if (!prodotto.getVenditore().getId().equals(venditorId)) {
            throw new IllegalArgumentException("Non autorizzato a eliminare questo prodotto");
        }

        prodottoRepository.delete(prodotto);
    }

    // Use Case: Accetta Richiesta Di Pubblicazione Del Prodotto
    public Prodotto approvaProdotto(String prodottoId, String curatoreId) {
        UtenteLoggato curatore = userService.findById(curatoreId);
        if (curatore.getRuolo() != UserRole.CURATORE) {
            throw new IllegalArgumentException("L'utente non è un curatore");
        }

        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        prodotto.setStato(StatoProdotto.APPROVATO);
        return prodottoRepository.save(prodotto);
    }

    // Use Case: Rifiuta Richiesta Di Pubblicazione Del Prodotto
    public Prodotto rifiutaProdotto(String prodottoId, String curatoreId, String motivoRifiuto) {
        UtenteLoggato curatore = userService.findById(curatoreId);
        if (curatore.getRuolo() != UserRole.CURATORE) {
            throw new IllegalArgumentException("L'utente non è un curatore");
        }

        Prodotto prodotto = prodottoRepository.findById(prodottoId)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));

        prodotto.setStato(StatoProdotto.RIFIUTATO);
        prodotto.setMotivoRifiuto(motivoRifiuto);
        return prodottoRepository.save(prodotto);
    }

    // Use Case: Visualizza Prodotti
    public List<Prodotto> visualizzaProdottiApprovati() {
        return prodottoRepository.findByStatoAndQuantitaDisponibileGreaterThan(
                StatoProdotto.APPROVATO, 0);
    }

    // Use Case: Cerca Prodotto
    public List<Prodotto> cercaProdotto(String nome) {
        return prodottoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .filter(p -> p.getStato() == StatoProdotto.APPROVATO)
                .toList();
    }

    public List<Prodotto> getProdottiInAttesaApprovazione() {
        return prodottoRepository.findByStato(StatoProdotto.IN_ATTESA_APPROVAZIONE);
    }

    public List<Prodotto> getProdottiByVenditore(String venditorId) {
        UtenteLoggato venditore = userService.findById(venditorId);
        return prodottoRepository.findByVenditore(venditore);
    }

    public Prodotto findById(String id) {
        return prodottoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prodotto non trovato"));
    }
}
