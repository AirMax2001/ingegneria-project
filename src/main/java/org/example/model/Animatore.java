package org.example.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "utenti_loggati")
public class Animatore extends UtenteLoggato {

    public Animatore() {
        super();
    }

    public Animatore(String email, String password, String nome, String cognome) {
        super(email, password, nome, cognome);
    }

    @Override
    public UserRole getRuolo() {
        return UserRole.ANIMATORE;
    }
}
