import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import network.FestivalServicesRpcProxy;
import service.IService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClient.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find client.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("festival.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("festival.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService server = new FestivalServicesRpcProxy(serverIP, serverPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ui/log-in.fxml"));
        Parent root = fxmlLoader.load();

        //controller initializations
        LogInController controller = fxmlLoader.getController();
        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];
        controller.titleBorderPane.setOnMousePressed(event -> {
            xOffset[0] = primaryStage.getX() - event.getScreenX();
            yOffset[0] = primaryStage.getY() - event.getScreenY();
        });
        controller.titleBorderPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset[0]);
            primaryStage.setY(event.getScreenY() + yOffset[0]);
        });
        controller.setServer(server);

        //another scene configurations
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }
}
