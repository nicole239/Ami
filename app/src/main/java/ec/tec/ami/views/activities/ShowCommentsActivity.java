package ec.tec.ami.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostDAO;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.PostEvent;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Comment;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;
import ec.tec.ami.views.adapters.CommentAdapter;

public class ShowCommentsActivity extends AppCompatActivity {

    ListView listComments;
    private Post post;
    private EditText userCommentDetail;
    Button userCommentSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        setContentView(R.layout.activity_show_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        userCommentSend = findViewById(R.id.userCommentSend);
        userCommentDetail = findViewById(R.id.userCommentDetail);
        listComments = findViewById(R.id.listPostComments);

        Intent intent = getIntent();
        String postID = intent.getStringExtra("ID");
        post = new Post();
        post.setId(postID);
        userCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newComment();
            }
        });
        loadComments();
    }


    private void newComment(){
        UserDAO.getInstance().getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail(), new UserEvent(){
            @Override
            public void onSuccess(User user) {
                user.setFriends(null);
                user.setNotifications(null);
                user.setEducation(null);
                user.setPosts(null);
                user.setTelephone(0);
                user.setCity(null);
                user.setGender(null);
                user.setBirthDay(null);
                String detalle = userCommentDetail.getText().toString();
                Date date = Calendar.getInstance().getTime();
                Comment comment = new Comment(user, date,detalle);
                String id = post.getId();

                PostDAO.getInstance().addComment(id,comment,new PostEvent(){
                    @Override
                    public void onSuccess(boolean status) {
                        if(status){
                            Toast.makeText(ShowCommentsActivity.this,"Added Comment",Toast.LENGTH_LONG).show();
                            userCommentDetail.setText("");

                        }
                        else{
                            Toast.makeText(ShowCommentsActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ShowCommentsActivity.this,"Failure to add comment",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void loadComments(){
        FirebaseDatabase firebaseDatabasee = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabasee.getReference().child("posts").child(post.getId()).child("comentarios");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<Comment>();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    comments.add(child.getValue(Comment.class));
                }
                Log.d("hola", "numero es: "+comments.size());
                CommentAdapter adapter = new CommentAdapter(ShowCommentsActivity.this, comments);
                listComments.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
