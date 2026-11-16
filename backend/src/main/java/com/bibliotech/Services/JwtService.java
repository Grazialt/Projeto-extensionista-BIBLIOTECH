package com.bibliotech.Services;

import com.bibliotech.Entidades.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long EXPIRATION_TIME = 86400000;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(secret)
        );
    }

    public String gerarToken(Usuario usuario) {

        Date agora = new Date();

        Date expiracao = new Date(
            agora.getTime() + EXPIRATION_TIME
        );

        return Jwts.builder()
            .subject(usuario.getEmail())
            .claim("tipo", usuario.getTipo())
            .issuedAt(agora)
            .expiration(expiracao)
            .signWith(secretKey)
            .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairTipo(String token) {
        return extrairClaims(token).get("tipo", String.class);
    }

    public boolean validarToken(String token, String email) {
        try {
            String emailToken = extrairEmail(token);

            return emailToken.equals(email)
                && extrairClaims(token).getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}