package user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import user.User;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // GET all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET single user by username
    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    // POST new user
    @PostMapping("/users")
    public User createUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
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
