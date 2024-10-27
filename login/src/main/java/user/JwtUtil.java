package user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "ef9a7e67e942b545539efd18ea22664b931550037148125231de3774ad6b290ba92d3f6a8027943c20947519cf9db1374d54a3774a5d0cfa1cbfab2bdc8114a8"; // Change to a secure key
    private final long EXPIRATION_TIME = 86400000; // 1 day

    // Create Key instance for signing
    private Key getSigningKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String hashedUsername) {
        return Jwts.builder()
                .setSubject(hashedUsername)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Updated signWith
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Updated parser to parserBuilder
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
