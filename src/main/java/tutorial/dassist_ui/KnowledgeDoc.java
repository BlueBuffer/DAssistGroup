package tutorial.dassist_ui;

public record KnowledgeDoc(
        String docId,
        String originalName,
        String mimeType,
        long sizeBytes,
        String sha256,
        String uploadedAtIso,
        String rawPath,
        String textPath,
        String status
) {}
