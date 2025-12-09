package com.anki.anki_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    private javax.crypto.SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            logger.info("=== JwtUtils: Validating JWT token");
            Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(authToken);
            logger.info("=== JwtUtils: JWT token is valid");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("=== JwtUtils: Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("=== JwtUtils: JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("=== JwtUtils: JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("=== JwtUtils: JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("=== JwtUtils: JWT validation error: {}", e.getMessage(), e);
        }

        return false;
    }
}
