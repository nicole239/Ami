package ec.tec.ami.views.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.User;
import ec.tec.ami.views.adapters.UserAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmigosFragment extends Fragment {

    RecyclerView listFriends;
    UserAdapter friendsAdapter;
    RecyclerView recyclerView;
    List<User> friends = new ArrayList<>();
    UserAdapter adapter;

    public AmigosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amigos, container, false);
        listFriends = view.findViewById(R.id.listFriends);
        recyclerView = view.findViewById(R.id.listFriends);
        adapter = new UserAdapter(getContext(),friends);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        refreshFriends();
        return view;
    }

    private void refreshFriends(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
