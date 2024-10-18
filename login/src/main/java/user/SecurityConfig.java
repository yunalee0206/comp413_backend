package user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for testing purposes
                .authorizeHttpRequests()
                .requestMatchers("/users").permitAll() // Allow all requests to /users
                .anyRequest().authenticated(); // All other requests require authentication

        return http.build();
    }
}