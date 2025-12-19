package dassist.rag.citation;

import java.util.List;

public class CitedAnswer {

    private final String answer;
    private final List<Citation> citations;

    public CitedAnswer(String answer, List<Citation> citations) {
        this.answer = answer;
        this.citations = citations;
    }

    public String getAnswer() {
        return answer;
    }

    public List<Citation> getCitations() {
        return citations;
    }
}
