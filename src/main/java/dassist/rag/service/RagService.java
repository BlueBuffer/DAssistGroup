package dassist.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.ArrayList;
import java.util.List;

public class RagService {

    private InMemoryEmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;
    private ChatLanguageModel chatModel;

    public RagService() {

        embeddingStore = new InMemoryEmbeddingStore<>();

        embeddingModel =
                OpenAiEmbeddingModel.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .build();

        chatModel =
                OpenAiChatModel.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .build();

        index();
    }

    // =======================
    // ===== Indexing ========
    // =======================
    public void index() {

        List<Document> documents =
                FileSystemDocumentLoader.loadDocumentsRecursively("knowledge-base");

        System.out.println("=================================");
        System.out.println("RAG INDEXING STARTED");
        System.out.println("Loaded documents: " + documents.size());
        System.out.println("=================================");

        int totalChunks = 0;
        final int MAX_CHARS = 2000;

        for (Document doc : documents) {


            String text = doc.text();
            if (text == null || text.isBlank()) {
                continue;
            }

            String source =
                    doc.metadata().getString("file_name") != null
                            ? doc.metadata().getString("file_name")
                            : "unknown";


// 🚫 跳过 README / 非知识文件
            if (source.equalsIgnoreCase("README.md")) {
                continue;
            }

            String currentSection = "GENERAL";

            String[] roughChunks = text.split("\\n\\n+");

            for (String rough : roughChunks) {

                String trimmed = rough.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                // ===== Robust section detection =====
                String upper = trimmed.toUpperCase();

// ===== Section detection (DO NOT continue) =====
                if (upper.contains("DRUG INTERACTIONS")) {
                    currentSection = "DRUG_INTERACTIONS";
                }
                else if (upper.contains("SIDE EFFECTS")) {
                    currentSection = "SIDE_EFFECTS";
                }
                else if (upper.contains("USES")) {
                    currentSection = "USES";
                }


                // ===== Metadata =====
                Metadata metadata = new Metadata();
                metadata.put("source", source);
                metadata.put("category", "General Information");
                metadata.put("section", currentSection);

                // ===== Chunking =====
                if (trimmed.length() <= MAX_CHARS) {

                    TextSegment segment =
                            TextSegment.from(trimmed, metadata);

                    embeddingStore.add(
                            embeddingModel.embed(trimmed).content(),
                            segment
                    );

                    totalChunks++;
                    System.out.println(
                            "[EMBED] (" + currentSection + ") length = " + trimmed.length()
                    );

                } else {
                    for (int start = 0; start < trimmed.length(); start += MAX_CHARS) {

                        int end = Math.min(start + MAX_CHARS, trimmed.length());
                        String slice = trimmed.substring(start, end);

                        if (slice.trim().length() < 100) {
                            continue;
                        }

                        TextSegment segment =
                                TextSegment.from(slice, metadata);

                        embeddingStore.add(
                                embeddingModel.embed(slice).content(),
                                segment
                        );

                        totalChunks++;
                        System.out.println(
                                "[EMBED] (" + currentSection + ") length = " + slice.length()
                        );
                    }
                }
            }
        }

        System.out.println("=================================");
        System.out.println("RAG INDEXING COMPLETED");
        System.out.println("Total embedded chunks: " + totalChunks);
        System.out.println("=================================");
    }

    // ============================
    // ===== Ask / Retrieve =======
    // ============================
    public CitedAnswer ask(String question) {

        ArrayList<Citation> citations = new ArrayList<>();

        System.out.println("\n========== QUERY ==========");
        System.out.println("Question: " + question);
        System.out.println("===========================\n");

        var queryEmbedding =
                embeddingModel.embed(question).content();

        List<EmbeddingMatch<TextSegment>> allMatches =
                embeddingStore.findRelevant(queryEmbedding, 10);

        List<EmbeddingMatch<TextSegment>> matches =
                allMatches.stream().limit(3).toList();



        if (matches.isEmpty()) {
            System.out.println("[WARN] No relevant chunks found.");
            return new CitedAnswer(
                    "No relevant information found.",
                    citations
            );
        }

        TextSegment segment = matches.get(0).embedded();

        String source = segment.metadata().getString("source");
        String section = segment.metadata().getString("section");

        if (source == null) source = "unknown";
        if (section == null) section = "GENERAL";

        System.out.println("[HIT] Source: " + source);
        System.out.println("[HIT] Section: " + section);
        System.out.println("[HIT] Context length: " + segment.text().length());

        String prompt =
                "Answer the question using the information below.\n\n"
                        + "Information:\n"
                        + segment.text() + "\n\n"
                        + "Question:\n"
                        + question;

        String answer = chatModel.generate(prompt);

        citations.add(new Citation(source, section));

        return new CitedAnswer(answer, citations);


    }
}
