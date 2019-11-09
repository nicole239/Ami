package ec.tec.ami.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Education;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;
import ec.tec.ami.model.User;
import ec.tec.ami.views.activities.CreateAccountActivity;
import ec.tec.ami.views.activities.LoginActivity;
import ec.tec.ami.views.adapters.GalleryAdapter;
import ec.tec.ami.views.adapters.PostAdapter;

import static ec.tec.ami.views.utils.PaginationListener.PAGE_SIZE;


public class PerfilFragment extends Fragment {

    RelativeLayout layoutPerfilInfo;
    private boolean showingInfo = false;
    TextView txtName, txtEmail, txtBirthday, txtGender, txtCity, txtPhone;
    ListView listEducation;
    RecyclerView listPosts, listPhotos;
    PostAdapter postAdapter;
    ImageView imgUser;

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");


    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

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

        Button btnPerfilViewData = view.findViewById(R.id.btnPerfilViewData);
        btnPerfilViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPerfilViewData_onClick();
            }
        });
        Log.i("PERFIL_TAG","Iniciando busqueda de usuario...");
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

        listPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), user.getPosts());
        listPosts.setAdapter(postAdapter);

        setPhotoGallery(user);
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
    }

    private void setCurrentUser(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.i("PERFIL_TAG",email);
        UserDAO.getInstance().getUser(email, new UserEvent() {
            @Override
            public void onSuccess(User user){
                Log.i("PERFIL_TAG",user.getName());
                loadPosts(user);


            }
            @Override
            public  void onFailure(Exception e){
                Log.i("PERFIL_TAG",e.getMessage());
            }
        });
    }

    private void setPhotoGallery(User user){
        ArrayList<String> photos = new ArrayList<>();
        for(Post post : user.getPosts()){
            if(post.getType() == Type.PHOTO){
                photos.add(post.getMedia());
                Log.i("PERFIL_TAG","Agregando foto: " + post.getMedia() );
            }
        }
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        listPhotos.setLayoutManager(layoutManager);
        GalleryAdapter adapter = new GalleryAdapter(getContext(),photos);
        listPhotos.setAdapter(adapter);

    }

    private void loadPosts(final User user){
         List<String> users = new ArrayList<>();
         users.add(user.getEmail());
         PostCursor cursor = new PostCursor(users, user.getEmail(), PAGE_SIZE);
         cursor.setEvent(new PostCursor.PostEvent() {
            @Override
            public void onDataFetched(List<Post> posts) {
                for(Post post : posts){
                    user.addPost(post);
                    Log.i("PERFIL_TAG", "Post agregado");
                }
                setData(user);
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onEmptyData() {

            }
         });
         cursor.next();
    }


}
