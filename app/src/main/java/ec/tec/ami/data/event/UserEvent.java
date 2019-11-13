package ec.tec.ami.data.event;

import java.util.List;

import ec.tec.ami.model.User;

public class UserEvent {
    public void onSuccess(boolean state){};
    public void onSuccess(){};
    public void onFailure(Exception e){};
    public void onSuccess(List<User> users){};
    public void onSuccess(User user){};
    public void onSuccess (int count){}
}
