package ec.tec.ami.views.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.User;
import ec.tec.ami.views.activities.DialogDecision;
import ec.tec.ami.views.adapters.UserAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmigosFragment extends Fragment implements UserAdapter.UserListener{

    RecyclerView listFriends;
    UserAdapter friendsAdapter;
    RecyclerView recyclerView;
    List<User> friends = new ArrayList<>();
    UserAdapter adapter;
    SwipeRefreshLayout lytRefresh;

    public AmigosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amigos, container, false);
        listFriends = view.findViewById(R.id.listFriends);
        recyclerView = view.findViewById(R.id.listFriends);
        adapter = new UserAdapter(getContext(),friends);
        adapter.setEditable(true);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        lytRefresh = view.findViewById(R.id.lytRefresh);
        lytRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFriends();
            }
        });
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
                lytRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Exception e) {
                lytRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDelete(final int position) {
        DialogDecision dialogDecision = new DialogDecision(getContext(), "Confirmation", "Do you want to delete this friend?", new DialogDecision.DialogResult() {
            @Override
            public void onConfirm() {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                final String friend = friends.get(position).getEmail();
                UserDAO.getInstance().deleteFriend(email, friend, new UserEvent(){
                    @Override
                    public void onSuccess() {
                        friends.remove(position);
                        adapter.notifyItemRemoved(position);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AmigosFragment.this.getContext(),"Could not delete friend",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
        dialogDecision.show();
    }
}
