package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import static sample.Functions.infoBox;
import static sample.Functions.isValidTextField;


public class EditProfile {

    @FXML
    private TextField fname;
    @FXML
    private TextField lname;
    @FXML
    private TextField email;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField age;
    @FXML
    private TextField oldPass;
    @FXML
    private TextField newPass;
    @FXML
    private TextField profession;
    @FXML
    private Text profText;
    @FXML
    private GridPane info;
    @FXML
    private Button apply;
    @FXML
    private CheckBox changePass;




    private String userEmail;
    private int userType;
    private Person p;
    private int userId;
    private Database database=Database.getInstance();
    private static String emailID="";
    private static String passwordID="";


    public void initialize(){

        //Getting user information
        userEmail=HomeController.sendUserEmail();
        userId=HomeController.sendUserId();
        userType=HomeController.sendUserType();

        //if the user is the manager
        if(userId<0){
            info.setVisible(false);
            changePass.setSelected(true);
            changePass.setDisable(true);
            apply.setOnAction(event -> {
                //check if the fields were filled properly
                if(!(isValidTextField(oldPass,"password")&&isValidTextField(newPass,"password")))
                    return;
                try {
                    if(database.editAccount(userId,null,newPass.getText().trim(),null,null,0,null,null,false,true,oldPass.getText().trim()))
                        infoBox("Password changed successfully",null,"Success");
                }catch (Exception e){
                    e.printStackTrace();
                    infoBox("Something wewnt wrong",null,"Error");
                }
            });
        }
        else {
            //fill fields with user information
            p=database.getUserInfo(userId,userType);
            fname.setText(p.getFname());
            lname.setText(p.getLname());
            email.setText(p.getEmail());
            phoneNumber.setText(String.valueOf(p.getPhoneNumber()));
            age.setText(String.valueOf(p.getAge()));
            if(userType==2){
                profText.setVisible(true);
                profession.setVisible(true);
                profession.setText(database.getProfession(userId));
            }
        }



        changePass.setOnMouseClicked(event -> {
            if(changePass.isSelected())
                info.setDisable(true);
            else
                info.setDisable(false);
        });


    }
    public void Apply(ActionEvent event){
        //check if fields were filled properly
        if(!(isValidTextField(fname,"first name")&&isValidTextField(lname,"last name")
            &&isValidTextField(email,"email")&&isValidTextField(phoneNumber,"phone number")
            &&isValidTextField(age,"age")&&(userType!=2||isValidTextField(profession,"profession"))))
                return;
        if(changePass.isSelected()&&(!isValidTextField(oldPass,"password")||!isValidTextField(newPass,"password")))
            return;



        String fnameID = fname.getText().trim();
        String lnameID = lname.getText().trim();
        emailID = email.getText().trim();
        Double phoneNumberID = Double.valueOf(phoneNumber.getText().trim());
        int ageID = Integer.valueOf(age.getText().trim());
        String professionID = "";
        passwordID = newPass.getText().trim();
        //if the user is a coach
        if (userType==2)
            professionID=profession.getText().trim();



        //flag to get the result of editing
        boolean flag=false;
        try {
            flag = database.editAccount(userId,emailID,passwordID,fnameID,lnameID,ageID,phoneNumberID,professionID,userType==2,changePass.isSelected(),oldPass.getText().trim());
        }catch (Exception e){e.printStackTrace();}
        if(flag)
                infoBox("Changes Done Successfully",
                        null, "Success");

    }
}
