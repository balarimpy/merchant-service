package io.movmint.msp.merchant.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

/**
 * Handle password encoding things.
 * @since : 2024-05-08
 */
public class PasswordUtil {

    private static BCryptPasswordEncoder passwordEncoder = null;

    private static Integer RESET_PASSWORD_TOKEN_EXPIRY = 300000;

    static {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hashing text with sugars.
     * @since : 2024-05-08
     *
     * @param str hashing text
     * @param sugars : An optional array of additional strings.
     * @return hashing String
     */
    public static String hashPin(String str, String... sugars) {
        String plainText = sugars.length == 0 ? str : str + "-" + String.join("|", sugars);
        return passwordEncoder.encode(plainText);
    }

    /**
     * Compare a plaintext pin (password), along with optional additional strings, against a hashed pin (password hash).
     * @since : 2024-05-08
     *
     * @param str : The plaintext pin (password) to be compared.
     * @param hash : The hashed pin (password hash) to compare against.
     * @param sugars : An optional array of additional strings.
     * @return true if the plaintext pin (along with additional strings, if any) matches the provided hash. Otherwise, it returns false.
     */
    public static boolean comparePinHash(String str, String hash, String... sugars) {
        String plainText = sugars.length == 0 ? str : str + "-" + String.join("|", sugars);
        return passwordEncoder.matches(plainText, hash);
    }

    /**
     * hash a plaintext password using the BCrypt hashing algorithm.
     * @since : 2024-05-08
     *
     * @param password : The plaintext pin (password) to be compared.
     * @return The hashed password, along with the salt, is returned as a single string.
     */
    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt(10);
        return BCrypt.hashpw(password, salt);
    }

    /**
     * generate a random password.
     * @since : 2024-05-08
     *
     * @return The generated random password, represented as a Base64-encoded string, is returned.
     */
    public static String generatePassword() {
        byte[] randomBytes = new byte[18];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Calculate the expiry time for a new reset password token.
     * @since : 2024-05-08
     *
     * @param resetPasswordTokenExpiry :  The duration in milliseconds for which the reset password token should be valid.
     * @return Instant : The method returns the calculated expiry time as an Instant.
     */
    public static Instant generateNewResetPasswordExpiry(int resetPasswordTokenExpiry) {
        Duration timeRange = Duration.ofMillis(resetPasswordTokenExpiry);
        return Instant.now().plus(timeRange);
    }

    /**
     * Generate a random token.
     * @since : 2024-05-14
     *
     * @return String : The method returns the random token.
     */
    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32]; // Adjust the size as needed
        random.nextBytes(tokenBytes);
        return bytesToHex(tokenBytes);
    }

    /**
     * Convert byte array to hexadecimal string
     * @since : 2024-05-14
     *
     * @param bytes : A byte array.
     * @return String : hexadecimal string representation of the byte array.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
