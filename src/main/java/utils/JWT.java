package utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class JWT {
    public static final int LEEWAY = 3600 * 12; // 12 hours
    private static final String SECRET;

    static {
        String secret;
        final String path = new File("secret.txt").exists() ?
                "secret.txt" :
                System.getProperty("user.home") + "/data/secret.txt";
        try {
            secret = Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());;
            secret = null;
        }
        SECRET = secret;
    }

    private JWT() {}

    public static String generate() throws JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return com.auth0.jwt.JWT.create()
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + LEEWAY * 1000))
                .sign(algorithm);
    }

    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = com.auth0.jwt.JWT.require(algorithm)
                    .withClaimPresence("iat")
                    .withClaimPresence("exp")
                    .acceptLeeway(60)
                    .acceptExpiresAt(LEEWAY)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }
}
