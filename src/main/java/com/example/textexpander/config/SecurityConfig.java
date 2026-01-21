package com.example.textexpander.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import static com.collablynk.accounts.security.config.ClServiceConfigurer.clService;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .with(clService(), cl -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow public pages and static assets
                        .requestMatchers("/login", "/signup", "/validate/**").authenticated()
                        // Allow signup/login API calls
                        .requestMatchers("/api/auth/**").permitAll()
                        // Protect all other APIs
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                                .requestMatchers("/api/user/**").hasRole("REGULAR_USER")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                // No form login or basic auth
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
