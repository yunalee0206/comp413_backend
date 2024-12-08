package com.backend.owlfinance.User;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
//import login.JwtUtil;
import java.util.stream.Collectors;
import com.backend.owlfinance.database.bigtable.BigTableManager;
import com.backend.owlfinance.database.obj.User;

@RestController
public class UserController {
//    private BigTableManager bigTableManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


//    private String getTokenFromRequest(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7); // Remove "Bearer " prefix
//        }
//        return null; // Or throw an exception if needed
//    }


    // GET all users with their JWT tokens
    // No database methods
    @GetMapping("/users")
    public List<UserWithToken> getAllUsersWithTokens() {
        // Get all users and generate tokens for each
        return userRepository.findAll().stream()
                .map(user -> new UserWithToken(user.getId(), user.getUsername(), user.getToken()))
                .collect(Collectors.toList());
    }


    // GET single user by username
    @GetMapping("/users/user/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
//        Optional<UserWithToken> user = userRepository.findByUsername(username);
//
//        return user.map(u -> {
//            // Create a UserResponse object with user details and the token
//            UserResponse userResponse = new UserResponse(u.getId(), u.getUsername(), u.getToken());
//            return ResponseEntity.ok(userResponse);
//        }).orElseThrow(() -> new UsernameNotFoundException(username));
        User user = BigTableManager.getUser(username);
        UserResponse userResponse = new UserResponse(user.username(), user.token());
        return ResponseEntity.ok(userResponse);
    }


    // no database methods
//    @GetMapping("/users/user/{token}")
//    public ResponseEntity<UserResponse> getUserByToken(@PathVariable String token) {
//        Optional<UserWithToken> user = userRepository.findByToken(token);
//
//        return user.map(u -> {
//            // Create a UserResponse object with user details and the token
//            UserResponse userResponse = new UserResponse(u.getUsername(), u.getToken());
//            return ResponseEntity.ok(userResponse);
//        }).orElseThrow(() -> new UserTokenNotFoundException(token));
//
//    }

    @GetMapping("/users/token/{username}")
    public ResponseEntity<String> getTokenWithUsername(@PathVariable String username) {
        String token = BigTableManager.getUserToken(username);
        return ResponseEntity.ok(token);
    }

    // POST new user with JWT generation
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserWithToken newUser) {
        //UserWithToken savedUser = userRepository.save(newUser);

        // Generate JWT token for the new user
        String username = newUser.getUsername();
        String token = jwtUtil.generateToken(username);
        //savedUser.setToken(token);
        User user = new User(username, "", token);

        BigTableManager.createUser(user);

        // Create response object
        UserResponse response = new UserResponse(username, token);

        // Return the response with user details and JWT token
        return ResponseEntity.ok(response);
    }


    // DELETE user by username
    @Transactional
    @DeleteMapping("/users/{username}")
    public void deleteUser(@PathVariable String username) {
        User user = BigTableManager.getUser(username);
        if (user != null) {
            BigTableManager.deleteUser(username);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
