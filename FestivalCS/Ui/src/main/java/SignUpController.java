import domain.Seller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.IService;
import utilsUI.Dialogs;

import java.io.IOException;
import java.security.KeyException;
import java.util.Objects;

public class SignUpController {

    private IService server;

    public void setServer(IService iService)
    {
        server = iService;
    }

    @FXML
    public TextField emailText;
    @FXML
    public PasswordField passwordText;
    @FXML
    public PasswordField confirmPasswordText;
    @FXML
    public Button signUpButton;
    @FXML
    public AnchorPane myAnchorPane;
    @FXML
    public BorderPane titleBorderPane;



    @FXML
    public void openLogIn(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/log-in.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();

            //controller initializations
            LogInController controller = loader.getController();
            final double[] xOffset = new double[1];
            final double[] yOffset = new double[1];
            controller.titleBorderPane.setOnMousePressed(event -> {
                xOffset[0] = stage.getX() - event.getScreenX();
                yOffset[0] = stage.getY() - event.getScreenY();
            });
            controller.titleBorderPane.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() + xOffset[0]);
                stage.setY(event.getScreenY() + yOffset[0]);
            });

            controller.setServer(server);

            //another stage configurations
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

            //close current stage
            Node currentSource = (Node) actionEvent.getSource();
            Stage currentStage = (Stage) currentSource.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            Dialogs.printErrorMessage(e.getMessage());
        }
    }

    public void signUp(javafx.event.ActionEvent actionEvent) {

        try {
            Seller seller = new Seller(emailText.getText(), passwordText.getText());
            Seller anotherSeller = server.findSellerByEmail(emailText.getText());
            if (anotherSeller != null)
                throw new KeyException("The email is already in use !");
            if (Objects.equals(passwordText.getText(), confirmPasswordText.getText())) {
                server.addSeller(seller);
                Dialogs.printConfirmation("You have successfully created an account", "Congratulation");
                openLogIn(actionEvent);
            } else
                throw new KeyException("Password confirmation is invalid !");
        } catch (Exception e) {
            Dialogs.printErrorMessage(e.getMessage());
        }
    }

    @FXML
    public void closeWindow() {
        Stage currentStage = (Stage) myAnchorPane.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    public void minimizeWindow() {
        Stage currentStage = (Stage) myAnchorPane.getScene().getWindow();
        currentStage.setIconified(true);
    }
}
