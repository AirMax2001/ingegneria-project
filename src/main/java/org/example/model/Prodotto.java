package org.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "prodotti")
public class Prodotto {
    @Id
    private String id;

    private String nome;
    private String descrizione;
    private BigDecimal prezzo;
    private int quantitaDisponibile;
    private String categoria;
    private String immagineUrl;

    @DBRef
    private UtenteLoggato venditore;

    private StatoProdotto stato = StatoProdotto.IN_ATTESA_APPROVAZIONE;

    @CreatedDate
    private LocalDateTime dataCreazione;

    @LastModifiedDate
    private LocalDateTime dataModifica;

    private String motivoRifiuto;

    // Constructors
    public Prodotto() {}

    public Prodotto(String nome, String descrizione, BigDecimal prezzo, int quantitaDisponibile, UtenteLoggato venditore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.venditore = venditore;
    }

    // Business Methods
    public boolean isDisponibile() {
        return stato == StatoProdotto.APPROVATO && quantitaDisponibile > 0;
    }

    public void riduciQuantita(int quantita) {
        if (quantitaDisponibile >= quantita) {
            this.quantitaDisponibile -= quantita;
        } else {
            throw new IllegalArgumentException("Quantità non disponibile");
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public int getQuantitaDisponibile() { return quantitaDisponibile; }
    public void setQuantitaDisponibile(int quantitaDisponibile) { this.quantitaDisponibile = quantitaDisponibile; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String immagineUrl) { this.immagineUrl = immagineUrl; }

    public UtenteLoggato getVenditore() { return venditore; }
    public void setVenditore(UtenteLoggato venditore) { this.venditore = venditore; }

    public StatoProdotto getStato() { return stato; }
    public void setStato(StatoProdotto stato) { this.stato = stato; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getDataModifica() { return dataModifica; }
    public void setDataModifica(LocalDateTime dataModifica) { this.dataModifica = dataModifica; }

    public String getMotivoRifiuto() { return motivoRifiuto; }
    public void setMotivoRifiuto(String motivoRifiuto) { this.motivoRifiuto = motivoRifiuto; }
}
