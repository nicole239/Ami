package ec.tec.ami.data.dao;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.tec.ami.data.event.PostEvent;
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
}
