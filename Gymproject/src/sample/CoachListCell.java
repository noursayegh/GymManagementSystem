package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;

import static sample.Functions.infoBox;

public class CoachListCell extends ListCell<Coach> {

    @FXML
    private Text text;

    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button;

    @FXML
    private GridPane pane;
    private Database database=Database.getInstance();
    private int userId=HomeController.sendUserId();


    @Override
    protected void updateItem(Coach c, boolean empty) {
        super.updateItem(c, empty);

        if (empty || c == null) {

            setText(null);
            setGraphic(null);

        } else {

                FXMLLoader mLLoader = new FXMLLoader(getClass().getResource("listView.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            text.setText(c.getProfession());
            button2.setText("Info");
            button2.setOnAction(event -> infoBox(c.getFname()+ " " + c.getLname(),String.valueOf(c.getPhoneNumber()),String.valueOf(c.getEmail())));
            text.setText(c.getProfession());
            if(userId<0){
                button.setVisible(true);
                button.setText("Remove");
                button.setOnAction(event -> {boolean flag=database.removeUserFromDatabase(c.getId(),2); updateItem(c, flag);});
            }
            if (database.isStudentFor(userId,c.getId(),pane,button1,button2,button)) {
                button1.setOnAction(event ->  {database.removeStudentFromTraining(userId,c.getId());updateItem(c,empty);});
                button1.setText("Fire");

            }else {
                button1.setOnAction(event ->  {database.addStudentToTraining(userId,c.getId());updateItem(c,empty);});
                button1.setText("Hire");
            }
            setText(null);
            setGraphic(pane);

        }
    }
}
