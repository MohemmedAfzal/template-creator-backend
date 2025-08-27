package com.example.textexpander.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "mySuperSecureSecretKeyWhichIsVeryLong+123456"; // Use env variable in prod
    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private final long expiration = 1000 * 60 * 60;

//    public String generateToken(String username, String role) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
