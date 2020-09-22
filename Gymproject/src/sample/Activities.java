package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;


public class Activities {
    @FXML
    private ListView<Activity> listView;
    private ArrayList<Activity> activities;
    @FXML
    private BorderPane mainView;
    private String userEmail=HomeController.sendUserEmail();
    private int userId=HomeController.sendUserId();


    public void initialize(){
        System.out.println("ID at activities"+userId);
        System.out.println(userEmail);
        Database database = Database.getInstance();
        activities= database.getActivities(userId);
        ObservableList<Activity> observableList = FXCollections.observableList(activities);
        listView = new ListView<>(observableList);
        listView.setPrefSize(1620,880);
        listView.setCellFactory(studentListView -> new ActivityListCell());

        mainView.setCenter(listView);


    }
}
