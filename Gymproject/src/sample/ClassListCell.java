package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.io.IOException;
import static sample.Functions.infoBox;


public class ClassListCell extends ListCell<Class> {

    @FXML
    private Text text;

    @FXML
    private Button button1;
    @FXML
    private  Button button2;
    @FXML
    private Button button;

    @FXML
    private GridPane pane;

    private Database database=Database.getInstance();
    private int userId=HomeController.sendUserId();

    @Override
    protected void updateItem(Class c, boolean empty) {
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
            if(userId<0){
                button.setText("Delete");
                button.setVisible(true);
                button.setOnAction(event -> {
                    if(database.removeFromDatabase(c.getId(),0))
                        updateItem(c,true);});

            }
            button2.setText("Info");
            button2.setOnAction(event -> infoBox(c.getClassInfo(),"Class Name : "+c.getClassName()+"\nCoach : "+c.getCoach(),"Class ID = "+c.getId()));
            text.setText(c.getClassName());
            if (database.isStudentIn(c.getId(),userId,pane,button1,button2,button)) {
                button1.setOnAction(event ->  {database.removeStudentFromClass(userId,c.getId());updateItem(c,empty);});
                if(userId>0)
                    button1.setText("Leave");
                else
                    button1.setText("Close");

            }else {
                button1.setOnAction(event ->  {database.addStudentToClass(userId,c.getId());updateItem(c,empty);});
                if(userId>0)
                    button1.setText("Join");
                else
                    button1.setText("Open");
            }
            setText(null);
            setGraphic(pane);

        }
    }

}
