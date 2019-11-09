package ec.tec.ami.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostCursor;
import ec.tec.ami.data.dao.UserCursor;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.dao.filter.PostFilter;
import ec.tec.ami.data.dao.filter.WordFilter;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.User;
import ec.tec.ami.views.adapters.PostAdapter;
import ec.tec.ami.views.adapters.UserAdapter;
import ec.tec.ami.views.utils.PaginationListener;

import static ec.tec.ami.views.utils.PaginationListener.PAGE_START;
import static ec.tec.ami.views.utils.PaginationListener.PAGE_SIZE;


public class BusquedaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Button btnSearch;
    private EditText txtSearch;
    private Spinner spinnerType;
    private View view;

    private List<User> users = new ArrayList<>();
    private LinearLayoutManager layoutManagerUser;

    private List<Post> posts = new ArrayList<>();
    private LinearLayoutManager layoutManagerPost;

    private PaginationListener paginationListenerUsers;
    private PaginationListener paginationListenerPosts;

    private boolean isLoading = false;
    private int itemCount = 0;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;

    private UserAdapter userAdapter;
    private PostAdapter postAdapter;

    private RecyclerView recyclerViewUsers;

    private SwipeRefreshLayout lytRefresh;

    private UserCursor userCursor;
    private PostCursor postCursor;

    private boolean isPost;

    public BusquedaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_busqueda, container, false);
        btnSearch = view.findViewById(R.id.btnSearch);
        txtSearch = view.findViewById(R.id.txtSearch);
        spinnerType = view.findViewById(R.id.spinnerType);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearch();
            }
        });

        List<String> types = Arrays.asList(new String[]{"Post","User"});
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), R.layout.simple_black_spinner,types);
        typeAdapter.setDropDownViewResource(R.layout.simple_black_spinner_dropdown);
        spinnerType.setAdapter(typeAdapter);


        layoutManagerUser = new LinearLayoutManager(getContext());
        listeners();

        lytRefresh = view.findViewById(R.id.lytRefresh);
        lytRefresh.setOnRefreshListener(this);

        userAdapter = new UserAdapter(getContext(), users);
        postAdapter = new PostAdapter(getContext(), posts);

        recyclerViewUsers = view.findViewById(R.id.recyclerUser);

        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(layoutManagerUser);
        recyclerViewUsers.addOnScrollListener(paginationListenerUsers);



        return view;
    }

    private void listeners(){
        paginationListenerUsers = new PaginationListener(layoutManagerUser) {
            @Override
            protected void loadMoreItems() {
                lytRefresh.setRefreshing(true);
                isLoading = true;
                currentPage++;
                if(isPost)
                    postCursor.next();
                else
                    userCursor.next();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        };
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        isLastPage = false;
        userAdapter.clear();
    }


    private void onSearch(){
        isPost = spinnerType.getSelectedItem().equals("Post");
        String value = txtSearch.getText().toString();
        if(!value.trim().isEmpty())
            btnSearch.setEnabled(false);
            if(isPost) {
                recyclerViewUsers.setAdapter(postAdapter);
                newPostCursor(value);
            }
            else {
                recyclerViewUsers.setAdapter(userAdapter);
                newCursor(value);
            }
    }

    private void newCursor(String word){
        userAdapter.clear();
        postAdapter.clear();
        userCursor = new UserCursor(10, word);
        userCursor.setEvent(new UserCursor.UserCursoEvent() {
            @Override
            public void onDataFetched(List<User> users) {
                lytRefresh.setRefreshing(false);
                btnSearch.setEnabled(true);
                isLoading = false;
                for(int i =  users.size()-1; i >=0 ;i--){
                    BusquedaFragment.this.users.add(users.get(i));
                    userAdapter.notifyItemInserted(itemCount++);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onEmptyData() {
                btnSearch.setEnabled(true);
                lytRefresh.setRefreshing(false);
                isLastPage = true;
                isLoading =false;
            }
        });
        itemCount = 0;
        userCursor.next();
        currentPage++;
        isLoading = true;
    }

    private void newPostCursor(final String word){
        userAdapter.clear();
        postAdapter.clear();
        final PostFilter postFilter = new PostFilter(PostFilter.FilterType.OR);
        final User u = new User();
        u.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        UserDAO.getInstance().listFriends(u, new UserEvent(){
            @Override
            public void onSuccess(List<User> users) {
//                postFilter.addFilter(new FriendsFilter(users));
//                postFilter.addFilter(new UserFilter(u.getEmail()));
                postFilter.addFilter(new WordFilter(word));
                postCursor = new PostCursor(null, FirebaseAuth.getInstance().getCurrentUser().getEmail(),2);
                postCursor.setFilter(postFilter);
                postCursor.setEvent(new PostCursor.PostEvent() {
                    @Override
                    public void onDataFetched(List<Post> posts) {
                        lytRefresh.setRefreshing(false);
                        isLoading = false;

//                        for(int i=posts.size()-1;i>=0;i--){
//                            BusquedaFragment.this.posts.add(posts.get(i));
//                            postAdapter.notifyItemInserted(itemCount++);
//                        }
                        for(Post post : posts){
                            BusquedaFragment.this.posts.add(post);
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
                itemCount = 0;
                postCursor.next();
                currentPage++;
                isLastPage = false;
                isLoading = true;
            }
        });
    }
}
