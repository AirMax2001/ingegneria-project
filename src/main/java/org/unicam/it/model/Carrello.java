package org.unicam.it.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "carrelli")
public class Carrello {
    @Id
    private String id;

    @DBRef
    private UtenteLoggato proprietario;

    private List<ElementoCarrello> prodotti = new ArrayList<>();
    private List<ElementoPacchettoCarrello> pacchetti = new ArrayList<>();

    @CreatedDate
    private LocalDateTime dataCreazione;

    @LastModifiedDate
    private LocalDateTime dataModifica;

    // Constructors
    public Carrello() {}

    public Carrello(UtenteLoggato proprietario) {
        this.proprietario = proprietario;
    }

    // Business Methods from VP diagram
    public BigDecimal getTotaleOrdine() {
        BigDecimal totaleProdotti = prodotti.stream()
                .map(elemento -> elemento.getPrezzo().multiply(BigDecimal.valueOf(elemento.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalePacchetti = pacchetti.stream()
                .map(elemento -> elemento.getPrezzo().multiply(BigDecimal.valueOf(elemento.getQuantita())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totaleProdotti.add(totalePacchetti);
    }

    public int getNumeroProdotti() {
        int numProdotti = prodotti.stream()
                .mapToInt(ElementoCarrello::getQuantita)
                .sum();

        int numPacchetti = pacchetti.stream()
                .mapToInt(ElementoPacchettoCarrello::getQuantita)
                .sum();

        return numProdotti + numPacchetti;
    }

    public void svuotaCarrello() {
        this.prodotti.clear();
        this.pacchetti.clear();
    }

    public boolean disponibile() {
        boolean prodottiDisponibili = prodotti.stream()
                .allMatch(elemento -> elemento.getProdotto().isDisponibile() &&
                         elemento.getProdotto().getQuantitaDisponibile() >= elemento.getQuantita());

        boolean pacchettiDisponibili = pacchetti.stream()
                .allMatch(elemento -> elemento.getPacchetto().isDisponibile() &&
                         elemento.getPacchetto().getQuantitaDisponibile() >= elemento.getQuantita());

        return prodottiDisponibili && pacchettiDisponibili;
    }

    public void aggiungiProdotto(Prodotto prodotto, int quantita) {
        if (!prodotto.isDisponibile()) {
            throw new IllegalStateException("Prodotto non disponibile");
        }

        // Check if product already exists in cart
        ElementoCarrello esistente = prodotti.stream()
                .filter(e -> e.getProdotto().getId().equals(prodotto.getId()))
                .findFirst()
                .orElse(null);

        if (esistente != null) {
            esistente.setQuantita(esistente.getQuantita() + quantita);
        } else {
            prodotti.add(new ElementoCarrello(prodotto, quantita, prodotto.getPrezzo()));
        }
    }

    public void aggiungiPacchetto(PacchettoProdotti pacchetto, int quantita) {
        if (!pacchetto.isDisponibile()) {
            throw new IllegalStateException("Pacchetto non disponibile");
        }

        // Check if package already exists in cart
        ElementoPacchettoCarrello esistente = pacchetti.stream()
                .filter(e -> e.getPacchetto().getId().equals(pacchetto.getId()))
                .findFirst()
                .orElse(null);

        if (esistente != null) {
            esistente.setQuantita(esistente.getQuantita() + quantita);
        } else {
            pacchetti.add(new ElementoPacchettoCarrello(pacchetto, quantita, pacchetto.getPrezzoScontato()));
        }
    }

    public void rimuoviProdotto(String prodottoId) {
        prodotti.removeIf(e -> e.getProdotto().getId().equals(prodottoId));
    }

    public void rimuoviPacchetto(String pacchettoId) {
        pacchetti.removeIf(e -> e.getPacchetto().getId().equals(pacchettoId));
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public UtenteLoggato getProprietario() { return proprietario; }
    public void setProprietario(UtenteLoggato proprietario) { this.proprietario = proprietario; }

    public List<ElementoCarrello> getProdotti() { return prodotti; }
    public void setProdotti(List<ElementoCarrello> prodotti) { this.prodotti = prodotti; }

    public List<ElementoPacchettoCarrello> getPacchetti() { return pacchetti; }
    public void setPacchetti(List<ElementoPacchettoCarrello> pacchetti) { this.pacchetti = pacchetti; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    // Inner class for cart items
    public static class ElementoCarrello {
        @DBRef
        private Prodotto prodotto;
        private int quantita;
        private BigDecimal prezzo; // Price at the time of adding to cart

        public ElementoCarrello() {}

        public ElementoCarrello(Prodotto prodotto, int quantita, BigDecimal prezzo) {
            this.prodotto = prodotto;
            this.quantita = quantita;
            this.prezzo = prezzo;
        }

        public Prodotto getProdotto() { return prodotto; }
        public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }

        public BigDecimal getPrezzo() { return prezzo; }
        public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }
    }

    // Inner class for package items
    public static class ElementoPacchettoCarrello {
        @DBRef
        private PacchettoProdotti pacchetto;
        private int quantita;
        private BigDecimal prezzo; // Price at the time of adding to cart

        public ElementoPacchettoCarrello() {}

        public ElementoPacchettoCarrello(PacchettoProdotti pacchetto, int quantita, BigDecimal prezzo) {
            this.pacchetto = pacchetto;
            this.quantita = quantita;
            this.prezzo = prezzo;
        }

        public PacchettoProdotti getPacchetto() { return pacchetto; }
        public void setPacchetto(PacchettoProdotti pacchetto) { this.pacchetto = pacchetto; }

        public int getQuantita() { return quantita; }
        public void setQuantita(int quantita) { this.quantita = quantita; }

        public BigDecimal getPrezzo() { return prezzo; }
        public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }
    }
}
