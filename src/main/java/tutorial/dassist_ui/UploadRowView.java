package tutorial.dassist_ui;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.Objects;

public class UploadRowView {

    private final HBox root = new HBox(16);

    private final Label fileName = new Label();
    private final Label sizeLabel = new Label();
    private final ImageView statusIcon = new ImageView();
    private final Label statusText = new Label();
    private final ImageView closeIcon = new ImageView();

    private RotateTransition spinner;
    private Runnable onClose = () -> {};

    public UploadRowView(String fileNameText, long totalBytes) {
        root.setPrefHeight(37);
        root.setPrefWidth(575);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(0, 18, 0, 18));
        root.setStyle("-fx-background-radius: 80; -fx-background-color: EEF1F7;");

        fileName.setText(fileNameText);
        fileName.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");

        sizeLabel.setText("0 KB of " + KnowledgeBaseService.humanBytes(totalBytes));
        sizeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        sizeLabel.setOpacity(0.55);

        statusText.setText("Uploading...");
        statusText.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");

        statusIcon.setFitHeight(18);
        statusIcon.setFitWidth(18);
        statusIcon.setPreserveRatio(true);

        closeIcon.setFitHeight(20);
        closeIcon.setFitWidth(18);
        closeIcon.setPreserveRatio(true);
        closeIcon.setImage(loadImg("/tutorial/dassist_ui/images/close_circle.png"));
        closeIcon.setOnMouseClicked(e -> onClose.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // initial icon = loading
        setLoadingIcon();

        root.getChildren().addAll(
                fileName,
                sizeLabel,
                statusIcon,
                statusText,
                spacer,
                closeIcon
        );
    }

    public HBox root() { return root; }

    public void setOnClose(Runnable onClose) { this.onClose = onClose != null ? onClose : () -> {}; }

    public void bindToTask(Task<?> task) {
        // sizeLabel shows “x KB of y KB”
        sizeLabel.textProperty().bind(task.messageProperty());

        task.runningProperty().addListener((obs, was, isNow) -> {
            if (isNow) {
                statusText.setText("Uploading...");
                setLoadingIcon();
            }
        });
    }

    public void markCompleted(KnowledgeDoc doc) {
        stopSpinner();
        sizeLabel.textProperty().unbind();
        sizeLabel.setText(KnowledgeBaseService.humanBytes(doc.sizeBytes()) + " of " + KnowledgeBaseService.humanBytes(doc.sizeBytes()));
        statusIcon.setImage(loadImg("/tutorial/dassist_ui/images/greentick_circle.png"));
        statusText.setText("Completed");
    }

    public void markFailed(Throwable ex) {
        stopSpinner();
        sizeLabel.textProperty().unbind();
        statusIcon.setImage(loadImg("/tutorial/dassist_ui/images/danger_icon.png"));
        statusText.setText("Failed");
        sizeLabel.setText(ex != null ? ex.getMessage() : "Unknown error");
        sizeLabel.setOpacity(0.75);
    }

    public void markCancelled() {
        stopSpinner();
        sizeLabel.textProperty().unbind();
        statusIcon.setImage(loadImg("/tutorial/dassist_ui/images/danger_icon.png"));
        statusText.setText("Cancelled");
        sizeLabel.setText("Upload cancelled");
        sizeLabel.setOpacity(0.75);
    }

    private void setLoadingIcon() {
        statusIcon.setImage(loadImg("/tutorial/dassist_ui/images/loading.png"));
        startSpinner();
    }

    private void startSpinner() {
        stopSpinner();
        spinner = new RotateTransition(Duration.millis(900), statusIcon);
        spinner.setByAngle(360);
        spinner.setCycleCount(Animation.INDEFINITE);
        spinner.play();
    }

    private void stopSpinner() {
        if (spinner != null) spinner.stop();
        spinner = null;
        statusIcon.setRotate(0);
    }

    private static Image loadImg(String path) {
        return new Image(Objects.requireNonNull(UploadRowView.class.getResourceAsStream(path)));
    }
}
