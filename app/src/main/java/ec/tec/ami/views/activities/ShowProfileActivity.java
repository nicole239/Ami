package ec.tec.ami.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import ec.tec.ami.views.adapters.GalleryAdapter;
import ec.tec.ami.views.adapters.PostAdapter;
import ec.tec.ami.views.utils.PaginationListener;

import static ec.tec.ami.views.utils.PaginationListener.PAGE_SIZE;
import static ec.tec.ami.views.utils.PaginationListener.PAGE_START;

public class ShowProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private View view;
    RelativeLayout layoutPerfilInfo;
    private boolean showingInfo = false;
    TextView txtName, txtEmail, txtBirthday, txtGender, txtCity, txtPhone;
    ListView listEducation;
    RecyclerView listPosts, listPhotos, galleryPosts;
    PostAdapter postAdapter;
    ImageView imgUser;
    private User user;
    private LinearLayoutManager linearLayoutManager;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
    private boolean isLoading = false;
    int itemCount = 0;
    private PostCursor cursor;
    Intent intent;
    String showPerfilUserEmail;
    Boolean isFriend = false;
    private Button btnSolicitud;

    private SwipeRefreshLayout lytRefresh;
    private RelativeLayout galleryLayout;


    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        Toolbar toolbar = findViewById(R.id.showPerfilToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        layoutPerfilInfo =  findViewById(R.id.showPerfilLayoutPerfilInfo);
        layoutPerfilInfo.setVisibility(View.GONE);

        txtName =  findViewById(R.id.showPerfilTxtPerfilName);
        txtEmail = findViewById(R.id.showPerfilTxtCorreo);
        txtBirthday = findViewById(R.id.showPerfilTxtPerfilBirthday);
        txtGender = findViewById(R.id.showPerfilTxtPerfilGender);
        txtCity = findViewById(R.id.showPerfilTxtPerfilCity);
        txtPhone = findViewById(R.id.showPerfilTxtPerfilPhone);
        listEducation  = findViewById(R.id.showPerfilListPerfilEducation);
        listPosts = findViewById(R.id.showPerfilListPerfilPosts);
        listPhotos = findViewById(R.id.showPerfilPhotoGallery);
        imgUser = findViewById(R.id.showPerfilImgPerfilPicture);
        galleryLayout = findViewById(R.id.PhotoLayoutShow);
        galleryPosts = findViewById(R.id.galleryPostRecylcerShow);

        Button btnCloseGallery = findViewById(R.id.btnCloseGalleryShow);
        btnCloseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLayout.setVisibility(View.INVISIBLE);
            }
        });

        intent = getIntent();
        showPerfilUserEmail = intent.getStringExtra("showPerfilUser");


        lytRefresh = findViewById(R.id.lytRefresh);
        //lytRefresh.setOnRefreshListener(ShowProfileActivity.this);

        Button btnPerfilViewData = findViewById(R.id.showPerfilBtnPerfilViewData);
        btnPerfilViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPerfilViewData_onClick();
            }
        });
        Log.i("PERFIL_TAG","Iniciando busqueda de usuario...");

        Button btnFriends = findViewById(R.id.showPerfilBtnPerfilViewFriends);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowProfileActivity.this,FriendsActivity.class);
                intent.putExtra("email",showPerfilUserEmail);
                startActivity(intent);
            }
        });

        btnSolicitud = findViewById(R.id.showPerfilBtnPerfilAddDelFriend);
        btnSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFriend)
                    deleteFriend();
                else
                    openSendRequestDialog();
            }
        });
        isFriend();



        setCurrentUser(showPerfilUserEmail);

    }

    private void btnPerfilViewData_onClick(){

        if(showingInfo)
            layoutPerfilInfo.setVisibility(View.GONE);
        else
            layoutPerfilInfo.setVisibility(View.VISIBLE);
        showingInfo =  !showingInfo;
    }

    private void setCurrentUser(String email){

        Log.i("PERFIL_TAG",email);
        UserDAO.getInstance().getUser(email, new UserEvent() {
            @Override
            public void onSuccess(User user){
                Log.i("PERFIL_TAG",user.getName());
                ShowProfileActivity.this.user = user;
                setData(user);
                loadPosts(user);

            }
            @Override
            public  void onFailure(Exception e){
                Log.i("PERFIL_TAG",e.getMessage());
            }
        });
    }

    private void setData(User user){
        if(user.getProfilePhoto()!= null && !user.getProfilePhoto().isEmpty()) {
            Glide.with(getApplicationContext()).load(user.getProfilePhoto()).into(imgUser);
        }

        txtName.setText(user.getName() +" "+ user.getLastNameA() +" "+ user.getLastNameB());
        txtEmail.setText(user.getEmail());
        txtBirthday.setText( df.format(user.getBirthDay()));
        txtGender.setText(user.getGender());
        txtCity.setText(user.getCity());
        txtPhone.setText(String.valueOf(user.getTelephone()));

        setEducation(user);

//        if(postAdapter == null) {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        listPosts.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(this, user.getPosts());
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
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
        listEducation.setAdapter(educationAdapter);
    }

    private void setPhotoGallery(User user){
        ArrayList<Post> photos = new ArrayList<>();
        for(Post post : user.getPosts()){
            if(post.getType() == Type.PHOTO){
                photos.add(post);
                Log.i("PERFIL_TAG","Agregando foto: " + post.getMedia() );
            }
        }
        LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
        listPhotos.setLayoutManager(layoutManager);
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(),photos, galleryLayout, galleryPosts);
        listPhotos.setAdapter(adapter);

        LinearLayoutManager galleryLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        galleryPosts.setLayoutManager(galleryLayoutManager);
        PostAdapter galleryPostAdapter = new PostAdapter(this, photos);
        galleryPosts.setAdapter(galleryPostAdapter);

    }

    private void loadPosts(final User user){
        UserFilter filter = new UserFilter(user.getEmail());
        cursor = new PostCursor(null, user.getEmail(), PAGE_SIZE);
        cursor.setFilter(filter);
        cursor.setEvent(new PostCursor.PostEvent() {
            @Override
            public void onDataFetched(List<Post> posts) {
                //lytRefresh.setRefreshing(false);
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
                //lytRefresh.setRefreshing(false);
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

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void openSendRequestDialog(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = new Intent(this, DialogSendFriendRequest.class);
        intent.putExtra(DialogSendFriendRequest.NAME_TAG, txtName.getText().toString());
        intent.putExtra(DialogSendFriendRequest.TO_EMAIL_TAG, showPerfilUserEmail);
        intent.putExtra(DialogSendFriendRequest.FROM_EMAIL_TAG, email);
        startActivity(intent);

    }

    private void isFriend(){
        final User currentUser = new User();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        currentUser.setEmail(email);

        UserDAO.getInstance().listFriends(currentUser,new UserEvent(){
            @Override
            public void onSuccess(List<User> users) {
                for(User u : users){
                    if(u.getEmail().equals(showPerfilUserEmail)) {
                        isFriend = true;
                        btnSolicitud.setText("Eliminar Amigo");
                    }
                }
            }
        });

    }

    private void deleteFriend(){
        DialogDecision dialogDecision = new DialogDecision(this, "Confirmation", "Do you want to delete this friend?", new DialogDecision.DialogResult() {
            @Override
            public void onConfirm() {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                final String friend = showPerfilUserEmail;
                UserDAO.getInstance().deleteFriend(email, friend, new UserEvent(){
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ShowProfileActivity.this,"Friend deleted",Toast.LENGTH_SHORT).show();
                        isFriend = false;
                        btnSolicitud.setText("Solicitud");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ShowProfileActivity.this,"Could not delete friend",Toast.LENGTH_SHORT).show();
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
