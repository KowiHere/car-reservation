package com.crp.warsztat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Panel admina i wszystkie operacje modyfikujące dane wymagają zalogowania
 * (HTTP Basic) jako admin. Klient nie musi się logować, żeby złożyć rezerwację
 * lub zobaczyć zajęte terminy w kalendarzu.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.password}") String adminPassword,
            PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles("ADMIN")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/styles.css", "/favicon.ico").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reservations").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/reservations/calendar/**").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
