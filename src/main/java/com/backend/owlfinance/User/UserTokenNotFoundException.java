package com.backend.owlfinance.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserTokenNotFoundException extends RuntimeException {

    public UserTokenNotFoundException(String token) {
        super("User token not found: " + token);
    }
}
