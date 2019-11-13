package ec.tec.ami.data.dao;

import android.os.AsyncTask;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ec.tec.ami.data.event.NotificationEvent;
import ec.tec.ami.data.event.PostEvent;
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

    public void getCommonFriendsCount(String email, String friend, final UserEvent event){
        User user = new User();
        user.setEmail(email);
        final User userFriend = new User();
        userFriend.setEmail(friend);

        DatabaseReference reference = database.getReference().child("users").child(user.getID()).child("friends");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                DatabaseReference reference2 = database.getReference().child("users").child(userFriend.getID()).child("friends");
                reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        if(!dataSnapshot.exists() || !dataSnapshot2.exists()){
                            event.onSuccess(0);
                            return;
                        }
                        Map<String, String> map1 = ( Map<String, String>)dataSnapshot.getValue();
                        Map<String, String> map2 = ( Map<String, String>)dataSnapshot2.getValue();
                        Set set = new HashSet(map1.keySet());
                        set.retainAll(map2.keySet());
                        event.onSuccess(set.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        event.onSuccess(0);
                        return;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                event.onSuccess(0);
                return;
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

    public void deleteAccount(final UserEvent event){
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseAuth.getInstance().getCurrentUser().delete()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("user").equalTo(email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<String> posts = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                posts.add(snapshot.getKey());
                            }

                            deletePost(posts, new PostEvent(){
                                @Override
                                public void onSuccess(boolean status) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            List<User> users = new ArrayList<>();
                                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                users.add(snapshot.getValue(User.class));
                                            }
                                            deleteFriends(email,users,new UserEvent(){
                                                @Override
                                                public void onSuccess() {
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            List<User> users = new ArrayList<>();
                                                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                                users.add(snapshot.getValue(User.class));
                                                            }
                                                            deleteNotifications(email,users,new UserEvent(){
                                                                @Override
                                                                public void onSuccess() {
                                                                    User user = new User();
                                                                    user.setEmail(email);
                                                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("users").child(user.getID());
                                                                    reference1.removeValue()
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

                                                                @Override
                                                                public void onFailure(Exception e) {
                                                                    event.onFailure(e);
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    event.onFailure(e);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    event.onFailure(e);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                    DeleteTask task1 = new DeleteTask(email,new UserEvent(){
//                        @Override
//                        public void onSuccess() {
//                            User user = new User();
//                            user.setEmail(email);
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getID());
//                            reference.removeValue()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    event.onSuccess();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    event.onFailure(e);
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onFailure(Exception e) {
//                            super.onFailure(e);
//                        }
//                    });
//
//                    task1.execute();
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

    public void deleteFriends(final String email, final List<User> friends, final UserEvent event){
        if(friends.size()==0){
            event.onSuccess();
            return;
        }
        User user  = new User();
        user.setEmail(email);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(friends.get(0).getID()).child("friends").child(user.getID());
        reference.removeValue()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    friends.remove(0);
                    deleteFriends(email,friends, new UserEvent(){
                        @Override
                        public void onSuccess() {
                            event.onSuccess();
                        }

                        @Override
                        public void onFailure(Exception e) {
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


    public void deleteNotifications(final String email, final List<User> friends, final UserEvent event){
        if(friends.size()==0){
            event.onSuccess();
            return;
        }
        User user  = new User();
        user.setEmail(email);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(friends.get(0).getID()).child("notifications").child(user.getID());
        reference.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friends.remove(0);
                            deleteNotifications(email,friends, new UserEvent(){
                                @Override
                                public void onSuccess() {
                                    event.onSuccess();
                                }

                                @Override
                                public void onFailure(Exception e) {
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



    public void deletePost(final List<String> posts, final PostEvent event){
        if(posts.size() == 0){
            event.onSuccess(true);
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts").child(posts.get(0));
        reference.removeValue()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    posts.remove(0);
                    deletePost(posts, new PostEvent(){
                        @Override
                        public void onSuccess(boolean status) {
                            event.onSuccess(true);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            event.onFailure(null);
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


    private static class DeleteTask extends AsyncTask<Void, Void, Boolean>{


        private String email;
        private UserEvent event;
        private Object lock;
        private Object lock2;

        public DeleteTask(String email, UserEvent event) {
            this.email = email;
            this.event = event;
            this.lock = new Object();
            this.lock2 = new Object();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            final boolean state[] = {true};
            synchronized (lock){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("posts");
                Query query = reference.orderByChild("user").equalTo(email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        synchronized (lock2){
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                DatabaseReference postReference = snapshot.getRef();
                                postReference.removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            state[0] = state[0] && true;
                                        }else{
                                            state[0] = false;
                                        }
                                        lock2.notify();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        state[0] = false;
                                        lock2.notify();
                                    }
                                });
                                try {
                                    lock2.wait();
                                } catch (InterruptedException e) {
                                    state[0] = false;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        state[0] = false;
                        lock.notify();
                    }
                });

                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    state[0] = false;
                }
            }

            synchronized (lock){
                final User user = new User();
                user.setEmail(email);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getID()).child("friends");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            final User friend = new User();
                            friend.setEmail(snapshot.getValue(String.class));
                            DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference().child("users").child(friend.getID()).child("friends").child(user.getID());
                            friendReference.removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("users").child(friend.getID()).child("notifications").child(user.getID());
                                        notificationReference.removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    state[0] = state[0] && true;
                                                }else{
                                                    state[0] = false;
                                                }
                                                lock2.notify();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                state[0] = false;
                                                lock2.notify();
                                            }
                                        });
                                    }else{
                                        state[0] = false;
                                        lock2.notify();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    state[0] = false;
                                    lock2.notify();
                                }
                            });
                        }

                        try {
                            lock2.wait();
                        } catch (InterruptedException e) {
                            state[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        state[0] = false;
                        lock.notify();
                    }
                });
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    state[0] = false;
                }
            }

            return state[0];

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                event.onSuccess();
            }else{
                event.onFailure(null);
            }
        }
    }

}
