package ec.tec.ami.data.dao;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.data.event.PostEvent;
import ec.tec.ami.model.Comment;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;

public class PostDAO {

    private static PostDAO postDAO;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private PostDAO(){
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized PostDAO  getInstance(){
        if(postDAO == null){
            postDAO = new PostDAO();
        }
        return postDAO;
    }


    public void addPost(final Post post, final PostEvent event){
        User user = new User();
        user.setEmail(post.getUser());
        DatabaseReference reference = database.getReference().child("posts").push();//.child("users").child(user.getID()).child("posts").push();
        reference.setValue(post)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    event.onSuccess(post);
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

    public void addComment(String postID, final Comment comment, final PostEvent event){

        DatabaseReference reference = database.getReference().child("posts").child(postID).child("comentarios").push();
        reference.setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    event.onSuccess(true);
                }else{
                    event.onFailure(task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                event.onFailure(e);
            }
        });

    }

    public void likePost(final Post post, final PostEvent event){
        final DatabaseReference reference = database.getReference().child("posts").child(post.getId()).child("likes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    List<String> td = (ArrayList<String>) dataSnapshot.getValue();
                    post.setLikes(td);
                    for(int i=0; i<td.size();i++){
                        Log.d("Likes", td.get(i));
                    }

                    if(post.getLikes().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        event.onRepeated();
                    }
                    else{
                        post.addLike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        reference.setValue(post.getLikes())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            event.onSuccess(true);

                                        } else {
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

                }
                else{
                    post.setLikes(new ArrayList<String>());
                    post.addLike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    reference.setValue(post.getLikes())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        event.onSuccess(true);

                                    } else {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    
    public void dislikePost(final Post post, final PostEvent event){
        final DatabaseReference reference = database.getReference().child("posts").child(post.getId()).child("dislikes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()>0){
                    List<String> td = (ArrayList<String>) dataSnapshot.getValue();
                    post.setDislikes(td);
                    for(int i=0; i<td.size();i++){
                        Log.d("Dislikes", td.get(i));
                    }

                    if(post.getDislikes().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        event.onRepeated();
                    }
                    else{
                        post.addDislike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        reference.setValue(post.getDislikes())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            event.onSuccess(true);

                                        } else {
                                            event.onSuccess(false);

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

                }
                else{
                    post.setDislikes(new ArrayList<String>());
                    post.addDislike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    reference.setValue(post.getDislikes())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        event.onSuccess(true);

                                    } else {
                                        event.onSuccess(false);

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void deleteLike(final String email, final Post post, final PostEvent event){

        final DatabaseReference reference = database.getReference().child("posts").child(post.getId()).child("likes");
        Query query = reference.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    String user = (String)child.getValue();

                    if(user.equals(email)){
                        Log.d("hola", " pase por aqui");
                        reference.child(child.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    event.onSuccess(true);
                                }
                                else{
                                    event.onSuccess(false);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                event.onFailure(e);
                            }
                        });
                        Log.d("hola", child.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void deleteDislike(final String email, final Post post, final PostEvent event){

        final DatabaseReference reference = database.getReference().child("posts").child(post.getId()).child("dislikes");
        Query query = reference.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    String user = (String)child.getValue();

                    if(user.equals(email)){
                        Log.d("hola", " pase por aqui");
                        reference.child(child.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    event.onSuccess(true);
                                }
                                else{
                                    event.onSuccess(false);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                event.onFailure(e);
                            }
                        });
                        Log.d("hola", child.getKey());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
