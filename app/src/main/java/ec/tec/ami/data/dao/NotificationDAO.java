package ec.tec.ami.data.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.data.event.NotificationEvent;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Notification;
import ec.tec.ami.model.User;

public class NotificationDAO {

    private static NotificationDAO notificationDAO;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private NotificationDAO(){
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized NotificationDAO  getInstance(){
        if(notificationDAO == null){
            notificationDAO = new NotificationDAO();
        }
        return notificationDAO;
    }


    public void getNotifications(final User user, final NotificationEvent event){
        final List<Notification> notifications = new ArrayList<>();
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/notifications");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0){
                    if(dataSnapshot.exists()){
                        Notification notification = new Notification();
                        notification.setEmail(dataSnapshot.getValue(String.class));
                        notifications.add(notification);
                    }

                }
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Notification notification = new Notification();
                    notification.setEmail(snapshot.getValue(String.class));
                    notifications.add(notification);
                }
                event.onSuccess(notifications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onFailure(databaseError.toException());
            }
        });
    }

    public void removeNotification(User user, Notification notification, final NotificationEvent event){
        final User userFriend = new User();
        userFriend.setEmail(notification.getEmail());
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/notifications").child(userFriend.getID());

        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   event.onSuccess();
                }else{
                    event.onFailure(task.getException());
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                event.onFailure(e);
            }
        });
    }

    public void checkNotification(String email1, String email2, final NotificationEvent event){
        User user1 = new User();
        user1.setEmail(email1);
        User user2 = new User();
        user2.setEmail(email2);
        DatabaseReference reference = database.getReference().child("users/"+user2.getID()+"/notifications").child(user1.getID());
        final DatabaseReference reference2 = database.getReference().child("users/"+user1.getID()+"/notifications").child(user2.getID());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    event.onNotificationSent();
                }else{
                    reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                event.onNotificationReceived();;
                            }else{
                                event.onFailure(null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            event.onFailure(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onFailure(null);
            }
        });

    }
}
