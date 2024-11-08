package user;

public class UserResponse {
    private Long id;
    private String username;
    private String token;

    // Constructor
    public UserResponse(Long id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
