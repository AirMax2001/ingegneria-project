package org.unicam.it.service;

import org.unicam.it.factory.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unicam.it.model.*;
import org.unicam.it.repository.*;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private AcquirenteRepository acquirenteRepository;

    @Autowired
    private VenditoreRepository venditoreRepository;

    @Autowired
    private CuratoreRepository curatoreRepository;

    @Autowired
    private AnimatoreRepository animatoreRepository;

    @Autowired
    private DistributoreRepository distributoreRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Use Case: Registrazione (senza ruolo specificato - default Acquirente)
    public UtenteLoggato registraUtente(String email, String password, String nome, String cognome) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email già registrata");
        }

        String encodedPassword = passwordEncoder.encode(password);
        UtenteLoggato utente = userFactory.creaUtenteDefault(email, encodedPassword, nome, cognome);

        return saveUser(utente);
    }

    // Use Case: Registrazione (con ruolo specificato)
    public UtenteLoggato registraUtente(String email, String password, String nome, String cognome, UserRole ruolo) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email già registrata");
        }

        String encodedPassword = passwordEncoder.encode(password);
        UtenteLoggato utente = userFactory.creaUtente(email, encodedPassword, nome, cognome, ruolo);

        return saveUser(utente);
    }

    // Use Case: Login
    public Optional<UtenteLoggato> login(String email, String password) {
        // Cerca l'utente in tutte le collezioni
        UtenteLoggato utente = findByEmail(email);

        if (utente != null && passwordEncoder.matches(password, utente.getPassword())) {
            return Optional.of(utente);
        }
        return Optional.empty();
    }

    // Use Case: Modifica Profilo
    public UtenteLoggato modificaProfilo(String userId, String nome, String cognome, String telefono, String indirizzo) {
        UtenteLoggato utente = findById(userId);
        if (utente == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setTelefono(telefono);
        utente.setIndirizzo(indirizzo);

        return saveUser(utente);
    }

    // Use Case: Elimina Profilo
    public void eliminaProfilo(String userId) {
        UtenteLoggato utente = findById(userId);
        if (utente == null) {
            throw new IllegalArgumentException("Utente non trovato");
        }

        utente.setAttivo(false);
        saveUser(utente);
    }

    // Metodi di utilità
    public UtenteLoggato findById(String id) {
        // Cerca in tutte le collezioni
        return acquirenteRepository.findById(id).map(u -> (UtenteLoggato) u)
                .or(() -> venditoreRepository.findById(id).map(u -> (UtenteLoggato) u))
                .or(() -> curatoreRepository.findById(id).map(u -> (UtenteLoggato) u))
                .or(() -> animatoreRepository.findById(id).map(u -> (UtenteLoggato) u))
                .or(() -> distributoreRepository.findById(id).map(u -> (UtenteLoggato) u))
                .orElse(null);
    }

    public UtenteLoggato findByEmail(String email) {
        // Cerca in tutte le collezioni
        return acquirenteRepository.findByEmail(email).map(u -> (UtenteLoggato) u)
                .or(() -> venditoreRepository.findByEmail(email).map(u -> (UtenteLoggato) u))
                .or(() -> curatoreRepository.findByEmail(email).map(u -> (UtenteLoggato) u))
                .or(() -> animatoreRepository.findByEmail(email).map(u -> (UtenteLoggato) u))
                .or(() -> distributoreRepository.findByEmail(email).map(u -> (UtenteLoggato) u))
                .orElse(null);
    }

    private boolean existsByEmail(String email) {
        return acquirenteRepository.existsByEmail(email) ||
               venditoreRepository.existsByEmail(email) ||
               curatoreRepository.existsByEmail(email) ||
               animatoreRepository.existsByEmail(email) ||
               distributoreRepository.existsByEmail(email);
    }

    private UtenteLoggato saveUser(UtenteLoggato utente) {
        return switch (utente.getRuolo()) {
            case ACQUIRENTE -> acquirenteRepository.save((Acquirente) utente);
            case VENDITORE -> venditoreRepository.save((Venditore) utente);
            case CURATORE -> curatoreRepository.save((Curatore) utente);
            case ANIMATORE -> animatoreRepository.save((Animatore) utente);
            case DISTRIBUTORE -> distributoreRepository.save((Distributore) utente);
            default -> throw new IllegalArgumentException("Ruolo utente non supportato: " + utente.getRuolo());
        };
    }
}
