package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;

import static sample.Functions.infoBox;

public class ActivityListCell extends ListCell<Activity> {
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
    protected void updateItem(Activity activity, boolean empty) {
        super.updateItem(activity, empty);

        if (empty || activity == null) {

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
            if(userId<0){
                button.setVisible(true);
                button.setText("Delete");
                button.setOnAction(event -> {
                    if(database.removeFromDatabase(activity.getId(),2))
                        updateItem(activity,true);});
            }

            text.setText(activity.getName());
            button2.setText("Info");
            button2.setOnAction(event -> infoBox(activity.getInfo(),"Start date : "+activity.getStart_date()+ "\nEnd date : " + activity.getEnd_date(),"Activity ID = "+activity.getId()));
            if (database.isParticipantIn(userId,activity.getId(),pane,button1,button2,button)) {
                button1.setOnAction(event ->  {database.removeStudentFromActivity(userId,activity.getId());updateItem(activity,empty);});
                if(userId>0)
                    button1.setText("Cancel");
                else
                    button1.setText("Revoke");
            }else {
                button1.setOnAction(event ->  {database.addStudentToActivity(userId,activity.getId());updateItem(activity,empty);});
                if(userId>0)
                    button1.setText("Going");
                else
                    button1.setText("Verify");
            }
            setText(null);
            setGraphic(pane);


            setText(null);
            setGraphic(pane);

        }
    }
}
