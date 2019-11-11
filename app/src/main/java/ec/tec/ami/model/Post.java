package ec.tec.ami.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {

    private String user;
    private String description;
    private String media;
    private String id;
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
            int count = 0;
            for(int i=0;i<likes.size();i++)if(likes.get(i)!=null)count++;
            return count;
        }
        else{
            return 0;
        }
    }

    public int getTotalDislikes(){
        if(dislikes!=null) {
            int count = 0;
            for(int i=0;i<dislikes.size();i++)if(dislikes.get(i)!=null)count++;
            return count;
        }
        else{
            return 0;
        }
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addLike(String email){
        this.likes.add(email);
    }

    public void addDislike(String email){
        this.dislikes.add(email);
    }

    public boolean checkUserLike(String email){
        if(this.likes!=null){
            if(this.likes.contains(email)){
                return true;
            }
        }
        return false;

    }

    public boolean checkUserDislike(String email){
        if(this.dislikes!=null){
            if(this.dislikes.contains(email)){
                return true;
            }
        }
        return false;
    }
}
