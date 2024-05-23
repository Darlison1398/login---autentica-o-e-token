package com.example.login.auth.api.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.login.auth.api.domain.User;

@Service
public class TokenService {

    //vai vim do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            // fazendo o token
            String token = JWT.create()
                              .withIssuer("login-auth-api")
                              .withSubject(user.getEmail())
                              .withExpiresAt(this.generateExpirationDate()) // vai definir o tempo que uma sessão fica ativa
                              .sign(algorithm);

            return token;
            
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while authenticate");
        }
    }


    // função para validar o token
    public String validateToken(String token) {
        try {
            
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                      .withIssuer("login-auth-api")
                      .build()
                      .verify(token)
                      .getSubject();


        } catch (JWTVerificationException e) {
            return null;
        }
    }


    // função de expiração de sessão
    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // 2 horas
    }
    
}
