package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import static sample.Functions.*;


public class Controller {

    @FXML
    public Button managerButton;
    @FXML
    public TextField managerPass;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    @FXML
    private Button login;

    private int userType;
    private int userID;
    private String userEmail;
    private Database database=Database.getInstance();




    public void onSignup(ActionEvent event) throws  Exception{

        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Signup.fxml"));
        primaryStage.setTitle("SignUp Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public void onLogin(ActionEvent event) throws Exception{
        System.out.println(email.getText());
        System.out.println(password.getText());
        String emailID=email.getText();
        String passID=password.getText();
        if(!isValidTextField(email,"email")||!isValidTextField(password,"password"))
            return;

        userType= database.valid(emailID,passID);
        if(userType==0)
            infoBox("Please check email and password and try again",null,"Login Failed");

        else{
            userEmail=emailID;
            userID=database.getUserId(emailID);
            SignIn(event,userType);
        }
    }
    //loading the home view
    public void SignIn(ActionEvent event,int userType) throws  Exception{
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeMember.fxml"));
        Parent root = loader.load();
        HomeController homeController= loader.getController();
        homeController.transferUser(userID,userType,userEmail);
        primaryStage.setTitle("Home Page");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }




    public void revealManager() {
        if (managerPass.isVisible()) {
            managerPass.setVisible(false);
            managerButton.setVisible(false);
        } else {
            managerButton.setVisible(true);
            managerPass.setVisible(true);
            managerPass.requestFocus();
        }
    }
    //on click of the title "ProCompanion"
    public void adminMode(ActionEvent event) throws Exception{
        System.out.println(managerPass.getText());
        if(!(isValidTextField(managerPass,"Admin Pass")))
            return;
        String passID=managerPass.getText();

        userType= database.valid(null,passID);
        if(userType==0)
            infoBox("Please check email and password and try again",null,"Login Failed");

        else{
            userID=-1;
            SignIn(event,userType);
        }
    }


}
