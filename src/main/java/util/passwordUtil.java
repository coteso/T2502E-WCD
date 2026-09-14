package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Tien ich bam/kiem tra mat khau bang BCrypt (khong bao gio luu plain text password).
 */
public final class passwordUtil {

    private passwordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean matches(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // hash khong hop le
            return false;
        }
    }
}
