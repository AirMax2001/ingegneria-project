package org.example.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "utenti_loggati")
public class Acquirente extends UtenteLoggato {

    public Acquirente() {
        super();
    }

    public Acquirente(String email, String password, String nome, String cognome) {
        super(email, password, nome, cognome);
    }

    @Override
    public UserRole getRuolo() {
        return UserRole.ACQUIRENTE;
    }
}
