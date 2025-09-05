package org.unicam.it.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "pacchetti_prodotti")
public class PacchettoProdotti {

    @Id
    private String id;

    private String nome;
    private String descrizione;

    @DBRef
    private List<Prodotto> prodotti;

    private BigDecimal prezzoTotaleOriginale;
    private BigDecimal prezzoScontato;
    private BigDecimal percentualeSconto;

    @DBRef
    private Distributore distributore;

    private StatoProdotto stato;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataScadenza;

    private boolean attivo;
    private int quantitaDisponibile;

    // Constructors
    public PacchettoProdotti() {
        this.prodotti = new ArrayList<>();
        this.dataCreazione = LocalDateTime.now();
        this.stato = StatoProdotto.IN_ATTESA_APPROVAZIONE;
        this.attivo = true;
    }

    public PacchettoProdotti(String nome, String descrizione, Distributore distributore) {
        this();
        this.nome = nome;
        this.descrizione = descrizione;
        this.distributore = distributore;
    }

    // Metodi di business
    public void calcolaPrezzoTotaleOriginale() {
        this.prezzoTotaleOriginale = prodotti.stream()
            .map(Prodotto::getPrezzo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void applicaSconto(BigDecimal percentuale) {
        this.percentualeSconto = percentuale;
        BigDecimal moltiplicatore = BigDecimal.valueOf(100).subtract(percentuale)
            .divide(BigDecimal.valueOf(100));
        this.prezzoScontato = this.prezzoTotaleOriginale.multiply(moltiplicatore);
    }

    public BigDecimal getRisparmio() {
        if (prezzoTotaleOriginale != null && prezzoScontato != null) {
            return prezzoTotaleOriginale.subtract(prezzoScontato);
        }
        return BigDecimal.ZERO;
    }

    public boolean isScaduto() {
        return dataScadenza != null && LocalDateTime.now().isAfter(dataScadenza);
    }

    public boolean isDisponibile() {
        return attivo && !isScaduto() && quantitaDisponibile > 0
            && stato == StatoProdotto.APPROVATO;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Prodotto> getProdotti() {
        return prodotti;
    }

    public void setProdotti(List<Prodotto> prodotti) {
        this.prodotti = prodotti;
        calcolaPrezzoTotaleOriginale();
    }

    public void aggiungiProdotto(Prodotto prodotto) {
        this.prodotti.add(prodotto);
        calcolaPrezzoTotaleOriginale();
    }

    public BigDecimal getPrezzoTotaleOriginale() {
        return prezzoTotaleOriginale;
    }

    public void setPrezzoTotaleOriginale(BigDecimal prezzoTotaleOriginale) {
        this.prezzoTotaleOriginale = prezzoTotaleOriginale;
    }

    public BigDecimal getPrezzoScontato() {
        return prezzoScontato;
    }

    public void setPrezzoScontato(BigDecimal prezzoScontato) {
        this.prezzoScontato = prezzoScontato;
    }

    public BigDecimal getPercentualeSconto() {
        return percentualeSconto;
    }

    public void setPercentualeSconto(BigDecimal percentualeSconto) {
        this.percentualeSconto = percentualeSconto;
    }

    public Distributore getDistributore() {
        return distributore;
    }

    public void setDistributore(Distributore distributore) {
        this.distributore = distributore;
    }

    public StatoProdotto getStato() {
        return stato;
    }

    public void setStato(StatoProdotto stato) {
        this.stato = stato;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public LocalDateTime getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDateTime dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }
}
