package ec.tec.ami.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostCursor;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.dao.filter.UserFilter;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Education;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;
import ec.tec.ami.model.User;
import ec.tec.ami.views.activities.CreateAccountActivity;
import ec.tec.ami.views.activities.EditAccountActivity;
import ec.tec.ami.views.activities.LoginActivity;
import ec.tec.ami.views.adapters.GalleryAdapter;
import ec.tec.ami.views.adapters.PostAdapter;
import ec.tec.ami.views.utils.PaginationListener;

import static ec.tec.ami.views.utils.PaginationListener.PAGE_SIZE;
import static ec.tec.ami.views.utils.PaginationListener.PAGE_START;


public class PerfilFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    RelativeLayout layoutPerfilInfo;
    private boolean showingInfo = false;
    TextView txtName, txtEmail, txtBirthday, txtGender, txtCity, txtPhone;
    ListView listEducation;
    RecyclerView listPosts, listPhotos,  galleryPosts;
    PostAdapter postAdapter;
    ImageView imgUser;
    private RelativeLayout galleryLayout;
    private User user;
    private LinearLayoutManager linearLayoutManager;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;
    private PostCursor cursor;
    private GalleryAdapter galleryAdapter;

    private SwipeRefreshLayout lytRefresh;

    Button btnPerfilUpdate;

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");


    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(view != null){
            return view;
        }

        view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        layoutPerfilInfo =  view.findViewById(R.id.layoutPerfilInfo);
        layoutPerfilInfo.setVisibility(View.GONE);

        txtName = view.findViewById(R.id.txtPerfilName);
        txtEmail = view.findViewById(R.id.txtCorreo);
        txtBirthday = view.findViewById(R.id.txtPerfilBirthday);
        txtGender = view.findViewById(R.id.txtPerfilGender);
        txtCity = view.findViewById(R.id.txtPerfilCity);
        txtPhone = view.findViewById(R.id.txtPerfilPhone);
        listEducation  = view.findViewById(R.id.listPerfilEducation);
        listPosts = view.findViewById(R.id.listPerfilPosts);
        listPhotos = view.findViewById(R.id.photoGallery);
        imgUser = view.findViewById(R.id.imgPerfilPicture);
        galleryLayout = view.findViewById(R.id.PhotoLayout);
        galleryPosts = view.findViewById(R.id.galleryPostRecylcer);

        Button btnCloseGallery = view.findViewById(R.id.btnCloseGallery);
        btnCloseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLayout.setVisibility(View.INVISIBLE);
            }
        });

        lytRefresh = view.findViewById(R.id.lytRefresh);
        lytRefresh.setOnRefreshListener(this);


        Button btnPerfilViewData = view.findViewById(R.id.btnPerfilViewData);
        btnPerfilViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPerfilViewData_onClick();
            }
        });
        Log.i("PERFIL_TAG","Iniciando busqueda de usuario...");


        btnPerfilUpdate = view.findViewById(R.id.btnPerfilUpdate);
        btnPerfilUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilFragment.this.getContext(), EditAccountActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        setCurrentUser();


        return view;
    }


    public void btnPerfilViewData_onClick(){
        if(showingInfo)
            layoutPerfilInfo.setVisibility(View.GONE);
        else
            layoutPerfilInfo.setVisibility(View.VISIBLE);
        showingInfo =  !showingInfo;
    }

    private void setData(User user){
        if(user.getProfilePhoto()!= null && !user.getProfilePhoto().isEmpty()) {
            Glide.with(getContext()).load(user.getProfilePhoto()).into(imgUser);
        }

        txtName.setText(user.getName() +" "+ user.getLastNameA() +" "+ user.getLastNameB());
        txtEmail.setText(user.getEmail());
        txtBirthday.setText( df.format(user.getBirthDay()));
        txtGender.setText(user.getGender());
        txtCity.setText(user.getCity());
        txtPhone.setText(String.valueOf(user.getTelephone()));

        setEducation(user);

//        if(postAdapter == null) {
            linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
            listPosts.setLayoutManager(linearLayoutManager);
            postAdapter = new PostAdapter(getContext(), user.getPosts());
            listPosts.setAdapter(postAdapter);
            listPosts.addOnScrollListener(new PaginationListener(linearLayoutManager) {
                @Override
                protected void loadMoreItems() {
//                    lytRefresh.setRefreshing(true);
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
//        }

//        setPhotoGallery(user);
    }

    private void setEducation(User user){
        List<String> items = new ArrayList<>();
        if(user.getEducation()!= null){
            for(Education edu : user.getEducation()){
                items.add(edu.toString());
            }
        }
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        listEducation.setAdapter(educationAdapter);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)listEducation.getLayoutParams();
        params.height = 100 * items.size()*2;
        listEducation.setLayoutParams(params);
    }

    private void setCurrentUser(){
        btnPerfilUpdate.setEnabled(false);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.i("PERFIL_TAG",email);
        UserDAO.getInstance().getUser(email, new UserEvent() {
            @Override
            public void onSuccess(User user){
                Log.i("PERFIL_TAG",user.getName());
                PerfilFragment.this.user = user;
                btnPerfilUpdate.setEnabled(true);
                setData(user);
                loadPosts(user);


            }
            @Override
            public  void onFailure(Exception e){
                Log.i("PERFIL_TAG",e.getMessage());
            }
        });
    }

    private void setPhotoGallery(User user){
        ArrayList<Post> photos = new ArrayList<>();
        for(Post post : user.getPosts()){
            if(post.getType() == Type.PHOTO){
                photos.add(post);
                Log.i("PERFIL_TAG","Agregando foto: " + post.getMedia() );
            }
        }
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        listPhotos.setLayoutManager(layoutManager);
        galleryAdapter = new GalleryAdapter(getContext(),photos, galleryLayout, galleryPosts);
        listPhotos.setAdapter(galleryAdapter);

        LinearLayoutManager galleryLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        galleryPosts.setLayoutManager(galleryLayoutManager);
        PostAdapter galleryPostAdapter = new PostAdapter(getContext(), photos);
        galleryPosts.setAdapter(galleryPostAdapter);

    }


    private void loadPosts(final User user){
        UserFilter filter = new UserFilter(user.getEmail());
        cursor = new PostCursor(null, user.getEmail(), PAGE_SIZE);
        cursor.setFilter(filter);
        cursor.setEvent(new PostCursor.PostEvent() {
            @Override
            public void onDataFetched(List<Post> posts) {
                lytRefresh.setRefreshing(false);
                isLoading = false;
                for(Post post : posts) {
                    user.addPost(post);
                    postAdapter.notifyItemInserted(itemCount++);
                    Log.i("PERFIL_TAG", "Post agregado");

                }
                setPhotoGallery(user);
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


    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        postAdapter.clear();
        setCurrentUser();
    }
}
