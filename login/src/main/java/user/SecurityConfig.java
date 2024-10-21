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
                .csrf().disable()  // Disable CSRF protection for development/testing (not recommended for production)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/users/**").permitAll()  // Allow public access to the users endpoint
                        .anyRequest().authenticated()  // Secure other endpoints
                )
                .httpBasic();  // Enable basic authentication

        return http.build();
    }
}