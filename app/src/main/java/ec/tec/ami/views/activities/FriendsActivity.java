package ec.tec.ami.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Iterator;
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
    Switch btnSwitch;

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

        btnSwitch = findViewById(R.id.btnSwitch);

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    commonFriends();
                }else{
                    refreshFriends();
                }
            }
        });

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

    private void commonFriends(){
        String emailUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user = new User();
        user.setEmail(emailUser);
        UserDAO.getInstance().listFriends(user,new UserEvent(){
            @Override
            public void onSuccess(List<User> users) {
//                Iterator<User> friendIterator = friends.iterator();
//                while ((friendIterator.hasNext())){
//                    User friend = friendIterator.next();
//                    boolean found = false;
//                    Iterator<User> userIterator = users.iterator();
//                    while ((userIterator.hasNext())){
//                        User u = userIterator.next();
//                        if(friend.getEmail().equals(u.getEmail())){
//                            found = true;
//                            userIterator.remove();
//                            break;
//                        }
//                        if(!found){
//                            friendIterator.remove();
//                        }
//                    }
//                }
                for(User friend : new ArrayList<>(friends)){
                    boolean found = false;
                    for(User u : new ArrayList<>(users)){
                        if(friend.getEmail().equals(u.getEmail())){
                            found = true;
                            users.remove(u);
                            break;
                        }
                    }
                    if(!found){
                        friends.remove(friend);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }
}
