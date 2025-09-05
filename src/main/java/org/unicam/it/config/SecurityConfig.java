package org.unicam.it.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoint pubblici di sistema
                        .requestMatchers("/", "/health", "/api-docs").permitAll()

                        // Endpoint di autenticazione - SEMPRE PUBBLICI
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoint pubblici per prodotti
                        .requestMatchers("/api/prodotti/visualizza").permitAll()
                        .requestMatchers("/api/prodotti/cerca").permitAll()

                        // Endpoint pubblici per eventi
                        .requestMatchers("/api/eventi/disponibili").permitAll()

                        // Endpoint pubblici per pacchetti
                        .requestMatchers("/api/pacchetti/visualizza").permitAll()
                        .requestMatchers("/api/pacchetti/cerca").permitAll()

                        // Endpoint di gestione utenti - PUBBLICI (usano userId come parametro)
                        .requestMatchers("/api/utenti/**").permitAll()

                        // Endpoint di gestione prodotti - PUBBLICI (usano venditorId/curatoreId come parametro)
                        .requestMatchers("/api/prodotti/**").permitAll()

                        // Endpoint di gestione pacchetti - PUBBLICI (usano distributoreId/curatoreId come parametro)
                        .requestMatchers("/api/pacchetti/**").permitAll()

                        // Endpoint di gestione carrello - PUBBLICI (usano userId come parametro)
                        .requestMatchers("/api/carrello/**").permitAll()

                        // Endpoint di gestione eventi - PUBBLICI (usano animatoreId/userId come parametro)
                        .requestMatchers("/api/eventi/**").permitAll()

                        // Endpoint di gestione ordini - PUBBLICI (usano userId come parametro)
                        .requestMatchers("/api/ordini/**").permitAll()

                        // Endpoint di debug/monitoring
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Tutti gli altri endpoint sono pubblici per ora (dato che non abbiamo JWT)
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
