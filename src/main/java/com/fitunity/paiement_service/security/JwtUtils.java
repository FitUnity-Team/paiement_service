package com.fitunity.paiement_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.audience}")
    private String jwtAudience;

    // Extraire le userId du token
    public String getUserIdFromToken(String token) {
        return getClaims(token).get("userId", String.class);
    }

    // Extraire le rôle du token
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Extraire le statut abonnement
    public String getStatutAbonnementFromToken(String token) {
        return getClaims(token).get("statutAbonnement", String.class);
    }

    // Vérifier si le token est valide
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);

            // Vérifier issuer
            if (!jwtIssuer.equals(claims.getIssuer())) {
                return false;
            }

            // Vérifier audience
            if (!claims.getAudience().contains(jwtAudience)) {
                return false;
            }

            // Vérifier claims obligatoires
            if (claims.get("userId") == null ||
                    claims.get("role") == null ||
                    claims.getId() == null) {
                return false;
            }

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Récupérer les claims du token
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .requireIssuer(jwtIssuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Clé de signature
    private Key getSignKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}