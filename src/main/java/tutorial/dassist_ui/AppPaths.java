package tutorial.dassist_ui;

import java.nio.file.*;

public final class AppPaths {
    private AppPaths() {}

    public static Path appHome() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return Paths.get(appData, "D-Assist");
            }
        } else if (os.contains("mac")) {
            return Paths.get(userHome, "Library", "Application Support", "D-Assist");
        }

        return Paths.get(userHome, ".local", "share", "D-Assist");
    }

    public static Path dbPath() { return appHome().resolve("data").resolve("dassist.db"); }
    public static Path kbRawDir() { return appHome().resolve("kb").resolve("raw"); }
    public static Path kbTextDir() { return appHome().resolve("kb").resolve("text"); }

    public static void ensureDirs() throws Exception {
        Files.createDirectories(dbPath().getParent());
        Files.createDirectories(kbRawDir());
        Files.createDirectories(kbTextDir());
    }
}
