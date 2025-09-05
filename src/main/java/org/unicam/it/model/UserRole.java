package org.unicam.it.model;

public enum UserRole {
    UTENTE_GENERICO("Utente Generico"),
    UTENTE_LOGGATO("Utente Loggato"),
    ACQUIRENTE("Acquirente"),
    VENDITORE("Venditore"),
    DISTRIBUTORE("Distributore"),
    CURATORE("Curatore"),
    ANIMATORE("Animatore");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
