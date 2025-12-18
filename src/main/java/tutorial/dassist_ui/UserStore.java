package tutorial.dassist_ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserStore {

    // email -> password
    private static final Map<String, String> USERS = new ConcurrentHashMap<>();

    static {
        // demo users
        USERS.put("openjavafx@gmail.com", "Password1");
        USERS.put("admin@gmail.com", "Admin1234");
    }

    public static boolean exists(String email) {
        return USERS.containsKey(email.toLowerCase());
    }

    public static void addUser(String email, String password) {
        USERS.put(email.toLowerCase(), password);
    }

    public static boolean validate(String email, String password) {
        String saved = USERS.get(email.toLowerCase());
        return saved != null && saved.equals(password);
    }
}
