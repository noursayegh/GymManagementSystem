package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Date;
import java.time.LocalDate;

import static sample.Functions.infoBox;


public class CreateActivity {

    @FXML
    private TextField name;
    @FXML
    private TextField activityId;
    @FXML
    private TextArea info;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private Button createButton;
    @FXML
    private CheckBox edit;
    private String userEmail;
    private Activity a;
    private int userId;
    private Database database=Database.getInstance();


    public void initialize(){
        userEmail=HomeController.sendUserEmail();
        userId=HomeController.sendUserId();
        if(userId<0)
            createButton.setText("Create");
        edit.setOnMouseClicked(event -> {
            if(edit.isSelected()){
                createButton.setText("Update");
                activityId.setDisable(false);

            }
            else {
                activityId.setDisable(true);
                createButton.setText("Create");
            }
        });
        activityId.textProperty().addListener((observable, oldValue, newValue) -> {
            a=database.getActivityToEdit(Integer.valueOf(newValue));
            name.setText(a.getName());
            info.setText(a.getInfo());
            startDate.setValue(LocalDate.parse(a.getStart_date().toString()));
            endDate.setValue(LocalDate.parse(a.getEnd_date().toString()));
        });
    }
    public void SubmitRequest(ActionEvent event){


        //validating entered information
        String message = "Activity Added Successfully,now it needs to get approved to show on the Activities tab";
        String activityName = name.getText().trim();
        String activityInfo = info.getText();
        Date start_date=null;
        Date end_date=null;
        if(startDate.getValue()!=null)
            start_date = Date.valueOf(startDate.getValue());
        if (endDate.getValue()!=null)
            end_date = Date.valueOf(endDate.getValue());

        if(activityName.isEmpty()){
            infoBox("Please enter a valid name!",null,"Attention");
            return;
        }

        if(start_date==null||end_date==null||start_date.after(end_date)) {
            infoBox("Check Dates!!", null, "Attention");
            return;
        }



            boolean flag=false;
        try {
            if(edit.isSelected())
                flag = database.addActivity(activityName, activityInfo, start_date, end_date, userId,edit.isSelected(),a.getId());
            else
                flag = database.addActivity(activityName, activityInfo, start_date, end_date, userId,edit.isSelected(),0);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(flag){
            if(!edit.isSelected()) {
                if (userId < 0)
                    message = "Activity has been created and verified";

                infoBox(message,
                        null, "Success");
            }
            else
                infoBox("Activity updated Successfully,now it needs to be verified again",
                        null, "Success");
            name.setText("");
            info.setText("");
        }
        else
            infoBox("Sth went wrong," +
                            "Make sure you have filled all the fields and try again",
                            null, "Failure!");

    }
}
