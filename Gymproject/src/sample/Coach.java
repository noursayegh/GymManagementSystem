package sample;


public class Coach extends Person {

    private int id;
    private String profession;


    public Coach(String fname,String lname, String email, int age, int phoneNumber) {
        super(fname,lname, email, age, phoneNumber);
    }
    public Coach(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString(){ return email;}


    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
