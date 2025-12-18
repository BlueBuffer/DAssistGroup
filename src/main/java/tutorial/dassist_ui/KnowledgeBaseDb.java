package tutorial.dassist_ui;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

            // Keep or replace this depending on whether you want "soft delete" uniqueness.
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

    // ============================
    // NEW METHODS (paste below)
    // ============================

    public List<KnowledgeDoc> listActive() throws SQLException {
        String sql = """
            SELECT doc_id, original_name, mime_type, size_bytes, sha256, uploaded_at,
                   raw_path, text_path, status
            FROM knowledge_documents
            WHERE status='ACTIVE'
            ORDER BY uploaded_at DESC;
        """;

        List<KnowledgeDoc> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new KnowledgeDoc(
                        rs.getString("doc_id"),
                        rs.getString("original_name"),
                        rs.getString("mime_type"),
                        rs.getLong("size_bytes"),
                        rs.getString("sha256"),
                        rs.getString("uploaded_at"),
                        rs.getString("raw_path"),
                        rs.getString("text_path"),
                        rs.getString("status")
                ));
            }
        }
        return out;
    }

    public KnowledgeDoc findById(String docId) throws SQLException {
        String sql = """
            SELECT doc_id, original_name, mime_type, size_bytes, sha256, uploaded_at,
                   raw_path, text_path, status
            FROM knowledge_documents
            WHERE doc_id = ?;
        """;

        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, docId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new KnowledgeDoc(
                        rs.getString("doc_id"),
                        rs.getString("original_name"),
                        rs.getString("mime_type"),
                        rs.getLong("size_bytes"),
                        rs.getString("sha256"),
                        rs.getString("uploaded_at"),
                        rs.getString("raw_path"),
                        rs.getString("text_path"),
                        rs.getString("status")
                );
            }
        }
    }

    public void deleteById(String docId) throws SQLException {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM knowledge_documents WHERE doc_id=?"
             )) {

            ps.setString(1, docId);
            ps.executeUpdate();
        }
    }
}
