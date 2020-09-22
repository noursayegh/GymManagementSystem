package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import java.util.*;

public class Classes {
    @FXML
    private ListView<Class> listView;
    private ArrayList<Class> classes;
    @FXML
    private BorderPane mainView;
    private String userEmail;
    private int userId;

    public void initialize(){
        userId=HomeController.sendUserId();
        userEmail=HomeController.sendUserEmail();
        System.out.println(userEmail);
        Database database = Database.getInstance();
        classes= database.getClasses(userId);
        ObservableList<Class> observableList = FXCollections.observableList(classes);
        listView = new ListView<>(observableList);
        listView.setPrefSize(1620,880);
        listView.setCellFactory(studentListView -> new ClassListCell());

    mainView.setCenter(listView);


}
}
