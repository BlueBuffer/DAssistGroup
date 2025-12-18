package tutorial.dassist_ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;

public final class DeleteConfirmDialog {

    private DeleteConfirmDialog() {}

    public static boolean show(Window owner, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(DeleteConfirmDialog.class.getResource(
                    "/tutorial/dassist_ui/admin_confirm_delete_dialog.fxml"
            ));
            Parent root = loader.load();

            AdminConfirmDeleteDialogController controller = loader.getController();
            controller.setMessage(message);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();

            return controller.isConfirmed();

        } catch (Exception e) {
            e.printStackTrace();
            return false; // safest default
        }
    }
}
