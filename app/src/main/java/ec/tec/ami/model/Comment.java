package ec.tec.ami.model;

import java.util.Date;
public class Comment {
    private User user;
    private Date date;
    private String comment;

    public Comment(){
        this.user=null;
        this.date=null;
        this.comment="";
    }

    public Comment(User user, Date fecha, String comentario) {
        this.user = user;
        this.date = fecha;
        this.comment = comentario;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User usuario) {
        this.user = usuario;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
