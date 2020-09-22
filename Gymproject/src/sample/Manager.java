package sample;

import java.util.ArrayList;

public class Manager extends Person {

    private int managerId;

    private ArrayList<Person> members;
    private ArrayList<Person> coaches;


    public Manager(String fname,String lname, String email, int age, int phoneNumber) {
        super(fname,lname,email, age, phoneNumber);
    }

    public int getManagerId() {
        return managerId;
    }

    public ArrayList<Person> getMembers() {
        return members;
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void removeMember(Member member) {
        members.remove(member);
    }

    public ArrayList<Person> getCoaches() {
        return coaches;
    }

    public void addCoach(Coach coach) {
        coaches.add(coach);
    }

    public void removeCoach(Coach coach) {
        coaches.remove(coach);
    }

    public void viewSchedule(){}

    public void viewEquipment(){}

    public void viewActivities(){}
}
