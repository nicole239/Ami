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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostCursor;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.dao.filter.FriendsFilter;
import ec.tec.ami.data.dao.filter.PostFilter;
import ec.tec.ami.data.dao.filter.UserFilter;
import ec.tec.ami.data.dao.filter.WordFilter;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;
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
        if(view != null){
            return  view;
        }
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
//        if(post.getType()== Type.PHOTO){

        lytRefresh.setOnRefreshListener(this);
        recyclerPosts.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override

            protected void loadMoreItems() {
                lytRefresh.setRefreshing(true);
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
        final PostFilter postFilter = new PostFilter(PostFilter.FilterType.OR);
        final String email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final User user = new User();
        user.setEmail(email);
        UserDAO.getInstance().listFriends(user,new UserEvent(){
            @Override
            public void onSuccess(List<User> users) {
                postFilter.addFilter(new FriendsFilter(users));
                postFilter.addFilter(new UserFilter(email));
                cursor = new PostCursor(null, FirebaseAuth.getInstance().getCurrentUser().getEmail(),PAGE_SIZE);
                cursor.setFilter(postFilter);
                cursor.setEvent(new PostCursor.PostEvent() {
                    @Override
                    public void onDataFetched(List<Post> posts) {
                        lytRefresh.setRefreshing(false);
                        isLoading = false;

//                        for(int i=posts.size()-1;i>=0;i--){
//                            TimeLineFragment.this.posts.add(posts.get(i));
//                            postAdapter.notifyItemInserted(itemCount++);
//                        }
                        for(Post post : posts){
                            TimeLineFragment.this.posts.add(post);
                            postAdapter.notifyItemInserted(itemCount++);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }

                    @Override
                    public void onEmptyData() {
                        lytRefresh.setRefreshing(false);
                        isLastPage = true;
                        isLoading =false;
                    }
                });
                cursor.next();
                currentPage++;
                isLastPage = false;
                isLoading = true;
            }
        });

    }



    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        postAdapter.clear();
        postAdapter.notifyDataSetChanged();
        newCursor();
    }
}
