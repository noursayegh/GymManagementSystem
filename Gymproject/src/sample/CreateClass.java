package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static sample.Functions.infoBox;


public class CreateClass {

    @FXML
    private TextField name;
    @FXML
    private TextField fee;
    @FXML
    private TextArea info;
    @FXML
    private TextField coachName;
    @FXML
    private TextField classId;
    @FXML
    private Button createButton;
    @FXML
    private CheckBox edit;
    private String userEmail;
    private int userId;
    private Class c;
    private Database database=Database.getInstance();

    public void initialize(){
        userEmail=HomeController.sendUserEmail();
        userId=HomeController.sendUserId();
        if(userId<0)
            createButton.setText("Create");
        edit.setOnMouseClicked(event -> {
            if(edit.isSelected()){
                classId.setDisable(false);
                createButton.setText("Update");

            }
            else {
                classId.setDisable(true);
                createButton.setText("Create");
            }
        });
        classId.textProperty().addListener((observable, oldValue, newValue) -> {
            c=database.getClassToEdit(Integer.valueOf(newValue));
            name.setText(c.getClassName());
            info.setText(c.getClassInfo());
            fee.setText(c.getFee());
            coachName.setText(c.getCoach());
        });
    }
    public void Create(ActionEvent event){
        String className = name.getText().trim();
        String classInfo = info.getText().trim();
        String feeString = fee.getText().trim();
        String coach = coachName.getText().trim();

        if(className.isEmpty()){
            infoBox("Please enter a valid name!",null,"Attention");
            return;
        }

        boolean flag=false;
        try {
            if(edit.isSelected())
                flag = database.addClass(className,feeString,coach,classInfo,edit.isSelected(),c.getId());
            else
                flag = database.addClass(className,feeString,coach,classInfo,edit.isSelected(),0);
        }catch (Exception e){}
        if(flag){
            if(!edit.isSelected())
                infoBox("Class created Successfully" ,
                    null, "Success");
            else
                infoBox("Class Updated Successfully" ,
                        null, "Success");
            name.setText("");
            info.setText("");
            fee.setText("");
            coachName.setText("");
        }
        else
            infoBox("Sth went wrong," +
                            "Make sure you have filled all the fields and try again",
                    null, "Failure!");

    }
}
