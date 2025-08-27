package org.example.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "venditori")
public class Venditore extends UtenteLoggato {

    public Venditore() {
        super();
    }

    public Venditore(String email, String password, String nome, String cognome) {
        super(email, password, nome, cognome);
    }

    @Override
    public UserRole getRuolo() {
        return UserRole.VENDITORE;
    }
}
