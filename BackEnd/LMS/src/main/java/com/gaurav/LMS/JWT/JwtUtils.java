package com.gaurav.LMS.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private final SecretKey secretKey;
    public JwtUtils(@Value("${jwt_secret}") String jwtSecretKey) {
        secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    //generate jwt token
    public String generateJwtToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("authorities",userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        long tokenExpirationTime = 1000 * 60 * 60;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    //extract the claims
    private Claims extractClaims(String jwtToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        }
        catch (JwtException exception) {
            throw new RuntimeException("Invalid JWT token" + exception);
        }
    }
    //extract username
    public String extractUserName(String jwtToken) {
        return extractClaims(jwtToken).getSubject();
    }
    //Is token expired ?
    public boolean isTokenExpired(String jwtToken) {
        return extractClaims(jwtToken).getExpiration().before(new Date());
    }
    //Validate jwtToken
    public boolean validateToken(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUserName(token)) && !isTokenExpired(token);
    }

}
