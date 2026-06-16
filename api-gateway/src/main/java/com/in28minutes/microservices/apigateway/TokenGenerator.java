package com.in28minutes.microservices.apigateway;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Simple helper to generate HS256 JWTs for local testing.
 * Usage (from the api-gateway module):
 *   mvn -pl api-gateway exec:java -Dexec.mainClass=com.in28minutes.microservices.apigateway.TokenGenerator -Dexec.args="<secret> <subject> [roles csv]"
 * Example:
 *   mvn -pl api-gateway exec:java -Dexec.mainClass=com.in28minutes.microservices.apigateway.TokenGenerator -Dexec.args="ChangeThisSecretToAStrongRandomValue user1 USER,ADMIN"
 *
 * The generated token is printed to stdout. Use it with curl:
 *   curl -H "Authorization: Bearer <token>" http://localhost:8765/protected
 */
public class TokenGenerator {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TokenGenerator <secret> <subject> [roles csv]");
            System.exit(1);
        }

        String secret = args[0];
        String subject = args[1];
        String rolesCsv = args.length >= 3 ? args[2] : "USER";

        List<String> roles = Arrays.asList(rolesCsv.split(","));

        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + 3600_000L); // 1 hour expiry

        String token = Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        System.out.println(token);
    }
}
