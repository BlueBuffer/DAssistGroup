package tutorial.dassist_ui;

import dassist.rag.history.ChatHistoryItem;
import dassist.rag.service.RagService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import util.SceneNavigator;

/**
 * Controller for user_dashboard.fxml
 * Handles UI interactions and connects UI with RagService
 */
public class UserDashboardController {

    /* ================== FXML injected nodes ================== */

    @FXML
    private Label mainHeading;

    @FXML
    private TextField askField;

    @FXML
    private VBox exampleBox;

    @FXML
    private VBox suggestionsBox;

    @FXML
    private ListView<ChatHistoryItem> historyListView;

    // Category buttons (left sidebar)
    @FXML private Button prescriptionBtn;
    @FXML private Button usesBtn;
    @FXML private Button dosagesBtn;
    @FXML private Button sideEffectsBtn;
    @FXML private Button interactionsBtn;
    @FXML private Button contraindicationsBtn;

    /* ================== Services ================== */

    private RagService ragService;

    private final ObservableList<ChatHistoryItem> historyItems =
            FXCollections.observableArrayList();


    /* ================== Lifecycle ================== */

    @FXML
    private void initialize() {

        // 绑定 history 数据
        historyListView.setItems(historyItems);

        // 点击 history → 显示对应回答
        historyListView.setOnMouseClicked(event -> {
            ChatHistoryItem selected =
                    historyListView.getSelectionModel().getSelectedItem();

            if (selected != null) {
                showAnswer(selected.getAnswer().getAnswer());
            }
        });

        // 默认选中 dosages
        selectDosages();
    }

    /* ================== Send / Ask ================== */

    @FXML
    private void onSend() {
        String question = askField.getText();

        if (question == null || question.isBlank()) {
            return;
        }

        // ✅ Lazy init：第一次用才创建 RagService
        if (ragService == null) {
            ragService = new RagService();
        }

        var result = ragService.ask(question);

// ⭐ 创建并保存 history item
        ChatHistoryItem item = new ChatHistoryItem(question, result);
        historyItems.add(0, item);

// ⭐ 切换页面并传完整数据
        FXMLLoader loader = SceneNavigator.goTo("answer_page.fxml");
        controller.AnswerPageController controller = loader.getController();
        controller.setAnswer(question, result, item);   // ✅ 3 个参数



        // Save to history
        historyItems.add(0, new ChatHistoryItem(question, result));

        // Clear input
        askField.clear();
    }


    /* ================== Example questions ================== */

    @FXML
    private void example1() {
        askField.setText(
                "What’s the starting dose of Metformin for Type 2 diabetes?"
        );
    }

    @FXML
    private void example2() {
        askField.setText(
                "What’s the maximum recommended dose of Jardiance?"
        );
    }

    @FXML
    private void example3() {
        askField.setText(
                "Does insulin dosage need adjustment in kidney disease?"
        );
    }

    /* ================== Category handlers ================== */

    @FXML
    private void onPrescription() {
        clearCategoryHighlight();
        prescriptionBtn.getStyleClass().add("active-category");

        mainHeading.setText("Drug prescription");
        askField.setPromptText("Ask about drug prescriptions...");

        updateExamples(
                "Who should be prescribed GLP-1 receptor agonists?",
                "When is insulin therapy recommended?"
        );
    }

    @FXML
    private void onUses() {
        clearCategoryHighlight();
        usesBtn.getStyleClass().add("active-category");

        mainHeading.setText("Drug uses");
        askField.setPromptText("Ask about drug uses...");

        updateExamples(
                "What is metformin used for?",
                "What conditions is insulin prescribed for?"
        );
    }

    @FXML
    private void onDosages() {
        selectDosages();
    }

    private void selectDosages() {
        clearCategoryHighlight();
        dosagesBtn.getStyleClass().add("active-category");

        mainHeading.setText("What can I help with?");
        askField.setPromptText("Tell me about common drug dosages...");

        updateExamples(
                "What’s the starting dose of Metformin for Type 2 diabetes?",
                "What’s the maximum dose of Jardiance and any kidney adjustments?"
        );
    }

    @FXML
    private void onSideEffects() {
        clearCategoryHighlight();
        sideEffectsBtn.getStyleClass().add("active-category");

        mainHeading.setText("Side effects");
        askField.setPromptText("Ask about side effects...");

        updateExamples(
                "What are the common side effects of metformin?",
                "Does insulin cause weight gain?"
        );
    }

    @FXML
    private void onInteractions() {
        clearCategoryHighlight();
        interactionsBtn.getStyleClass().add("active-category");

        mainHeading.setText("Drug interactions");
        askField.setPromptText("Ask about drug interactions...");

        updateExamples(
                "Does metformin interact with contrast media?",
                "Can sulfonylureas be taken with insulin?"
        );
    }

    @FXML
    private void onContraindications() {
        clearCategoryHighlight();
        contraindicationsBtn.getStyleClass().add("active-category");

        mainHeading.setText("Contraindications");
        askField.setPromptText("Ask about contraindications...");

        updateExamples(
                "When should metformin be avoided?",
                "Who should not use SGLT2 inhibitors?"
        );
    }

    /* ================== UI helper methods ================== */

    private void updateExamples(String... examples) {
        exampleBox.getChildren().clear();

        for (String text : examples) {
            Button btn = new Button(text);
            btn.getStyleClass().add("example-btn");
            btn.setOnAction(e -> askField.setText(text));
            exampleBox.getChildren().add(btn);
        }
    }

    private void showAnswer(String answer) {
        Label answerLabel = new Label(answer);
        answerLabel.setWrapText(true);
        answerLabel.getStyleClass().add("answer-text");

        suggestionsBox.getChildren().add(answerLabel);
    }

    private void clearCategoryHighlight() {
        prescriptionBtn.getStyleClass().remove("active-category");
        usesBtn.getStyleClass().remove("active-category");
        dosagesBtn.getStyleClass().remove("active-category");
        sideEffectsBtn.getStyleClass().remove("active-category");
        interactionsBtn.getStyleClass().remove("active-category");
        contraindicationsBtn.getStyleClass().remove("active-category");
    }
}
