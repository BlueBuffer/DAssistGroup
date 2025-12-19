package tutorial.dassist_ui;

import dassist.rag.citation.CitedAnswer;
import dassist.rag.citation.Citation;
import dassist.rag.history.ChatHistoryItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import util.SceneNavigator;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class AnswerPageController {

    // ⭐ 当前显示的聊天记录
    private ChatHistoryItem currentItem;

    /* ================== FXML Nodes ================== */

    @FXML
    private Label questionLabel;

    @FXML
    private Label answerLabel;

    @FXML
    private VBox sourcesBox;

    @FXML
    private Button bookmarkBtn;

    @FXML
    private Button copyBtn;


    /* ================== Data Binding ================== */

    /**
     * 接收 Dashboard 传过来的数据
     */
    public void setAnswer(String question,
                          CitedAnswer result,
                          ChatHistoryItem item) {

        this.currentItem = item;

        // 显示问题 & 回答
        questionLabel.setText(question);
        answerLabel.setText(result.getAnswer());

        // 显示 sources
        sourcesBox.getChildren().clear();
        for (Citation c : result.getCitations()) {
            Label src = new Label(
                    "• " + c.getSource() + " (" + c.getCategory() + ")"
            );
            src.getStyleClass().add("source-text");
            sourcesBox.getChildren().add(src);
        }

        // 更新 bookmark 图标
        updateBookmarkIcon();
    }

    /* ================== Bookmark ================== */

    @FXML
    private void onBookmark() {
        if (currentItem == null) return;

        currentItem.toggleBookmark();
        updateBookmarkIcon();
    }

    private void updateBookmarkIcon() {
        if (currentItem != null && currentItem.isBookmarked()) {
            bookmarkBtn.setText("💾 Saved");
            bookmarkBtn.getStyleClass().add("bookmarked");
        } else {
            bookmarkBtn.setText("💾");
            bookmarkBtn.getStyleClass().remove("bookmarked");
        }
    }

    @FXML
    private void onCopy() {
        if (answerLabel == null || answerLabel.getText().isBlank()) {
            return;
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(answerLabel.getText());

        clipboard.setContent(content);

        // 简单用户反馈（可选）
        copyBtn.setText("✅");
    }


    /* ================== Navigation ================== */

    @FXML
    private void onBack() {
        SceneNavigator.goTo("user_dashboard.fxml");
    }
}
