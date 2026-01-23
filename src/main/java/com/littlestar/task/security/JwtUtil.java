/*package com.littlestar.task.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JwtUtil {
    private static final String SECRET_KEY = "your-secret-key";

    public static boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (SignatureException | IllegalArgumentException e) {
            return false;
        }
    }
}*/