package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;

import static sample.Functions.confirm;
import static sample.Functions.infoBox;

public class MemberListCell extends ListCell<Member> {
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
    protected void updateItem(Member member, boolean empty) {
        super.updateItem(member, empty);

        if (empty || member == null) {

            setText(null);
            setGraphic(null);

        } else {
            FXMLLoader mLLoader = new FXMLLoader(getClass().getResource("listView.fxml"));
            mLLoader.setController(this);

            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error pt");
            }
            button.setVisible(true);
            button.setText("Delete");
            if(userId<0){
                System.out.println("userid at MemberListCell"+userId);
                button.setOnAction(event -> {
                    if(database.removeUserFromDatabase(member.getId(),1))
                        updateItem(member,true);});
            }
            else
                button.setOnAction(event ->  {
                    boolean flag = confirm();
                    if(flag)
                        database.removeStudentFromTraining(member.getId(),userId);
                    updateItem(member,flag);});


            text.setText(member.getFname()+" "+member.getLname());
            button2.setText("Info");
            button2.setOnAction(event -> infoBox(String.valueOf(member.getPhoneNumber()),member.getEmail(),String.valueOf(member.getId())));
            if (database.paidPT(member.getId(),userId,pane,button1,button2,button)) {
                button1.setText("Revoke");
                button1.setOnAction(event -> {database.setPaid(member.getId(),userId,0);updateItem(member,empty);});

            }else {
                button1.setText("Verify");
                button1.setOnAction(event ->  {database.setPaid(member.getId(),userId,1);updateItem(member,empty);});
            }

            setText(null);
            setGraphic(pane);

        }
    }
}
