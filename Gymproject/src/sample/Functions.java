package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import java.util.Optional;
import java.util.regex.Pattern;

public class Functions {
    public static boolean infoBox(String message, String header, String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(header);
        Optional<ButtonType> result = alert.showAndWait();
        return (result.isPresent()&&result.get() == ButtonType.OK);
    }

    public static boolean isValidTextField(TextField t,  String s){
        if( t.getText().isEmpty() || ( s.equals("email") && !isValidEmail( t.getText()))) {
            infoBox("Please enter your "+s+" correctly !!", null,"Error" );
            t.requestFocus();
            return false;
        }
        if(s.equals("age"))
            if(!(Integer.valueOf(t.getText())<100&&Integer.valueOf(t.getText())>0)){
                infoBox("please enter a valid age",null,"Attention");
                return false;
            }

        if(s.equals("phone number"))
            if(!(t.getText().matches("[0-9]{8}"))) {
                infoBox("please enter a valid phone number", null, "Attention");
                return false;
            }
        if(s.equals("password"))
           if(!(t.getText().matches(".{4,16}"))){
               infoBox("please enter a valid password(4-16characters)",null,"Attention");
           }
        return true;
    }
    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return  ((email == null) || pat.matcher(email).matches()) ;
    }
    public static boolean confirm(){
        return infoBox(null,"Are you sure?","Confirm");
    }
}
