import domain.Seller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.Objects;

public class LogInController {

    private IService server;

    public void setServer(IService iService)
    {
        server = iService;
    }

    @FXML
    public AnchorPane myAnchorPane;
    @FXML
    public Button logInButton;
    @FXML
    public TextField emailText;
    @FXML
    public PasswordField passwordText;
    @FXML
    public BorderPane titleBorderPane;


    public void runAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/log-in.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        } catch (Exception e) {
            Dialogs.printErrorMessage(e.getMessage());
        }
    }

    public void runUser(Seller seller) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));
            Parent root = loader.load();

            //controller initialization
            MainController controller = loader.getController();

            //server
            server.login(seller, controller);

            //controller initializations
            Stage stage = new Stage();
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
            controller.setSeller(seller);
            controller.showAllShows();


            //another scene configurations
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

            //close current stage
            Stage currentStage = (Stage) myAnchorPane.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            Dialogs.printErrorMessage(e.getMessage());
        }
    }

    @FXML
    public void logIn() {
        String email = emailText.getText();
        String password = passwordText.getText();
        Seller seller = new Seller(email, password);

        if (Objects.equals(email, "a") && Objects.equals(password, "a"))
            runAdmin();
        else
            runUser(seller);
    }

    @FXML
    public void openSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/sign-up.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();

            //controller initializations
            SignUpController controller = loader.getController();
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

            //another scene configurations
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

            //close current stage
            Stage currentStage = (Stage) myAnchorPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
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
