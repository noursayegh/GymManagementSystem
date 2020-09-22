package sample;


public class Member extends Person {

    private int id;

    public Member(String fname,String lname, String email, int age, int phoneNumber) {
        super(fname,lname, email, age, phoneNumber);
    }
    public Member(){}

    public int getId() {
        return id;
    }
    public void setId(int id){this.id=id;}



}
