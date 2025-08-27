package org.example.model;

public enum StatoProdotto {
    IN_ATTESA_APPROVAZIONE("In Attesa di Approvazione"),
    APPROVATO("Approvato"),
    RIFIUTATO("Rifiutato"),
    SOSPESO("Sospeso");

    private final String displayName;

    StatoProdotto(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
