package ec.tec.ami.data.event;

import java.util.List;

import ec.tec.ami.model.Notification;
import ec.tec.ami.model.User;

public class NotificationEvent {
    public void onSuccess(boolean state){};
    public void onSuccess(){};
    public void onFailure(Exception e){};
    public void onSuccess(List<Notification> notifications){};
    public void onNotificationSent(){};
    public void onNotificationReceived(){};

}
