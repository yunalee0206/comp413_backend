package com.backend.owlfinance.User;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import com.backend.owlfinance.User.UserRepository;

@Configuration
public class LoadData {

    @Bean
    CommandLineRunner initUserDatabase(UserRepository repository) {
        return args -> {
            // Create first user
            UserWithToken user = new UserWithToken();
            user.setId(1L);
            user.setUsername("TestUser");

            // Save all useres
            repository.save(user);

            System.out.println("Database has been loaded with test users!");
        };
    }
}