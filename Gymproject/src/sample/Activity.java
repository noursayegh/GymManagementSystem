package sample;


import java.sql.Date;

public class Activity {
    private Date start_date;
    private Date end_date;
    private String name;
    private int id;
    private String info;
    private String coach;
    private String verified;

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_time) {
        this.start_date = start_time;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_time) {
        this.end_date = end_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

}
