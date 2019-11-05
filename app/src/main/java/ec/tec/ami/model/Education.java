package ec.tec.ami.model;

import java.io.Serializable;
import java.util.Date;

public class Education implements Serializable {
    private Date date;
    private String institute;

    public Education() { }

    public Education(Date date, String institute) {
        this.date = date;
        this.institute = institute;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    @Override
    public String toString() {
        return  date + " - " + institute;
    }
}
