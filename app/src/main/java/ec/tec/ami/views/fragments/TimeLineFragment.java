package ec.tec.ami.views.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostCursor;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;
import ec.tec.ami.views.activities.PostActivity;
import ec.tec.ami.views.adapters.PostAdapter;
import ec.tec.ami.views.utils.PaginationListener;

import static ec.tec.ami.views.utils.PaginationListener.PAGE_START;
import static ec.tec.ami.views.utils.PaginationListener.PAGE_SIZE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeLineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private EditText txtNewPost;
    private View view;
    private RecyclerView recyclerPosts;
    private SwipeRefreshLayout lytRefresh;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;

    private PostCursor cursor;

    public TimeLineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_time_line, container, false);
        txtNewPost = view.findViewById(R.id.txtNewPost);
        recyclerPosts = view.findViewById(R.id.recyclerPosts);
        lytRefresh = view.findViewById(R.id.lytRefresh);
        postAdapter = new PostAdapter(getContext(),posts);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerPosts.setAdapter(postAdapter);
        recyclerPosts.setHasFixedSize(true);
        recyclerPosts.setLayoutManager(linearLayoutManager);
        txtNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPost();
            }
        });


        lytRefresh.setOnRefreshListener(this);
        recyclerPosts.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                cursor.next();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        newCursor();
        return view;
    }

    private void newPost(){
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void newCursor(){
        String email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final User user = new User();
        user.setEmail(email);
        UserDAO.getInstance().listFriends(user,new UserEvent(){
            @Override
            public void onSuccess(List<User> users) {
                List<String> friends = new ArrayList<>();
                for(User u : users)friends.add(u.getEmail());
                cursor = new PostCursor(friends, FirebaseAuth.getInstance().getCurrentUser().getEmail(),PAGE_SIZE);
                cursor.setEvent(new PostCursor.PostEvent() {
                    @Override
                    public void onDataFetched(List<Post> posts) {
                        itemCount += posts.size();
                        for(int i=posts.size()-1;i>=0;i--){
                            TimeLineFragment.this.posts.add(posts.get(i));
                            postAdapter.notifyItemInserted(TimeLineFragment.this.posts.size()-1);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onEmptyData() {
                        isLastPage = true;
                        isLoading =false;
                    }
                });
//                cursor.next();
            }
        });

    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        postAdapter.clear();
    }
}
