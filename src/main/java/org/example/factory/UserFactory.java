package org.example.factory;

import org.example.model.*;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    /**
     * Crea un utente del tipo specificato dal ruolo
     * @param email Email dell'utente
     * @param password Password (già codificata)
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param ruolo Ruolo che determina il tipo di utente da creare
     * @return Istanza concreta di UtenteLoggato
     */
    public UtenteLoggato creaUtente(String email, String password, String nome, String cognome, UserRole ruolo) {
        return switch (ruolo) {
            case ACQUIRENTE -> new Acquirente(email, password, nome, cognome);
            case VENDITORE -> new Venditore(email, password, nome, cognome);
            case CURATORE -> new Curatore(email, password, nome, cognome);
            case ANIMATORE -> new Animatore(email, password, nome, cognome);
            case DISTRIBUTORE -> new Distributore(email, password, nome, cognome);
            case UTENTE_GENERICO, UTENTE_LOGGATO -> new Acquirente(email, password, nome, cognome);
        };
    }

    /**
     * Crea un utente con ruolo di default (Acquirente)
     * @param email Email dell'utente
     * @param password Password (già codificata)
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @return Istanza di Acquirente (ruolo di default)
     */
    public UtenteLoggato creaUtenteDefault(String email, String password, String nome, String cognome) {
        return creaUtente(email, password, nome, cognome, UserRole.ACQUIRENTE);
    }
}
