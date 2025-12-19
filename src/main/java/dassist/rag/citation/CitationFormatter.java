package dassist.rag.citation;

public class CitationFormatter {

    public static String format(int index, Citation citation) {

        return index + ". "
                + citation.getSource()
                + " (Category: "
                + citation.getCategory()
                + ")";
    }
}
