package utilsUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class Dialogs {
    /**
     * @param message the error message
     * A dialog that prints an error message
     */
    public static void printErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setHeaderText("Error occured !");
        alert.showAndWait();
    }

    /**
     * @param content the dialog content
     * @param header the dialog header
     * A dialog that prints an info message
     */
    public static void printInfo(String content, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * A dialog that prints a confirmation with two buttons
     * @param content the dialog content
     * @param header the dialog header
     * @return "Yes" - if the "Yes" button is pressed
     *         "No" - if the "No" button is pressed
     */
    public static String printConfirmation(String content, String header){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);
        var ref = new Object() {
            String resultType;
        };
        alert.showAndWait().ifPresent(type -> {
            if (type == yesButton)
                ref.resultType = "Yes";
            else
                ref.resultType = "No";
        });
        return ref.resultType;
    }
}
