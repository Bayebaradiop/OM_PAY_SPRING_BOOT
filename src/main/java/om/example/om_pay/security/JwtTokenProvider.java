package om.example.om_pay.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import om.example.om_pay.config.JwtConfig;

/**
 * Composant responsable de la génération et validation des tokens JWT
 */
@Component
public class JwtTokenProvider {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Génère un token JWT pour un utilisateur
     * @param telephone Le numéro de téléphone de l'utilisateur
     * @return Le token JWT
     */
    public String generateToken(String telephone) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(telephone)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrait le numéro de téléphone du token JWT
     * @param token Le token JWT
     * @return Le numéro de téléphone
     */
    public String getPhoneFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Valide un token JWT
     * @param authToken Le token à valider
     * @return true si le token est valide
     */
    public boolean validateToken(String authToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            
            return true;
        } catch (SecurityException ex) {
            System.err.println("Signature JWT invalide");
        } catch (MalformedJwtException ex) {
            System.err.println("Token JWT invalide");
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT expiré");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token JWT non supporté");
        } catch (IllegalArgumentException ex) {
            System.err.println("Claims JWT vide");
        }
        return false;
    }
}
