package sample;
import javafx.geometry.Insets;

import javafx.scene.control.Button;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;


import static sample.Functions.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Database {
    private static Database database;
    private static Connection connection;
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/db?verifyServerCertificate=false&useSSL=true";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "1475369";
    private static final String SELECT_COACHES = "SELECT * FROM coach";
    private static final String SELECT_PRIVATE_TRAINING = "SELECT * FROM private_training where cid = ?";
    private static final String MANAGER_QUERY="SELECT * FROM manager where password = ?";
    private static final String SELECT_MEMBER_BY_ID = "SELECT * FROM member where id=?";
    private static final String SELECT_QUERY_CLASS = "SELECT * FROM class";
    private static final String SELECT_QUERY_ACTIVITY = "SELECT * from ongoing_activities";
    private static final String INSERT_QUERY = "INSERT into member (id,email, fname, lname,age,phoneNumber) values (?,?,?,?,?,?)";
    private static final String INSERT_QUERY_COACH = "INSERT into coach (id,email, fname, lname,age,phoneNumber,profession) values (?,?,?,?,?,?,?)";
    private static final String INSERT_QUERY_ACTIVITY = "INSERT into activity (name,info , start_date, end_date,cid) values (?,?,?,?,?)";

    //private constructor inorder to make a singleton database connection
    private Database() {
        try {

            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public static Database getInstance() {
        if (database == null)
            database = new Database();
        return database;
    }






    //create new account
    public boolean addAccount(String email, String password, String fname, String lname,int age,Double phoneNumber, String profession, boolean coachFlag)  throws SQLException{

        String query=INSERT_QUERY;
        if (coachFlag)
            query = INSERT_QUERY_COACH;

        //verify that the email isn't already found in database
        if (getUserId(email)!=0) {
            infoBox("User already has an account", null, "Error");
            return false;
        }



        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            //generate a unique id for our new user

            //in case of error return
            int id=createID(email,password,coachFlag);
            if(id==0)
                return false;
            preparedStatement.setInt(1,id);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, fname);
            preparedStatement.setString(4, lname);
            preparedStatement.setInt(5, age);
            preparedStatement.setDouble(6, phoneNumber);
            if(coachFlag)
                preparedStatement.setString(7, profession);
            System.out.println(preparedStatement);
            preparedStatement.execute();


        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }
        return true;
    }
    public boolean editAccount(int id,String email, String newPassword, String fname, String lname,int age,Double phoneNumber, String profession, boolean coachFlag,boolean passChange,String oldPassword)throws SQLException{


        if(passChange){
            if(valid(email,oldPassword)==0) {
                infoBox("Please check your password", null, "Wrong Password");
                return false;
            }
            if(oldPassword.equals(newPassword)){
                infoBox("New password can't be the same as your old password!",null,"Error");
            return false;
            }
            String user = "ids";
            if (id==-1)
                user = "manager";
            try {
                PreparedStatement preparedStatement=connection.prepareStatement("UPDATE "+user+" set password=? where id=?");
                preparedStatement.setInt(1,newPassword.hashCode());
                preparedStatement.setInt(2,id);
                System.out.println(preparedStatement);
                preparedStatement.execute();
                return true;
            }catch (SQLException e){
                printSQLException(e);
                infoBox("Sth went wrong," +
                                "Make sure you have filled all the fields and try again",
                        null, "Failure!");
                return false;
            }
        }

        //check if the new email is used by another user
        //returns 0 in case email wasn't found,and id otherwise
        int tempID=getUserId(email);
        if (tempID!=id&&tempID!=0) {
            infoBox("Email is used by another account",null,"Attention");
            return false;
        }

        String user = " member";
        String query = "";
        String temp="SET fname=?,lname=?,email=?,phoneNumber=?,age=?";
        if(coachFlag) {
            user = "coach";
            temp+=",profession=?";
        }

        query="update "+user+" "+temp+" where id = ?";


        try {
            //we need a transaction in order to make sure the email is updated in the table of ids too
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,fname);
            preparedStatement.setString(2,lname);
            preparedStatement.setString(3,email);
            preparedStatement.setDouble(4,phoneNumber);
            preparedStatement.setInt(5,age);
            if(coachFlag) {
                preparedStatement.setString(6,profession);
                preparedStatement.setInt(7,id);
            }else
                preparedStatement.setInt(6,id);
            System.out.println(preparedStatement);
            preparedStatement.execute();
            //check if the email was modified ,the we need to update it in ids table too
            if(tempID==0){
                PreparedStatement preparedStatement1 = connection.prepareStatement("UPDATE ids SET email = ? WHERE id = ?");
                preparedStatement1.setString(1,email);
                preparedStatement1.setInt(2,id);
                System.out.println(preparedStatement1);
                preparedStatement1.execute();
            }

            connection.commit();
            connection.setAutoCommit(true);
           return true;

        } catch (SQLException e) {
            printSQLException(e);
            connection.rollback();
            infoBox("Sth went wrong," +
                            "Make sure you have filled all the fields and try again",
                    null, "Failure!");
            return false;
        }
    }





    //valid checks if the email entered matches the password && returns the user type {-1 for manager , 1 for member,
    // 2 for coach , 0 for fail or not found}
    public int valid(String email, String password) throws SQLException {
        if(email!=null)
            return validQuery(email, password);
        return validQueryManager(password,MANAGER_QUERY);
    }

    public int validQuery(String email, String password)throws SQLException{
        CallableStatement cStmt = connection.prepareCall("{? = call validate_login(?,?)}");
        cStmt.registerOutParameter(1, Types.INTEGER);
        cStmt.setString(2, email);
        cStmt.setInt(3,password.hashCode());
        System.out.println(cStmt);
        cStmt.execute();
        int outputValue = cStmt.getInt(1);
        return outputValue+1;
    }

    public int validQueryManager( String password, String query)  {
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1,password.hashCode());

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                    return -1;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return 0;
    }





    public Activity getActivityToEdit(int activityID){
        try (
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM activity where id = ?")) {
            preparedStatement.setInt(1,activityID);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return makeObjActivity(resultSet);

        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    public Class getClassToEdit(int classID){
        try (
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM class where id = ?")) {
            preparedStatement.setInt(1,classID);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
               return makeObjClass(resultSet);

        } catch (SQLException e) {
            printSQLException(e);
        }
    return null;
    }






    public void printSQLException(SQLException ex) {
        for (Throwable e : ex)
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
    }





    //returns all found classes in database
    public ArrayList<Class> getClasses(int id) {
        String query = SELECT_QUERY_CLASS;
        if(id>0)
            query = "SELECT * FROM open_classes";
        ArrayList<Class> classes = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                classes.add(makeObjClass(resultSet));

        } catch (SQLException e) {
            printSQLException(e);
        }
        return classes;
    }
    //returns verfied and still ongoing activities for normal users
    //returns all activities to manager
    public ArrayList<Activity> getActivities(int userId) {
        ArrayList<Activity> activities = new ArrayList<>();
        String query="SELECT * FROM activity";
        if(userId>0)
            query=SELECT_QUERY_ACTIVITY;
        try (

                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                activities.add(makeObjActivity(resultSet));
            }


        } catch (SQLException e) {
            printSQLException(e);
        }
        return activities;
    }
    //same as getActivities
    public ArrayList<Coach> getCoaches(int cid,String userEmail) {
        ArrayList<Coach> coaches = new ArrayList<>();
        String query = "SELECT * FROM verified_coaches";
        if(cid<0)
            query = SELECT_COACHES;
        Coach c ;
        try (

                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                c = makeObjCoach(resultSet);
                if(!(c.getId()==cid && c.getEmail().equals(userEmail)))
                    coaches.add(c);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return coaches;
    }
    //returns coach's students
    //if manager ,returns all members in the gym
    public ArrayList<Member> getStudentsById(int userID) {
        ArrayList<Member> students = new ArrayList<>();
        if(userID>0) {
            try (
                    PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRIVATE_TRAINING)) {
                preparedStatement.setInt(1, userID);
                System.out.println(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(SELECT_MEMBER_BY_ID)) {
                        preparedStatement2.setInt(1, resultSet.getInt("sid"));
                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        while (resultSet2.next())
                            students.add(makeObjMember(resultSet2));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

            } catch (SQLException e) {
                printSQLException(e);
            }
        }
        else try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT *FROM member");
            System.out.println(preparedStatement);
            ResultSet rs=preparedStatement.executeQuery();
            while (rs.next())
                students.add(makeObjMember(rs));
        }catch (SQLException e){e.printStackTrace();}

        System.out.println(students);
        return students;
    }








    //returns the id of the coach that created the activity
    public int getActivityCoach(int id){
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT *FROM activity where id=?");
            preparedStatement.setInt(1,id);
            System.out.println(preparedStatement);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getInt("cid");
        }catch (SQLException e){printSQLException(e);}
        return 0;
    }

    public int getUserId(String email) {

        try (

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from ids where email=?")) {

            preparedStatement.setString(1, email);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("id");


        } catch (SQLException e) {
            printSQLException(e);
            return 0;
        }
        return 0;
    }






    public boolean isStudentIn(int classid, int sid,GridPane pane,Button button1,Button button2,Button button) {
        setColor(pane,button1,button2,button,false);
        System.out.println(sid);
        String query = "SELECT * FROM class where id =? and open = true ";
        if(sid>0)
            query= "SELECT * FROM students_classes where classid =?  AND sid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, classid);
            if(sid>0)
                preparedStatement.setInt(2, sid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                setColor(pane,button1,button2,button,true);
                return true;
            }
        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }
        return false;
    }

    public void addStudentToClass(int sid, int classid) {
        String query = "INSERT into students_classes (classid, sid) values (?,?)";
        if(sid<0)
        query = "UPDATE class set open = 1 where id = ?";
        else if(!isVerified(sid)){
            infoBox("You need to be verified to perform this action",null,"Error");
            return;
        }
       executeQuery(query,classid,sid);
    }

    public void removeStudentFromClass(int sid, int classid) {
        String query = "DELETE from students_classes where classid=? and sid=?";
        if (sid < 0)
            query = "UPDATE class SET open = false where id = ?";
        executeQuery(query,classid,sid);
    }






    public void addStudentToTraining(int sid, int cid) {
        String query;
        if(sid>0) {
            query = "INSERT into private_training (sid, cid) values (?,?)";
            if(!isVerified(sid)){
                infoBox("You need to be verified to perform this action",null,"Error");
                return;
            }
        }
        else
            query ="UPDATE ids SET verified = 1 where id= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if(sid<0)
                preparedStatement.setInt(1, cid);
            else{
                preparedStatement.setInt(1, sid);
                preparedStatement.setInt(2, cid);

            }


            System.out.println(preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            printSQLException(e);

        }
    }

    public void removeStudentFromTraining(int sid, int cid) {

        String query = "DELETE from private_training where cid=? and sid=?";
        if(sid<0)
            query = "UPDATE ids SET verified = 0 where id= ?";

        executeQuery(query,cid,sid);
    }

    public boolean isStudentFor(int sid, int cid, GridPane pane, Button button1, Button button2 ,Button button) {
       setColor(pane,button1,button2,button,false);
       String query = "SELECT * FROM private_training where cid =? AND sid = ?";
       if(sid<0)
           query="SELECT *FROM ids where id = ? and verified =1 ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                query)) {
            if(sid>0)
                preparedStatement.setInt(2, sid);
            preparedStatement.setInt(1, cid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                setColor(pane,button1,button2,button,true);
                return true;

            }
        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }

        return false;
    }







    public boolean isParticipantIn(int sid,int aid,GridPane pane, Button button1,Button button2,Button button){
        setColor(pane,button1,button2,button,false);
        String query ="SELECT * FROM activity_participants where sid =? AND aid = ?" ;
        if(sid<0)
            query = "SELECT * FROM activity where verified = ? and id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                query)) {
            if(sid<0)
                preparedStatement.setInt(1,1);
            else
                preparedStatement.setInt(1, sid);
            preparedStatement.setInt(2, aid);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                setColor(pane,button1,button2,button,true);
                return true;
            }
        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }

        return false;
    }

    public void removeStudentFromActivity(int sid,int aid){
        String query = "DELETE from activity_participants where sid=? and aid=?";
        if(sid<0)
            query = "UPDATE activity set verified=? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                query)) {
            if(sid<0)
                preparedStatement.setInt(1, 0);
            else
                preparedStatement.setInt(1, sid);
            preparedStatement.setInt(2,aid);
            System.out.println(preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            printSQLException(e);

        }
    }

    public void addStudentToActivity(int sid,int aid){
        String query;
        if (sid>0) {
            if(!isVerified(sid)){
                infoBox("You need to be verified to perform this action",null,"Error");
                return;
            }
            query = "INSERT into activity_participants (sid, aid) values (?,?)";
        }
        else
            query = "UPDATE activity set verified = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if(sid<0)
                preparedStatement.setInt(1,1);
            else
                preparedStatement.setInt(1,sid);
            preparedStatement.setInt(2,aid);
            System.out.println(preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            printSQLException(e);

        }
    }






    // sets the color of the listview cell according to the data given
    private void setColor(GridPane pane,Button button1,Button button2,Button button,boolean flag){
        if (flag){
            if(button!=null)
                button.setBackground(new Background(new BackgroundFill(Color.rgb(0, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
            button1.setBackground(new Background(new BackgroundFill(Color.rgb(0, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
            button2.setBackground(new Background(new BackgroundFill(Color.rgb(0, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        else {
            if(button!=null)
                button.setBackground(new Background(new BackgroundFill(Color.rgb(145, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
            pane.setBackground(new Background(new BackgroundFill(Color.rgb(145, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
            button1.setBackground(new Background(new BackgroundFill(Color.rgb(145, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
            button2.setBackground(new Background(new BackgroundFill(Color.rgb(145, 145, 145), CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }







    public boolean paidPT(int sid,int cid,GridPane pane,Button button1,Button button2,Button button){
        setColor(pane,button1,button2,button,false);
        String query = "SELECT * FROM private_training where sid =? AND cid = ? AND paid = 1 ";
        if(cid<0)
            query="SELECT * FROM ids where id =? AND verified = 1 ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sid);
            if(cid>0)
                preparedStatement.setInt(2, cid);
            System.out.println(preparedStatement);
            System.out.println(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                setColor(pane,button1,button2,button,true);
                return true;
            }
        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }

        return false;
    }

    public void setPaid(int sid,int cid,int paid){
        String query= "UPDATE private_training SET paid = ? WHERE sid =? AND cid =?";
        if(cid<0)
            query="UPDATE ids set verified = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1,paid);
            preparedStatement.setInt(2, sid);
            if(cid>0)
                preparedStatement.setInt(3, cid);

            System.out.println(preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            printSQLException(e);

        }
    }
    //deletes selected class or activity from database
    public boolean removeFromDatabase(int id,int choice){
        if(!confirm())
            return false;
        try {
            CallableStatement callableStatement = connection.prepareCall("CALL deleteFromDatabase(?,?)");
            callableStatement.setInt(1, id);
            callableStatement.setInt(2,choice);
            System.out.println(callableStatement);
            callableStatement.execute();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return true;
    }
    //deletes selected user from database
    public boolean removeUserFromDatabase(int id,int choice) {
        if(!confirm())
           return false;
            try {
                CallableStatement callableStatement = connection.prepareCall("CALL deleteUser(?,?)");
                callableStatement.setInt(1, id);
                callableStatement.setInt(2,choice);
                System.out.println(callableStatement);
                callableStatement.execute();
            } catch (SQLException e) {
                printSQLException(e);

            }
       return true;
    }
    //creates a unique id for the user in ids table and returns it
    public int createID(String email,String password,boolean coachFlag){
        int id=getUserId(email);
        if (id!=0)
            return id;
        try(PreparedStatement preparedStatement=connection.prepareStatement("INSERT into ids(email,password,coach) values (?,?,?)")){
            preparedStatement.setString(1,email);
            preparedStatement.setInt(2,password.hashCode());
            preparedStatement.setBoolean(3,coachFlag);
            System.out.println(preparedStatement);
            preparedStatement.execute();
        }catch (SQLException e){printSQLException(e);}
        return getUserId(email);
    }

    public boolean addClass(String name, String fee, String coachName, String info,Boolean flag,int id) {
        String query = "INSERT into class(cname, info, fee, coachName) values (?,?,?,?)";
        if(flag)
            query = "UPDATE class set cname=?,info=?,fee=?,coachName=? where id=?";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, info);
            preparedStatement.setString(3, fee);
            preparedStatement.setString(4, coachName);
            if(flag)
                preparedStatement.setInt(5,id);
            System.out.println(preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }
        return true;

    }
    public boolean addActivity(String name, String info, Date start_date, Date end_date, int cid,boolean flag,int aid) {
        String query=INSERT_QUERY_ACTIVITY;
        if(cid>0&&!isVerified(cid)){
                infoBox("You need to be verified to perform this action",null,"Error");
                return false;
        }
        if(flag)
            if(cid==getActivityCoach(aid))
                query="UPDATE activity set name=?,info=?,start_date=?,end_date=?,verified=0 where id=?";
            else return false;
        if(!flag&&cid<0)
            query="INSERT into activity(name, info, start_date, end_date,cid, verified) values (?,?,?,?,-1,1)";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, info);
            preparedStatement.setDate(3, start_date);
            preparedStatement.setDate(4, end_date);
            if (!flag) {
                if (cid > 0)
                    preparedStatement.setInt(5, cid);
            }else
                preparedStatement.setInt(5,aid);

            System.out.println(preparedStatement);
            preparedStatement.execute();


        } catch (SQLException e) {
            printSQLException(e);
            return false;
        }
        return true;

    }
    public Person getUserInfo(int id,int choice){

        String query = "SELECT * FROM member where id=?";
        if (choice==2)
            query = "SELECT * FROM coach where id=?";
        try(PreparedStatement preparedStatement=connection.prepareStatement(query)){
            preparedStatement.setInt(1,id);
            System.out.println(preparedStatement);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next())
                if(choice==1)
                    return makeObjMember(rs);

        }catch (SQLException e){printSQLException(e);}

        return null;
    }
    public String getProfession(int id){
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT profession FROM coach where id = ?")){
            preparedStatement.setInt(1,id);
            System.out.println(preparedStatement);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next())
               return rs.getString("profession");

        }catch (SQLException e){printSQLException(e);}
        return null;
    }
    // makeObj takes a result set , analyses its data and transforms it into Desired object

    private Class makeObjClass(ResultSet rs){
        Class c=new Class();
        try {

            String name = rs.getString("cname");
            String info = rs.getString("info");
            String coachName = rs.getString("coachName");
            String fee = rs.getString("fee");
            int id = rs.getInt("id");
            c.setId(id);
            c.setClassName(name);
            c.setClassInfo(info);
            c.setCoach(coachName);
            c.setFee(fee);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return c;
    }
    private boolean checkActivityOutdated(int activityId){
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM activity where id = ? and end_date<CURRENT_DATE ")){
            preparedStatement.setInt(1,activityId);
            System.out.println(preparedStatement);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next())
                return true;

        }catch (SQLException e){printSQLException(e);}
        return false;
    }


    private Activity makeObjActivity(ResultSet resultSet){
        Activity activity = new Activity();
        try {
            String name = resultSet.getString("name");
            String info = resultSet.getString("info");
            int id = resultSet.getInt("id");
            java.sql.Date start_date = resultSet.getDate("start_date");
            java.sql.Date end_date = resultSet.getDate("end_date");
            if(checkActivityOutdated(id))
                name = "*outdated*"+name;
            activity.setName(name);
            activity.setInfo(info);
            activity.setId(id);
            activity.setStart_date(start_date);
            activity.setEnd_date(end_date);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return activity;
    }

    private Coach makeObjCoach(ResultSet resultSet) {
        Coach coach = new Coach();
        try {

            String fname = resultSet.getString("fname");
            String lname = resultSet.getString("lname");
            String profession = resultSet.getString("profession");
            String email = resultSet.getString("email");
            int id = resultSet.getInt("id");
            int phoneNumber = resultSet.getInt("phoneNumber");
            int age = resultSet.getInt("age");
            coach.setAge(age);
            coach.setPhoneNumber(phoneNumber);
            coach.setFname(fname);
            coach.setLname(lname);
            coach.setProfession(profession);
            coach.setEmail(email);
            coach.setId(id);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return coach;
    }

    private Member makeObjMember(ResultSet resultSet) {
        Member member= new Member();
        try {

            String fname = resultSet.getString("fname");
            String lname = resultSet.getString("lname");
            int id = resultSet.getInt("id");
            String email = resultSet.getString("email");
            int phoneNumber = resultSet.getInt("phoneNumber");
            int age = resultSet.getInt("age");
            member.setAge(age);
            member.setPhoneNumber(phoneNumber);
            member.setFname(fname);
            member.setLname(lname);
            member.setEmail(email);
            member.setId(id);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return member;
    }
    private boolean isVerified(int id){
        try(PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM ids where id=? and verified = true ")){
            preparedStatement.setInt(1,id);

            System.out.println(preparedStatement);
            ResultSet rs=preparedStatement.executeQuery();
            if(rs.next())
                return true;

        }catch (SQLException e){printSQLException(e);}
        return false;
    }

// wrote this function to avoid code duplicate
    private void executeQuery(String query,int cid,int sid){
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, cid);
            if (sid>0)
                preparedStatement.setInt(2, sid);
            System.out.println(preparedStatement);
            preparedStatement.execute();

        } catch (SQLException e) {
            printSQLException(e);

        }
    }
}

