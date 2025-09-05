package org.unicam.it.model;

public enum StatoOrdine {
    CONFERMATO("Confermato"),
    IN_ELABORAZIONE("In Elaborazione"),
    SPEDITO("Spedito"),
    CONSEGNATO("Consegnato"),
    ANNULLATO("Annullato");

    private final String displayName;

    StatoOrdine(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
