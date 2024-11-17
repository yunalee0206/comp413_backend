package com.backend.owlfinance.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserWithToken, Long> {
    Optional<UserWithToken> findByUsername(String username);
    Optional<UserWithToken> findByToken(String token);
    void deleteByUsername(String username);
}
