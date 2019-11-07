package ec.tec.ami.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    private String user;
    private String description;
    private String media;
    private Date date;
    private Type type;
    private List<String> likes;
    private List<String> dislikes;
    private List<Comment> comments;

    public Post(){}

    public Post(String user, String description, String media, Date date, Type type) {
        this.user = user;
        this.description = description;
        this.media = media;
        this.date = date;
        this.type = type;
        this.likes = new ArrayList<>();
        this.dislikes = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public Post(String user, String description, String media, Date date, Type type, List<String> likes, List<String> dislikes, List<Comment> comments) {
        this.user = user;
        this.description = description;
        this.media = media;
        this.date = date;
        this.type = type;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }

    public List<Comment> getComments() {
        if(comments!=null) {
            return comments;
        }
        else{
            return
                new ArrayList<Comment>();
        }
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getTotalLikes(){
        if(likes!= null) {
            return likes.size();
        }
        else{
            return 0;
        }
    }

    public int getTotalDislikes(){
        if(dislikes!=null) {
            return dislikes.size();
        }
        else{
            return 0;
        }
    }
}
