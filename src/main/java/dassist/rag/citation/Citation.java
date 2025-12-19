package dassist.rag.citation;

public class Citation {

    private final String source;
    private final String category;

    public Citation(String source, String category) {
        this.source = source;
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public String getCategory() {
        return category;
    }
}
