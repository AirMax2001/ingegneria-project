package org.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ordini")
public class Ordine {
    @Id
    private String id;

    @DBRef
    private UtenteLoggato acquirente;

    private List<Carrello.ElementoCarrello> prodotti;
    private BigDecimal totale;
    private StatoOrdine stato = StatoOrdine.CONFERMATO;

    private String indirizzoSpedizione;
    private MetodoPagamento metodoPagamento;

    @CreatedDate
    private LocalDateTime dataCreazione;

    @LastModifiedDate
    private LocalDateTime dataModifica;

    private LocalDateTime dataSpedizione;
    private LocalDateTime dataConsegna;

    // Constructors
    public Ordine() {}

    public Ordine(UtenteLoggato acquirente, List<Carrello.ElementoCarrello> prodotti, BigDecimal totale) {
        this.acquirente = acquirente;
        this.prodotti = prodotti;
        this.totale = totale;
    }

    // Business methods
    public void confermaOrdine() {
        this.stato = StatoOrdine.CONFERMATO;
    }

    public void spedisci() {
        this.stato = StatoOrdine.SPEDITO;
        this.dataSpedizione = LocalDateTime.now();
    }

    public void consegna() {
        this.stato = StatoOrdine.CONSEGNATO;
        this.dataConsegna = LocalDateTime.now();
    }

    public void annulla() {
        this.stato = StatoOrdine.ANNULLATO;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public UtenteLoggato getAcquirente() { return acquirente; }
    public void setAcquirente(UtenteLoggato acquirente) { this.acquirente = acquirente; }

    public List<Carrello.ElementoCarrello> getProdotti() { return prodotti; }
    public void setProdotti(List<Carrello.ElementoCarrello> prodotti) { this.prodotti = prodotti; }

    public BigDecimal getTotale() { return totale; }
    public void setTotale(BigDecimal totale) { this.totale = totale; }

    public StatoOrdine getStato() { return stato; }
    public void setStato(StatoOrdine stato) { this.stato = stato; }

    public String getIndirizzoSpedizione() { return indirizzoSpedizione; }
    public void setIndirizzoSpedizione(String indirizzoSpedizione) { this.indirizzoSpedizione = indirizzoSpedizione; }

    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    public LocalDateTime getDataSpedizione() { return dataSpedizione; }
    public void setDataSpedizione(LocalDateTime dataSpedizione) { this.dataSpedizione = dataSpedizione; }

    public LocalDateTime getDataConsegna() { return dataConsegna; }
    public void setDataConsegna(LocalDateTime dataConsegna) { this.dataConsegna = dataConsegna; }
}
