package org.example.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "distributori")
public class Distributore extends UtenteLoggato {

    public Distributore() {
        super();
    }

    public Distributore(String email, String password, String nome, String cognome) {
        super(email, password, nome, cognome);
    }

    @Override
    public UserRole getRuolo() {
        return UserRole.DISTRIBUTORE;
    }
}
