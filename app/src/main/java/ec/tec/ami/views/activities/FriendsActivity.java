package ec.tec.ami.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.User;
import ec.tec.ami.views.adapters.UserAdapter;

public class FriendsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<User> friends = new ArrayList<>();
    UserAdapter adapter;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

        setContentView(R.layout.activity_friends);

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

        recyclerView = findViewById(R.id.recyclerUser);
        adapter = new UserAdapter(this,friends);
        adapter.setEditable(false);;
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        email = getIntent().getStringExtra("email");

        refreshFriends();
    }

    private void refreshFriends(){
        User user = new User();
        user.setEmail(email);
        UserDAO.getInstance().listFriends(user,new UserEvent(){
            @Override
            public void onSuccess(List<User> users) {
                friends.clear();
                for(User u : users){
                    friends.add(u);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
