package ec.tec.ami.data.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.data.event.NotificationEvent;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.User;

public class UserDAO {
    private static UserDAO userDAO;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private UserDAO(){
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized UserDAO  getInstance(){
        if(userDAO == null){
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    public void loginWithEmail(String email, String password, final  UserEvent event){
        auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
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

    public void loginWithGoogle(GoogleSignInAccount account, final UserEvent event){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    event.onSuccess();
                }else{
                    event.onFailure(null);
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

    public void checkEmail(String email, final UserEvent event){
        DatabaseReference reference = database.getReference().child("users");
        Query query = reference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    event.onSuccess(true);
                }else{
                    event.onSuccess(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onFailure(databaseError.toException());
            }
        });
    }

    public void listFriends(final User user, final UserEvent event){
        final List<User> friends = new ArrayList<>();
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/friends");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0){
                    if(dataSnapshot.exists()){
                        User friend = new User();
                        friend.setEmail(dataSnapshot.getValue(String.class));
                        friends.add(friend);
                    }

                }
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User friend = new User();
                    friend.setEmail(snapshot.getValue(String.class));
                    friends.add(friend);
                }
                event.onSuccess(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onFailure(databaseError.toException());
            }
        });
    }

    public void getUser(String email, final UserEvent event){
        final User user = new User();
        user.setEmail(email);
        DatabaseReference reference = database.getReference().child("users/"+user.getID());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
//                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User newUser = dataSnapshot.getValue(User.class);
                        event.onSuccess(newUser);
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserType(String email, final UserEvent event){
        final User user = new User();
        user.setEmail(email);
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/type");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User.Type type = dataSnapshot.getValue(User.Type.class);
                    User newUser = new User();
                    newUser.setType(type);
                    event.onSuccess(newUser);
                }else{
                    event.onFailure(new Exception("No data found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onFailure(databaseError.toException());
            }
        });

    }

    public void getUserPhoto(String email, final UserEvent event){
        final User user = new User();
        user.setEmail(email);
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/profilePhoto");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User newUser = new User();
                    newUser.setProfilePhoto(dataSnapshot.getValue(String.class));
                    event.onSuccess(newUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addFriends(final String email, final String friend, final UserEvent event){
        final User user = new User();
        user.setEmail(email);
        final User userFriend = new User();
        userFriend.setEmail(friend);
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/friends").child(userFriend.getID());
        reference.setValue(friend)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference reference2 = database.getReference().child("users/"+userFriend.getID()+"/friends").child(user.getID());
                    reference2.setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void deleteFriend(final String email, String friend, final UserEvent event){
        final User user = new User();
        user.setEmail(email);
        final User userFriend = new User();
        userFriend.setEmail(friend);
        DatabaseReference reference = database.getReference().child("users/"+user.getID()+"/friends").child(userFriend.getID());
        final DatabaseReference reference2 = database.getReference().child("users/"+userFriend.getID()+"/friends").child(user.getID());

        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void addNotification(final String email, final String friend, final UserEvent event){
        final User user = new User();
        user.setEmail(email);
        final User userFriend = new User();
        userFriend.setEmail(friend);
        DatabaseReference reference = database.getReference().child("users/"+userFriend.getID()+"/notifications").child(user.getID());
        reference.setValue(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void checkFriend(String email1, String email2, final UserEvent event){
        User user1 = new User();
        user1.setEmail(email1);
        User user2 = new User();
        user2.setEmail(email2);
        DatabaseReference reference = database.getReference().child("users/"+user2.getID()+"/friends").child(user1.getID());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    event.onSuccess(true);
                }else {
                    event.onSuccess(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onFailure(null);
            }
        });

    }


}
