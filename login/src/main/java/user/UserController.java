package user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import user.User;
import user.UserWithToken;
import user.JwtUtil;
import user.UserResponse;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null; // Or throw an exception if needed
    }


    // GET all users with their JWT tokens
    @GetMapping("/users")
    public List<UserWithToken> getAllUsersWithTokens() {
        // Get all users and generate tokens for each
        return userRepository.findAll().stream()
                .map(user -> new UserWithToken(user.getId(), user.getUsername(), jwtUtil.generateToken(user.getUsername())))
                .collect(Collectors.toList());
    }


    // GET single user by username
    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(u -> {
            // Create a UserResponse object with user details and the token
            UserResponse userResponse = new UserResponse(u.getId(), u.getUsername(), jwtUtil.generateToken(username));
            return ResponseEntity.ok(userResponse);
        }).orElseThrow(() -> new UserNotFoundException(username));
    }

    // POST new user with JWT generation
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody User newUser) {
        User savedUser = userRepository.save(newUser);

        // Generate JWT token for the new user
        String token = jwtUtil.generateToken(savedUser.getUsername());

        // Create response object
        UserResponse response = new UserResponse(savedUser.getId(), savedUser.getUsername(), token);

        // Return the response with user details and JWT token
        return ResponseEntity.ok(response);
    }


    // DELETE user by username
    @Transactional
    @DeleteMapping("/users/{username}")
    public void deleteUser(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            userRepository.deleteByUsername(username);
        } else {
            throw new UserNotFoundException("User not found with username: " + username);
        }
    }

}
