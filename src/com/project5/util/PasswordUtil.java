package com.project5.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Handles salted password hashing using PBKDF2 (industry-standard, slow
 * hash algorithm suited for passwords). Plain-text passwords are never
 * stored: only the derived hash and the random salt used to create it.
 *
 * Used for both the account password and the security-question answer.
 */
public final class PasswordUtil {

    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final int ITERATIONS = 65536;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private PasswordUtil() {
    }

    /** Generates a new random salt, Base64-encoded for storage. */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** Derives a Base64-encoded hash from plain text + a Base64-encoded salt. */
    public static String hash(String plainText, String base64Salt) {
        try {
            byte[] salt = Base64.getDecoder().decode(base64Salt);
            PBEKeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt, ITERATIONS, HASH_BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] derived = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(derived);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Unable to hash value", e);
        }
    }

    /** Verifies plain text against a stored hash + salt pair, in constant time. */
    public static boolean verify(String plainText, String storedHash, String storedSalt) {
        if (plainText == null || storedHash == null || storedSalt == null) {
            return false;
        }
        String computed = hash(plainText, storedSalt);
        return constantTimeEquals(computed, storedHash);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
