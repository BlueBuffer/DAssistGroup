package tutorial.dassist_ui;

import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.util.List;

public class KnowledgeBaseService {

    private final KnowledgeBaseDb db = new KnowledgeBaseDb(AppPaths.dbPath());

    public void init() throws Exception {
        db.init();
    }

    public void close() { /* no-op (we open connections per operation) */ }

    public Task<KnowledgeDoc> createUploadTask(File selected) {
        return new Task<>() {
            @Override
            protected KnowledgeDoc call() throws Exception {
                if (selected == null || !selected.exists()) {
                    throw new FileNotFoundException("File not found");
                }

                String originalName = selected.getName();
                String lower = originalName.toLowerCase();

                String ext;
                if (lower.endsWith(".pdf")) ext = "pdf";
                else if (lower.endsWith(".docx")) ext = "docx";
                else if (lower.endsWith(".txt")) ext = "txt";
                else throw new IllegalArgumentException("Unrecognized file format");

                UUID docId = UUID.randomUUID();
                long total = selected.length();

                Path rawFolder = AppPaths.kbRawDir().resolve(docId.toString());
                Files.createDirectories(rawFolder);

                String safeName = safeFileName(originalName);
                Path rawTarget = rawFolder.resolve(safeName);

                // Copy file with progress + SHA-256 in one pass
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                long copied = 0;

                try (InputStream in = new BufferedInputStream(new FileInputStream(selected));
                     OutputStream out = new BufferedOutputStream(Files.newOutputStream(rawTarget, StandardOpenOption.CREATE_NEW))) {

                    byte[] buf = new byte[64 * 1024];
                    int r;
                    while ((r = in.read(buf)) != -1) {
                        if (isCancelled()) throw new InterruptedIOException("Cancelled");

                        out.write(buf, 0, r);
                        digest.update(buf, 0, r);

                        copied += r;
                        updateMessage(humanBytes(copied) + " of " + humanBytes(total));
                        updateProgress(copied, total);
                    }
                } catch (Exception e) {
                    // cleanup partially copied file/folder
                    safeDelete(rawTarget);
                    safeDelete(rawFolder);
                    throw e;
                }

                String sha256 = HexFormat.of().formatHex(digest.digest());
                String mime = mimeFromExt(ext);

                // Extract and save text
                String extracted = extractText(rawTarget, ext);
                String normalized = normalizeText(extracted);

                Path textTarget = AppPaths.kbTextDir().resolve(docId + ".txt");
                Files.writeString(textTarget, normalized, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);

                // Insert into SQLite
                KnowledgeDoc doc = new KnowledgeDoc(
                        docId.toString(),
                        originalName,
                        mime,
                        total,
                        sha256,
                        Instant.now().toString(),
                        rawTarget.toString(),
                        textTarget.toString(),
                        "ACTIVE"
                );

                db.insert(doc);

                // final UI label
                updateMessage(humanBytes(total) + " of " + humanBytes(total));
                return doc;
            }
        };
    }

    private static String extractText(Path rawFile, String ext) throws Exception {
        return switch (ext) {
            case "txt" -> Files.readString(rawFile, StandardCharsets.UTF_8);

            case "pdf" -> {
                try (PDDocument doc = PDDocument.load(rawFile.toFile())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    yield stripper.getText(doc);
                }
            }

            case "docx" -> {
                try (InputStream in = Files.newInputStream(rawFile);
                     XWPFDocument docx = new XWPFDocument(in)) {
                    StringBuilder sb = new StringBuilder();
                    for (XWPFParagraph p : docx.getParagraphs()) {
                        sb.append(p.getText()).append("\n");
                    }
                    yield sb.toString();
                }
            }

            default -> throw new IllegalArgumentException("Unsupported extension: " + ext);
        };
    }

    private static String normalizeText(String s) {
        if (s == null) return "";
        String t = s.replace("\r\n", "\n").replace("\r", "\n");
        // light normalization
        t = t.replaceAll("[ \\t\\x0B\\f]+", " ");
        t = t.replaceAll("\\n{3,}", "\n\n");
        return t.trim();
    }

    private static String mimeFromExt(String ext) {
        return switch (ext) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt" -> "text/plain";
            default -> "application/octet-stream";
        };
    }

    private static String safeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static void safeDelete(Path p) {
        try {
            if (p == null) return;
            if (Files.isDirectory(p)) {
                // try delete empty dir
                Files.deleteIfExists(p);
            } else {
                Files.deleteIfExists(p);
            }
        } catch (Exception ignored) {}
    }

    public static String humanBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.0f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.1f MB", mb);
        double gb = mb / 1024.0;
        return String.format("%.2f GB", gb);
    }

    public List<KnowledgeDoc> listActiveDocs() throws Exception {
        return db.listActive();
    }

    public Task<Void> createDeleteTask(String docId) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                KnowledgeDoc doc = db.findById(docId);
                if (doc == null) return null;

                // delete raw file + doc folder
                try {
                    Path raw = Path.of(doc.rawPath());
                    Files.deleteIfExists(raw);

                    Path rawDir = raw.getParent(); // .../raw/<docId>/
                    if (rawDir != null && Files.isDirectory(rawDir)) {
                        // delete folder if empty
                        try (var s = Files.list(rawDir)) {
                            if (s.findAny().isEmpty()) Files.deleteIfExists(rawDir);
                        }
                    }
                } catch (Exception ignored) {}

                // delete extracted text
                try {
                    Files.deleteIfExists(Path.of(doc.textPath()));
                } catch (Exception ignored) {}

                // delete DB row (this removes UNIQUE constraint block for re-upload)
                db.deleteById(docId);

                return null;
            }
        };
    }

}
