package tutorial.dassist_ui;

import java.nio.file.Path;
import java.sql.*;

public class KnowledgeBaseDb {

    private final String url;

    public KnowledgeBaseDb(Path dbPath) {
        this.url = "jdbc:sqlite:" + dbPath.toAbsolutePath();
    }

    public void init() throws SQLException {
        try (Connection c = DriverManager.getConnection(url);
             Statement st = c.createStatement()) {

            st.execute("PRAGMA foreign_keys=ON;");

            st.execute("""
                CREATE TABLE IF NOT EXISTS knowledge_documents (
                    doc_id TEXT PRIMARY KEY,
                    original_name TEXT NOT NULL,
                    mime_type TEXT,
                    size_bytes INTEGER NOT NULL,
                    sha256 TEXT NOT NULL,
                    uploaded_at TEXT NOT NULL,
                    raw_path TEXT NOT NULL,
                    text_path TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'ACTIVE'
                );
            """);

            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_kb_sha256 ON knowledge_documents(sha256);");
        }
    }

    public void insert(KnowledgeDoc doc) throws SQLException {
        String sql = """
            INSERT INTO knowledge_documents
            (doc_id, original_name, mime_type, size_bytes, sha256, uploaded_at, raw_path, text_path, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, doc.docId());
            ps.setString(2, doc.originalName());
            ps.setString(3, doc.mimeType());
            ps.setLong(4, doc.sizeBytes());
            ps.setString(5, doc.sha256());
            ps.setString(6, doc.uploadedAtIso());
            ps.setString(7, doc.rawPath());
            ps.setString(8, doc.textPath());
            ps.setString(9, doc.status());
            ps.executeUpdate();
        }
    }
}
