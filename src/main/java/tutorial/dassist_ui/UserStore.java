package tutorial.dassist_ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {

    // email -> User
    private static final Map<String, User> USERS = new ConcurrentHashMap<>();

    // ---------------- USER MODEL ----------------
    public static class User {
        String name;
        private String email;
        private String password;

        public User(String name, String email, String password) {
            this.name = name;
            this.email = email.toLowerCase();
            this.password = password;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }

        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email.toLowerCase(); }
        public void setPassword(String password) { this.password = password; }

        public String name() {
            return null;
        }
    }

    // ---------------- DEMO USERS ----------------
    static {
        addUser("Open JavaFX", "openjavafx@gmail.com", "Password1");
        addUser("Admin", "admin@gmail.com", "Admin1234");
    }

    // ---------------- BASIC OPS ----------------
    public static boolean exists(String email) {
        if (email == null) return false;
        return USERS.containsKey(email.toLowerCase());
    }

    public static void addUser(String name, String email, String password) {
        if (email == null) return;
        USERS.put(email.toLowerCase(), new User(name, email, password));
    }

    public static boolean validate(String email, String password) {
        if (email == null || password == null) return false;
        User u = USERS.get(email.toLowerCase());
        return u != null && password.equals(u.getPassword());
    }

    public static User getUser(String email) {
        if (email == null) return null;
        return USERS.get(email.toLowerCase());
    }

    // ---------------- UPDATE OPS ----------------
    public static boolean updateName(String email, String newName) {
        User u = getUser(email);
        if (u == null) return false;
        u.setName(newName);
        return true;
    }

    public static boolean updatePassword(String email, String newPassword) {
        User u = getUser(email);
        if (u == null) return false;
        u.setPassword(newPassword);
        return true;
    }
    public static boolean updateEmail(String oldEmail, String newEmail) {
        if (oldEmail == null || newEmail == null) return false;

        oldEmail = oldEmail.toLowerCase();
        newEmail = newEmail.toLowerCase();

        if (!USERS.containsKey(oldEmail)) return false;
        if (USERS.containsKey(newEmail)) return false;

        User u = USERS.remove(oldEmail);
        u.setEmail(newEmail);
        USERS.put(newEmail, u);

        // ✅ Fix: use getEmail() not getCurrentEmail()
        if (UserSession.isLoggedIn() && oldEmail.equalsIgnoreCase(UserSession.getEmail())) {
            UserSession.login(newEmail, UserSession.getRole());
        }

        return true;
    }

}
