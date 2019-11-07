package ec.tec.ami.views.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.ImageDAO;
import ec.tec.ami.data.dao.PostDAO;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.ImageEvent;
import ec.tec.ami.data.event.PostEvent;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;
import ec.tec.ami.model.User;
import ec.tec.ami.views.utils.ImageSelector;

public class PostActivity extends AppCompatActivity {

    private ImageSelector imageSelector = new ImageSelector(this);
    private Uri uri;
    private Button btnPicture, btnDelete, btnVideo, btnDeleteVideo, btnPublish;
    private View photoArea, buttonBar, videoArea;
    private ImageView imgPhotoView, imgVideo, imgPhoto;
    private String URL;
    private TextView tvTitle;
    private Type type;
    private ProgressBar progressBar;
    private EditText txtPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(PostActivity.this,R.style.AlertDialogCustom)
                        .setTitle("Confirmation")
                        .setMessage("Do want to discard the changes?")
                        .setPositiveButton("Discard changes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                setResult(Activity.RESULT_CANCELED,intent);
                                finish();
//                                overridePendingTransition(R.anim.slide_out,R.anim.slide_in);

                            }
                        })
                        .setNegativeButton("Keep editing",null).show();


            }
        });

        btnPicture = findViewById(R.id.btnPicture);
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSelectImage();
            }
        });

        photoArea = findViewById(R.id.photoArea);
        buttonBar = findViewById(R.id.buttonBar);
        videoArea = findViewById(R.id.videoArea);
        btnDelete = findViewById(R.id.btnDelete);
        btnVideo = findViewById(R.id.btnVideo);
        imgPhotoView = findViewById(R.id.imgPhotoView);
        imgVideo = findViewById(R.id.imgVideo);
        tvTitle = findViewById(R.id.tvTitle);
        btnPublish = findViewById(R.id.btnPublish);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePhotoArea();
                type = Type.TEXT;
            }
        });

        btnDeleteVideo = findViewById(R.id.btnDeleteVideo);

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSelectVideo();
            }
        });

        btnDeleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVideoArea();
                type = Type.TEXT;
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPost();
            }
        });

        progressBar = findViewById(R.id.progress);

        imgPhoto = findViewById(R.id.imgPhoto);

        txtPost = findViewById(R.id.txtPost);

        txtPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtPost.getText().toString().isEmpty()){
                    btnPublish.setEnabled(false);
                }else{
                    btnPublish.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        UserDAO.getInstance().getUserPhoto(email,new UserEvent(){
            @Override
            public void onSuccess(User user) {
                Glide.with(imgPhoto).load(user.getProfilePhoto()).into(imgPhoto);
            }
        });
    }

    private void btnSelectImage(){
        imageSelector.selectImage();
    }

    private void btnSelectVideo(){
        DialogInput input = new DialogInput(this,"Youtube video URL");
        input.setOnInputListener(new DialogInput.InputListener() {
            @Override
            public void onFinished(String value) {
                URL = value;
                loadVideoInfo(value);
                type = Type.VIDEO;
            }
        });
        input.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ImageSelector.CAMERA){
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    imageSelector.takePicture();
                else
                    Toast.makeText(this, "Camera permission not granted, ", Toast.LENGTH_SHORT).show();
        }
        if(requestCode == ImageSelector.GALLERY){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    imageSelector.chooseFromGallery();
            }
            else
                Toast.makeText(this, "Gallery permission not granted, ",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ImageSelector.CAMERA || requestCode == ImageSelector.GALLERY){
            if(resultCode == Activity.RESULT_OK){
                uri = data.getData();
                imgPhotoView.setImageURI(uri);
                togglePhotoArea();
                type = Type.PHOTO;
            }
        }
    }

    private void togglePhotoArea(){
        toggleButtonBar();
        int visibility = photoArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        photoArea.setVisibility(visibility);
    }

    private void toggleVideoArea(){
        toggleButtonBar();
        int visibility = videoArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        videoArea.setVisibility(visibility);
    }


    private void toggleButtonBar(){
        int visibility = buttonBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        buttonBar.setVisibility(visibility);
    }

    private void loadVideoInfo(final String value){
        final Bitmap[] bmp = {null};
        Runnable load = new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(value).get();
                    final String title = document.select("meta[name=title]").get(0).attr("content");

                    String videoId = null;
                    Pattern pattern = Pattern.compile(".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");
                    Matcher matcher = pattern.matcher(value);
                    if(matcher.matches()){
                        videoId = matcher.group(1);
                        String videoImageUrl = String.format("https://i.ytimg.com/vi/%s/hqdefault.jpg",videoId);
                        Connection.Response response = Jsoup
                                .connect(videoImageUrl)
                                .ignoreContentType(true)
                                .execute();
                        bmp[0] = BitmapFactory.decodeStream(new ByteArrayInputStream(response.bodyAsBytes()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(bmp[0] != null){
                                imgVideo.setImageBitmap(bmp[0]);
                            }
                            tvTitle.setText(title);
                            toggleVideoArea();
                        }
                    });

                } catch (IOException e) {
                    Log.e("err-doc",e.getMessage());
                }

            }
        };
        Thread thread = new Thread(load);
        thread.start();

    }

    public void newPost(){
        final String description = txtPost.getText().toString();
        final Date date = new Date();
        final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        progressBar.setVisibility(View.VISIBLE);
        btnPublish.setEnabled(false);
        if(type == Type.PHOTO){
            User u = new User();
            u.setEmail(user);
            SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            String path = "post/"+u.getID()+"/"+format.format(date);
            ImageDAO.getInstance().uploadImage(uri,path,new ImageEvent(){
                @Override
                public void onSuccess(String link) {
                    Post post = new Post(user,description,link,date,type);
                    publishPost(post);
                }

                @Override
                public void onFailure(String err) {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnPublish.setEnabled(true);
                    Toast.makeText(PostActivity.this,"Could not add the post",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            String _media = "";
            if(type == Type.VIDEO){
                _media = URL;
            }
            Post post = new Post(user,description,_media,date,type);
            publishPost(post);
        }
    }

    private void publishPost(Post post){
        PostDAO.getInstance().addPost(post,new PostEvent(){
            @Override
            public void onSuccess(Post user) {
                Toast.makeText(PostActivity.this,"Post published",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                btnPublish.setEnabled(true);
                Toast.makeText(PostActivity.this,"Could not add the post",Toast.LENGTH_LONG).show();
            }
        });
    }


}
