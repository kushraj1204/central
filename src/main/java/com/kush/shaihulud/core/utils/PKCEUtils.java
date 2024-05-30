package com.kush.shaihulud.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

/**
 * This class provides utility methods for generating PKCE (Proof Key for Code Exchange) code verifiers and challenges.
 */
@Component
public class PKCEUtils {

    public static final int DEFAULT_CODE_VERIFIER_LENGTH = 64;

    /**
     * Generates a random code verifier of the default length (64 bytes).
     *
     * @return The generated code verifier as a Base64-encoded string.
     */
    public String generateCodeVerifier() {
        return generateCodeVerifier(DEFAULT_CODE_VERIFIER_LENGTH);
    }

    /**
     * Generates a random code verifier of the specified length.
     *
     * @param length The length of the code verifier to generate, in bytes.
     * @return The generated code verifier as a Base64-encoded string.
     */
    public String generateCodeVerifier(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifierBytes = new byte[length];
        secureRandom.nextBytes(codeVerifierBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierBytes);
    }

    /**
     * Generates a code challenge from the given code verifier using SHA-256 as the default hashing algorithm.
     *
     * @param codeVerifier The code verifier to generate a challenge for.
     * @return The generated code challenge as a Base64-encoded string.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available on the current platform.
     */
    public String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        return generateCodeChallenge(codeVerifier, "SHA-256");
    }

    /**
     * Generates a code challenge from the given code verifier using the specified hashing algorithm.
     *
     * @param codeVerifier The code verifier to generate a challenge for.
     * @param algorithm The hashing algorithm to use.
     * @return The generated code challenge as a Base64-encoded string.
     * @throws NoSuchAlgorithmException If the specified algorithm is not available on the current platform.
     */
    public String generateCodeChallenge(String codeVerifier, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] codeVerifierBytes = codeVerifier.getBytes();
        messageDigest.update(codeVerifierBytes, 0, codeVerifierBytes.length);
        byte[] codeChallengeBytes = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeChallengeBytes);
    }
}
