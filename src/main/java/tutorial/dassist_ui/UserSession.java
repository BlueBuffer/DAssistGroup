package tutorial.dassist_ui;

/**
 * Holds information about the currently logged-in user.
 * This is a simple in-memory session (perfect for your assignment).
 */
public class UserSession {

    private static String currentEmail;
    private static String currentRole;

    // Called after successful login
    public static void login(String email, String role) {
        currentEmail = email;
        currentRole = role;
    }

    // Called on logout
    public static void logout() {
        currentEmail = null;
        currentRole = null;
    }

    // Check if user is logged in
    public static boolean isLoggedIn() {
        return currentEmail != null && !currentEmail.isBlank();
    }

    // Get logged-in email
    public static String getEmail() {
        return currentEmail;
    }

    // Get logged-in role (ADMIN / STAFF)
    public static String getRole() {
        return currentRole;
    }
}
