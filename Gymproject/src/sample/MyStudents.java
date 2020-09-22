package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MyStudents {
    @FXML
    private ListView<Member> listView;
    private ArrayList<Member> students;
    @FXML
    private BorderPane mainView;
    @FXML
    private Text text;
    private String userEmail;
    private int userId;

    public void initialize(){

        userId=HomeController.sendUserId();
        userEmail=HomeController.sendUserEmail();
        if(userId<0)
            text.setText("Members");
        System.out.println(userEmail);
        Database database = Database.getInstance();
        students= database.getStudentsById(userId);
        ObservableList<Member> observableList = FXCollections.observableList(students);
        listView = new ListView<>(observableList);
        listView.setPrefSize(1620,880);
        listView.setCellFactory(studentListView -> new MemberListCell());
        mainView.setCenter(listView);

    }

}
