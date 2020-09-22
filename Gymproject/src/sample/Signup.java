package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import static sample.Functions.infoBox;
import static sample.Functions.isValidTextField;

public class Signup {

    @FXML
    private TextField fname;
    @FXML
    private TextField lname;
    @FXML
    private TextField emailS;
    @FXML
    private TextField profession;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField age;
    @FXML
    private PasswordField passwordS;
    @FXML
    private CheckBox checkbox;
    @FXML
    public Button createAccount;


    private Database database=Database.getInstance();
    public void initialize(){
        checkbox.setOnMouseClicked(e ->{
            if(checkbox.isSelected())
                profession.setDisable(false);
            else
                profession.setDisable(true);
        } );
    }

    public void createAccount (ActionEvent event)throws IOException{

        System.out.println(emailS.getText());
        System.out.println(passwordS.getText());
        System.out.println(fname.getText());
        System.out.println(lname.getText());
        System.out.println(phoneNumber.getText());

        //checking if fields are filled properly
        if(!(isValidTextField(fname,"first name")&&isValidTextField(lname,"last name")
                &&isValidTextField(emailS,"email")&&isValidTextField(passwordS,"password")
                &&isValidTextField(phoneNumber,"phone number")&&isValidTextField(age,"age")))
            return;
        if (checkbox.isSelected())
            if (!isValidTextField(profession,"proffesion"))
                return;
        String emailID=emailS.getText();
        String passwordID=passwordS.getText();
        String fnameID=fname.getText();
        String lnameID=lname.getText();
        String profess=profession.getText();
        Double pNumber = Double.valueOf(phoneNumber.getText());
        int ageID = Integer.valueOf(age.getText());
        try {

            if(!database.addAccount(emailID,passwordID,fnameID,lnameID,ageID,pNumber,profess,checkbox.isSelected()))
                return;

        }catch (SQLException e){}
        infoBox("You can login as a guest now.In order to join classes, your account needs to be verified by the manager","Account created successfully,proceeding to login","Success");

            Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            primaryStage.setTitle("SignUp Page");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

    }
    public void backToLogin(ActionEvent event)throws IOException{
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("SignUp Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
