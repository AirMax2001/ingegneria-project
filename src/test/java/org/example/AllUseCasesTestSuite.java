package org.example;

import org.junit.jupiter.api.Test;

/**
 * Suite di test per tutti i casi d'uso implementati
 * Questa classe può essere estesa per eseguire test di suite quando necessario
 *
 * Per eseguire tutti i test: gradle test
 * Per eseguire test specifici: gradle test --tests "*ControllerTest"
 */
public class AllUseCasesTestSuite {

    @Test
    void testSuiteInfo() {
        // Questo test documenta che la suite è configurata
        // I test effettivi sono nei package controller e integration
        System.out.println("IDS Marketplace - Use Cases Test Suite");
        System.out.println("Esegui 'gradle test' per tutti i test dei casi d'uso");
    }
}
