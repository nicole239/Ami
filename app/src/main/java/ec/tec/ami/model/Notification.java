package ec.tec.ami.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String email;

    public Notification(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
