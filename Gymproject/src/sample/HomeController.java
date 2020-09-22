package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import static sample.Functions.*;

public class HomeController {

    @FXML
    private BorderPane mainView;
    private static int userID;
    private static String userEmail;
    private static int userType;
    @FXML
    private Button createActivity;
    @FXML
    private Button myStudents;
    @FXML
    private Button createClass;



    public void initialize(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Tips.fxml"));
            mainView.setCenter(loader.load());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangeView(ActionEvent event) {
        //get the button id inorder to know which view we need to load
        try {
            String menuItemID = ((Button)event.getSource()).getId();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(menuItemID + ".fxml"));
            mainView.setCenter(loader.load());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //functions for communication between controllers
    public void transferUser(int userID,int userType,String userEmail){
        System.out.println(userID);
        System.out.println(userType);
        if(userType==1) {
            createActivity.setVisible(false);
            myStudents.setVisible(false);
        }
        this.userType=userType;
        this.userID=userID;
        System.out.println("User ID at home Controller = "+ this.userID);
        if(userID<0) {
            createClass.setVisible(true);
            myStudents.setText("Members");
        }
        this.userEmail=userEmail;
    }
    public static String sendUserEmail(){
        return userEmail;
    }
    public static int sendUserId(){return userID;}
    public static int sendUserType(){return userType;}



    @FXML
    private void logout(ActionEvent event){
       if(confirm())
            try {
                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
                primaryStage.setTitle("Login Page");
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            }catch (IOException e){}



    }

}
