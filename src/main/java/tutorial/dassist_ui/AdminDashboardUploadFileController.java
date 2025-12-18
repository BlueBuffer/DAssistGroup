package tutorial.dassist_ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDashboardUploadFileController implements Initializable {

    @FXML private AnchorPane dropArea;

    private VBox uploadsBox;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));

    private final KnowledgeBaseService kb = new KnowledgeBaseService();

    private static final String DROP_DEFAULT_STYLE =
            "-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-style: segments(10, 10); " +
                    "-fx-border-radius: 15; -fx-background-radius: 15; -fx-background-color: f5f5f5;";

    private static final String DROP_HOVER_STYLE =
            "-fx-border-color: #7aa7ff; -fx-border-width: 2; -fx-border-style: segments(10, 10); " +
                    "-fx-border-radius: 15; -fx-background-radius: 15; -fx-background-color: f5f5f5;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AppPaths.ensureDirs();
            kb.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createUploadsBox();

        // Persistent bars when reopening the screen
        loadPersistentUploads();

        // Click-to-browse
        dropArea.setOnMouseClicked(e -> browseFiles());

        // Drag & Drop
        dropArea.setOnDragOver(this::onDragOver);
        dropArea.setOnDragDropped(this::onDragDropped);
        dropArea.setOnDragEntered(e -> dropArea.setStyle(DROP_HOVER_STYLE));
        dropArea.setOnDragExited(e -> dropArea.setStyle(DROP_DEFAULT_STYLE));
    }

    private void createUploadsBox() {
        uploadsBox = new VBox(12);
        uploadsBox.setPrefWidth(dropArea.getPrefWidth());
        uploadsBox.setLayoutX(dropArea.getLayoutX());

        // Place below the drop area (your HBox sample is around y=491)
        double y = dropArea.getLayoutY() + dropArea.getPrefHeight() + 42;
        uploadsBox.setLayoutY(y);

        Parent parent = dropArea.getParent();
        if (parent instanceof AnchorPane root) {
            root.getChildren().add(uploadsBox);
        } else {
            throw new IllegalStateException("dropArea parent is not AnchorPane.");
        }
    }

    private void loadPersistentUploads() {
        try {
            List<KnowledgeDoc> docs = kb.listActiveDocs();

            for (KnowledgeDoc doc : docs) {
                UploadRowView row = new UploadRowView(doc.originalName(), doc.sizeBytes());
                uploadsBox.getChildren().add(row.root());
                row.markCompleted(doc);

                row.setOnClose(() -> requestDelete(doc, row));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void browseFiles() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Knowledge Base Files");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF/DOCX/TXT", "*.pdf", "*.docx", "*.txt")
        );

        List<File> files = chooser.showOpenMultipleDialog(dropArea.getScene().getWindow());
        if (files != null) files.forEach(this::startUpload);
    }

    private void onDragOver(DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasFiles() && hasAllowed(db.getFiles())) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }

    private void onDragDropped(DragEvent e) {
        Dragboard db = e.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            success = true;
            for (File f : db.getFiles()) startUpload(f);
        }

        e.setDropCompleted(success);
        e.consume();
    }

    private boolean hasAllowed(List<File> files) {
        for (File f : files) {
            String name = f.getName().toLowerCase();
            if (name.endsWith(".pdf") || name.endsWith(".docx") || name.endsWith(".txt")) return true;
        }
        return false;
    }

    private void startUpload(File file) {
        // Validate format quickly (service validates too)
        String n = file.getName().toLowerCase();
        if (!(n.endsWith(".pdf") || n.endsWith(".docx") || n.endsWith(".txt"))) {
            UploadRowView row = new UploadRowView(file.getName(), file.length());
            uploadsBox.getChildren().add(0, row.root());
            row.markFailed(new IllegalArgumentException("Unrecognized file format"));
            return;
        }

        UploadRowView row = new UploadRowView(file.getName(), file.length());
        uploadsBox.getChildren().add(0, row.root());

        Task<KnowledgeDoc> task = kb.createUploadTask(file);

        // show size updates while uploading
        row.bindToTask(task);

        // While uploading, X cancels and removes row (no DB insert yet if cancelled early)
        row.setOnClose(() -> {
            if (task.isRunning()) task.cancel();
            uploadsBox.getChildren().remove(row.root());
        });

        task.setOnSucceeded(ev -> {
            KnowledgeDoc doc = task.getValue();
            row.markCompleted(doc);

            // After completion, X should do permanent delete with confirmation
            row.setOnClose(() -> requestDelete(doc, row));
        });

        task.setOnFailed(ev -> row.markFailed(task.getException()));
        task.setOnCancelled(ev -> row.markCancelled());

        executor.submit(task);
    }

    private void requestDelete(KnowledgeDoc doc, UploadRowView row) {
        boolean confirmed = DeleteConfirmDialog.show(
                dropArea.getScene().getWindow(),
                "Are you sure you want to delete this file?"
        );

        if (!confirmed) return;

        // UI state
        row.markDeleting();

        Task<Void> deleteTask = kb.createDeleteTask(doc.docId());
        deleteTask.setOnSucceeded(e -> uploadsBox.getChildren().remove(row.root()));
        deleteTask.setOnFailed(e -> row.markFailed(deleteTask.getException()));

        executor.submit(deleteTask);
    }

    // Optional: call when closing app (if you have a central lifecycle)
    public void shutdown() {
        executor.shutdownNow();
        kb.close();
    }
}
