package com.sau.intern.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sau.intern.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JWTUtil {

    @Value(value = "${spring.security.secret}")
    private String secret;

    @Value(value = "${spring.security.tokenPrefix}")
    private String tokenPrefix;

    public String getSecret() {
        return secret;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String createToken(User user) {
        return getTokenPrefix() + JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .sign(Algorithm.HMAC512(getSecret().getBytes()));
    }

    public Optional<Long> parseToken(String token) {
        String subject = JWT.require(Algorithm.HMAC512(getSecret().getBytes()))
                .build()
                .verify(token.substring(getTokenPrefix().length()))
                .getSubject();
        if(subject != null)
            return Optional.of(Long.parseLong(subject));
        else return Optional.empty();
    }

    public boolean verifyToken(String token) {
        return token.startsWith(getTokenPrefix());
    }
}
