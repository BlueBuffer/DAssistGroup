package dassist.rag.history;

public class ChatHistoryItem {

    private final String question;
    private final CitedAnswer answer;
    private boolean bookmarked;

    public ChatHistoryItem(String question, CitedAnswer answer) {
        this.question = question;
        this.answer = answer;
        this.bookmarked = false;
    }

    public String getQuestion() {
        return question;
    }

    public CitedAnswer getAnswer() {
        return answer;
    }

    public boolean isBookmarked(){
        return bookmarked;
    }

    public void toggleBookmark(){
        bookmarked = !bookmarked;
    }

    // ListView 显示用
    @Override
    public String toString() {
        return question;
    }
}
