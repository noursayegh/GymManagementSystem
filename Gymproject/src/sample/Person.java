package sample;

public abstract class Person {

    private String fname;
    private String lname;
    protected String email;
    protected int age;
    protected int phoneNumber;

    public Person(){}

    public Person(String fname,String lname, String email, int age, int phoneNumber) {
        this.fname = fname;
        this.lname=lname;
        this.email = email;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String name) {
        this.fname = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }





}
