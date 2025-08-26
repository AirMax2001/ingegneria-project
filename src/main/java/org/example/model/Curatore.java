package org.example.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "curatori")
public class Curatore extends UtenteLoggato {

    public Curatore() {
        super();
    }

    public Curatore(String email, String password, String nome, String cognome) {
        super(email, password, nome, cognome);
    }

    @Override
    public UserRole getRuolo() {
        return UserRole.CURATORE;
    }
}
