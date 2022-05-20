import domain.Artist;
import domain.Seller;
import domain.Show;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.IObserver;
import service.IService;
import utilsUI.Dialogs;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public class MainController implements IObserver {

    private Seller seller;
    private IService server;

    public void setServer(IService iService)
    {
        server = iService;
    }

    @FXML
    public AnchorPane myAnchorPane;
    @FXML
    public BorderPane titleBorderPane;
    @FXML
    public ListView<Label> allShows;
    @FXML
    public ListView<String> dayList;
    @FXML
    public DatePicker datePicker;
    @FXML
    public TextField buyerName;
    @FXML
    public TextField numberOfSeats;

    public MainController() {

    }

    public void showAllShows() throws Exception {
        allShows.getItems().clear();
        for(Artist artist: server.getAllArtists())
        {
            for(Show show: server.findShowsByArtistId(artist.getId())){
                Label text = new Label();
                text.setText(artist.getName() + "|" + LocalDate.ofInstant(Instant.ofEpochMilli(show.getDate()), ZoneOffset.UTC)+ "|" + show.getPlace() + "|" + (show.getTotalSeats()-show.getSoldSeats()) + "|" + show.getTotalSeats());
                if(show.getSoldSeats() == show.getTotalSeats())
                    text.setStyle("-fx-background-color: red");
                allShows.getItems().add(text);
            }
        }
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
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

    @FXML
    public void logOut(ActionEvent actionEvent) {

        if (Objects.equals(Dialogs.printConfirmation("Do you want to log out ?", "Log out"), "Yes")) {
            //close current stage
            Stage currentStage = (Stage) myAnchorPane.getScene().getWindow();
            currentStage.close();

            //open log in stage
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("ui/log-in.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();

                //controller initializations
                LogInController controller = loader.getController();
                final double[] xOffset = new double[1];
                final double[] yOffset = new double[1];
                controller.titleBorderPane.setOnMousePressed(event1 -> {
                    xOffset[0] = stage.getX() - event1.getScreenX();
                    yOffset[0] = stage.getY() - event1.getScreenY();
                });
                controller.titleBorderPane.setOnMouseDragged(event1 -> {
                    stage.setX(event1.getScreenX() + xOffset[0]);
                    stage.setY(event1.getScreenY() + yOffset[0]);
                });

                controller.setServer(server);
                server.logout(seller, this);

                //another scene configurations
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.initStyle(StageStyle.UNDECORATED);
                stage.show();

            } catch (IOException e) {
                Dialogs.printErrorMessage(e.getMessage());
            } catch (Exception e) {
                Dialogs.printErrorMessage(e.getMessage());
            }
        }
    }

    @FXML
    public void searchByDay(ActionEvent actionEvent) throws Exception {
        if(datePicker.getValue() == null)
            Dialogs.printErrorMessage("Nu este selectata nici o data");
        else
        {
            dayList.getItems().clear();
            for(Show show: server.findShowsByDay(datePicker.getValue()))
                dayList.getItems().add(server.findArtistById(show.getIdArtist()).getName() + "|" + show.getPlace() + "|" + LocalDateTime.ofInstant(Instant.ofEpochMilli(show.getDate()), ZoneOffset.UTC).getHour() + "|" + (show.getTotalSeats() - show.getSoldSeats()));
        }
    }

    @FXML
    public void buyTickets() throws Exception {
        if(allShows.getSelectionModel().getSelectedItem() == null)
            Dialogs.printErrorMessage("Nu este selectat nici un show");
        else{
            int indexItem = allShows.getSelectionModel().getSelectedIndex();
            int freeSeats = Integer.parseInt(allShows.getItems().get(indexItem).getText().split("\\|")[3]);
            if(freeSeats < Integer.parseInt(numberOfSeats.getText()))
                Dialogs.printErrorMessage("Nu exista atatea locuri disponibile");
            else
            {
                String[] s = allShows.getItems().get(indexItem).getText().split("\\|");
                Show show = server.findShowByDateAndPlace(s[1], s[2]);
                String[] numberOfSoldSeats = allShows.getItems().get(indexItem).getText().split("\\|");
                show.setSoldSeats(Integer.parseInt(numberOfSoldSeats[4]) - Integer.parseInt(numberOfSoldSeats[3]) + Integer.parseInt(numberOfSeats.getText()));
                server.updateShow(show);
                showAllShows();
            }
        }
    }

    @Override
    public void ticketBought() {
        System.out.println("Ajung sa intru in runLater");
        Platform.runLater(()->{
            System.out.println("Intru in runLater");
            try {
                showAllShows();
            } catch (Exception e) {
                Dialogs.printErrorMessage(e.getMessage());
            }
        });
    }
}
