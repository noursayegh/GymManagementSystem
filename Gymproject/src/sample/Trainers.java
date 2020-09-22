package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;


public class Trainers {
    @FXML
    private ListView<Coach> listView;
    private ArrayList<Coach> coaches;
    @FXML
    private BorderPane mainView;
    private String userEmail;
    private int userId;

    public void initialize(){

        userId=HomeController.sendUserId();
        userEmail=HomeController.sendUserEmail();
        System.out.println(userEmail);
        Database database = Database.getInstance();
        coaches= database.getCoaches(userId,userEmail);
        ObservableList<Coach> observableList = FXCollections.observableList(coaches);
        listView = new ListView<>(observableList);
        listView.setPrefSize(1620,880);
        listView.setCellFactory(studentListView -> new CoachListCell());
        mainView.setCenter(listView);


    }
}
