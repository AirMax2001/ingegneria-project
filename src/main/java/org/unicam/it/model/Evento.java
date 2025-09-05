package org.unicam.it.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "eventi")
public class Evento {
    @Id
    private String id;

    private String nome;
    private String descrizione;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String luogo;
    private int maxPartecipanti;
    private boolean gratuito = true;

    @DBRef
    private UtenteLoggato animatore;

    @DBRef
    private List<UtenteLoggato> partecipanti = new ArrayList<>();

    private StatoEvento stato = StatoEvento.PROGRAMMATO;

    @CreatedDate
    private LocalDateTime dataCreazione;

    @LastModifiedDate
    private LocalDateTime dataModifica;

    // Constructors
    public Evento() {}

    public Evento(String nome, String descrizione, LocalDateTime dataInizio, LocalDateTime dataFine, UtenteLoggato animatore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.animatore = animatore;
    }

    // Business methods
    public boolean isPrenotabile() {
        return stato == StatoEvento.PROGRAMMATO &&
               partecipanti.size() < maxPartecipanti &&
               dataInizio.isAfter(LocalDateTime.now());
    }

    public void aggiungiPartecipante(UtenteLoggato user) {
        if (!isPrenotabile()) {
            throw new IllegalStateException("Evento non prenotabile");
        }
        if (!partecipanti.contains(user)) {
            partecipanti.add(user);
        }
    }

    public void rimuoviPartecipante(UtenteLoggato user) {
        partecipanti.remove(user);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime dataInizio) { this.dataInizio = dataInizio; }

    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime dataFine) { this.dataFine = dataFine; }

    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }

    public int getMaxPartecipanti() { return maxPartecipanti; }
    public void setMaxPartecipanti(int maxPartecipanti) { this.maxPartecipanti = maxPartecipanti; }

    public boolean isGratuito() { return gratuito; }
    public void setGratuito(boolean gratuito) { this.gratuito = gratuito; }

    public UtenteLoggato getAnimatore() { return animatore; }
    public void setAnimatore(UtenteLoggato animatore) { this.animatore = animatore; }

    public List<UtenteLoggato> getPartecipanti() { return partecipanti; }
    public void setPartecipanti(List<UtenteLoggato> partecipanti) { this.partecipanti = partecipanti; }

    public StatoEvento getStato() { return stato; }
    public void setStato(StatoEvento stato) { this.stato = stato; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    public enum StatoEvento {
        PROGRAMMATO("Programmato"),
        IN_CORSO("In Corso"),
        COMPLETATO("Completato"),
        ANNULLATO("Annullato");

        private final String displayName;

        StatoEvento(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
