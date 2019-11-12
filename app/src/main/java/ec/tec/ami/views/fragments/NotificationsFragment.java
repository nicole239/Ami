package ec.tec.ami.views.fragments;


import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import ec.tec.ami.data.dao.NotificationDAO;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.NotificationEvent;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Notification;
import ec.tec.ami.model.User;
import ec.tec.ami.views.adapters.NotificationAdapter;
import ec.tec.ami.views.adapters.UserAdapter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment implements NotificationAdapter.NotificationListener {

    RecyclerView recyclerView;
    List<Notification> notifications = new ArrayList<>();
    NotificationAdapter adapter;
    SwipeRefreshLayout refreshLayout;


    public NotificationsFragment() {

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if(direction == ItemTouchHelper.LEFT){
                notifications.remove(position);
                adapter.notifyItemRemoved(position);
            }

            if(direction == ItemTouchHelper.RIGHT){
                notifications.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightBackgroundColor(ContextCompat.getColor(NotificationsFragment.this.getContext(),R.color.colorPrimary))
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(NotificationsFragment.this.getContext(),R.color.colorPrimary))
                .addSwipeLeftActionIcon(R.drawable.ic_sentiment_dissatisfied_black_48dp)
                .addSwipeRightActionIcon(R.drawable.ic_sentiment_satisfied_black_48dp)
                .create()
                .decorate();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.listNotification);
        adapter = new NotificationAdapter(getContext(), notifications, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        refreshLayout = view.findViewById(R.id.lytRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFriends();
            }
        });
//        itemTouchHelper.attachToRecyclerView(recyclerView);
        refreshFriends();
        return view;
    }


    private void refreshFriends(){
        notifications.clear();
        adapter.notifyDataSetChanged();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user = new User();
        user.setEmail(email);
        NotificationDAO.getInstance().getNotifications(user, new NotificationEvent(){
            @Override
            public void onSuccess(List<Notification> notifications) {
                for(Notification notification : notifications){
                    NotificationsFragment.this.notifications.add(notification);
                }
                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(NotificationsFragment.this.getContext(),"Could not load the notifications", Toast.LENGTH_LONG).show();
            }
        });
//        for(String str : new String[]{"kahho@gmail.com","steven.moya.quinones@gmail.com"}){
//            Notification notification = new Notification();
//            notification.setEmail(str);
//            notifications.add(notification);
//            adapter.notifyDataSetChanged();
//        }
//        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        User user = new User();
//        user.setEmail(email);
//        UserDAO.getInstance().listFriends(user,new UserEvent(){
//            @Override
//            public void onSuccess(List<User> users) {
//                friends.clear();
//                for(User u : users){
//                    friends.add(u);
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public void onAccept(final int position) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        UserDAO.getInstance().addFriends(email,notifications.get(position).getEmail(),new UserEvent(){
            @Override
            public void onSuccess() {
                refreshLayout.setRefreshing(true);
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                User user = new User();
                user.setEmail(email);
                NotificationDAO.getInstance().removeNotification(user, notifications.get(position), new NotificationEvent(){
                    @Override
                    public void onSuccess() {
                        notifications.remove(position);
                        adapter.notifyItemRemoved(position);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        notifications.remove(position);
                        adapter.notifyItemRemoved(position);
                        refreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(NotificationsFragment.this.getContext(),"Could not add the friend",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReject(final int position) {
        refreshLayout.setRefreshing(true);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        User user = new User();
        user.setEmail(email);
        NotificationDAO.getInstance().removeNotification(user, notifications.get(position), new NotificationEvent(){
            @Override
            public void onSuccess() {
                notifications.remove(position);
                adapter.notifyItemRemoved(position);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(NotificationsFragment.this.getContext(),"Could not reject the notification :(", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }
}
