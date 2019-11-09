package ec.tec.ami.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String name;
    private String lastNameA;
    private String lastNameB;
    private String profilePhoto;
    private List<Education> education;
    private Date birthDay;
    private String gender;
    private String city;
    private int telephone;
    private String email;
    private List<String> friends = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    public static String[] GENDERS = {"Female", "Male","Non-binary","Queer","Other"};

    public User() { }

    public User(String name, String lastNameA, String lastNameB, String profilePhoto, List<Education> education, Date birthDay, String gender, String city, int telephone, String email) {
        this.name = name;
        this.lastNameA = lastNameA;
        this.lastNameB = lastNameB;
        this.profilePhoto = profilePhoto;
        this.education = education;
        this.birthDay = birthDay;
        this.gender = gender;
        this.city = city;
        this.telephone = telephone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastNameA() {
        return lastNameA;
    }

    public void setLastNameA(String lastNameA) {
        this.lastNameA = lastNameA;
    }

    public String getLastNameB() {
        return lastNameB;
    }

    public void setLastNameB(String lastNameB) {
        this.lastNameB = lastNameB;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getID(){
        return getEmail().replace(".","_").replace("@","_");
    }

    public void addPost(Post post){
        posts.add(post);
    }
}
