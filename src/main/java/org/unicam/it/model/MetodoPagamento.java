package org.unicam.it.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "metodi_pagamento")
public class MetodoPagamento {
    @Id
    private String id;

    @DBRef
    private UtenteLoggato proprietario;

    private TipoPagamento tipo;
    private String numeroCartaMascherato;
    private String intestatario;
    private String scadenza;
    private boolean predefinito = false;
    private boolean attivo = true;

    // Constructors
    public MetodoPagamento() {}

    public MetodoPagamento(UtenteLoggato proprietario, TipoPagamento tipo, String numeroCartaMascherato, String intestatario) {
        this.proprietario = proprietario;
        this.tipo = tipo;
        this.numeroCartaMascherato = numeroCartaMascherato;
        this.intestatario = intestatario;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public UtenteLoggato getProprietario() { return proprietario; }
    public void setProprietario(UtenteLoggato proprietario) { this.proprietario = proprietario; }

    public TipoPagamento getTipo() { return tipo; }
    public void setTipo(TipoPagamento tipo) { this.tipo = tipo; }

    public String getNumeroCartaMascherato() { return numeroCartaMascherato; }
    public void setNumeroCartaMascherato(String numeroCartaMascherato) { this.numeroCartaMascherato = numeroCartaMascherato; }

    public String getIntestatario() { return intestatario; }
    public void setIntestatario(String intestatario) { this.intestatario = intestatario; }

    public String getScadenza() { return scadenza; }
    public void setScadenza(String scadenza) { this.scadenza = scadenza; }

    public boolean isPredefinito() { return predefinito; }
    public void setPredefinito(boolean predefinito) { this.predefinito = predefinito; }

    public boolean isAttivo() { return attivo; }
    public void setAttivo(boolean attivo) { this.attivo = attivo; }

    public enum TipoPagamento {
        CARTA_CREDITO("Carta di Credito"),
        CARTA_DEBITO("Carta di Debito"),
        PAYPAL("PayPal"),
        BONIFICO("Bonifico Bancario");

        private final String displayName;

        TipoPagamento(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
