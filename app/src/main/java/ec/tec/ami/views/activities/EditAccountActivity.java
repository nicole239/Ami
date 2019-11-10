package ec.tec.ami.views.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import ec.tec.ami.R;
import ec.tec.ami.data.dao.ImageDAO;
import ec.tec.ami.data.event.ImageEvent;
import ec.tec.ami.model.Education;
import ec.tec.ami.model.User;

public class EditAccountActivity extends AppCompatActivity {

    private static final int GALLERY = 1, CAMERA = 2, EDUCATION = 3;

    private Uri uri;
    private Button btnNext, btnEducation;
    private Spinner spinnerGender;
    private EditText txtBirthday,txtName, txtLastName, txtLastName2, txtCity, txtTelephone;
    private ImageView imgSelect;
    private CircleImageView imgProfile;
    private int day, month,year;
    private List<Education> education = new ArrayList<>();
    private String email, password, profileURL;
    private boolean isGoogle;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(EditAccountActivity.this,R.style.AlertDialogCustom)
                        .setTitle("Confirmation")
                        .setMessage("Do want to discard the changes?")
                        .setPositiveButton("Discard changes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                setResult(Activity.RESULT_CANCELED,intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Keep editing",null).show();
            }
        });

        spinnerGender = findViewById(R.id.spinnerGender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,R.layout.spinner_item, Arrays.asList(User.GENDERS));
        spinnerGender.setAdapter(genderAdapter);

        Calendar currentDate = Calendar.getInstance();
        year = currentDate.get(Calendar.YEAR);
        month = currentDate.get(Calendar.MONTH);
        day = currentDate.get(Calendar.DAY_OF_MONTH);

        txtBirthday = findViewById(R.id.txtBirthday);
        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { ;
                createAccount();
            }
        });

        btnEducation = findViewById(R.id.btnEducation);
        btnEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectEducation();
            }
        });

        imgSelect = findViewById(R.id.imgSelect);
        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        imgProfile = findViewById(R.id.imgProfile);

        txtName = findViewById(R.id.txtName);
        txtLastName = findViewById(R.id.txtLastName);
        txtLastName2 = findViewById(R.id.txtLastName2);
        txtCity = findViewById(R.id.txtCity);
        txtTelephone = findViewById(R.id.txtTelephone);

        progressBar = findViewById(R.id.progress);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users");
        auth = FirebaseAuth.getInstance();
        loadData();
    }

    private void loadData(){
        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("user");

        txtName.setText(user.getName());
        txtLastName.setText(user.getLastNameA());
        txtLastName2.setText(user.getLastNameB());
        txtCity.setText(user.getCity());
        txtTelephone.setText(String.valueOf(user.getTelephone()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getBirthDay());
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        btnEducation.setText("Edit");
        String format = "dd/MM/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        txtBirthday.setText(formatter.format(user.getBirthDay()));
        for(int i=0;i<spinnerGender.getAdapter().getCount();i++){
            Object obj = spinnerGender.getAdapter().getItem(i);
            if(obj.equals(user.getGender())){
                spinnerGender.setSelection(i);
            }
        }
        for(Education e : user.getEducation()){
            this.education.add((Education) e.clone());
        }
        Glide.with(this).load(user.getProfilePhoto()).into(imgProfile);
    }

    private void selectEducation() {
        Intent intent = new Intent(this, EducationActivity.class);
        intent.putExtra("list",(Serializable)education);
        startActivityForResult(intent,EDUCATION);
    }

    private void selectDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,i);
                calendar.set(Calendar.MONTH,i1);
                calendar.set(Calendar.DAY_OF_MONTH,i2);
                String format = "dd/MM/yyyy";
                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
                txtBirthday.setText(formatter.format(calendar.getTime()));
                year = i;
                month = i1;
                day = i2;
            }
        },year,month,day);
        datePickerDialog.show();
    }


    private void createAccount(){
        String name = txtName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String lastName2 = txtLastName2.getText().toString();
        String city = txtCity.getText().toString();
        String telephone = txtTelephone.getText().toString();
        if(isEmpty(name, lastName, lastName2, city, telephone))
            return;

        Calendar calendar = Calendar.getInstance();
        final User user = new User();
        user.setName(name);
        user.setLastNameA(lastName);
        user.setLastNameB(lastName2);
        user.setCity(city);
        user.setTelephone(Integer.parseInt(telephone));
        user.setGender((String)spinnerGender.getSelectedItem());
        user.setEmail(this.user.getEmail());
        user.setEducation(education);

        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        user.setBirthDay(calendar.getTime());
        user.setFriends(this.user.getFriends());
        progressBar.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false);
        registerInFireBase(user);


    }

    private void registerInFireBase(final User user){
        ImageDAO.getInstance().uploadImage(uri,"profile/"+user.getID(),new ImageEvent(){
            @Override
            public void onSuccess(String link) {
                if(link.isEmpty()){
                    user.setProfilePhoto(EditAccountActivity.this.user.getProfilePhoto());
                }else {
                    user.setProfilePhoto(link);
                }
                reference.child(user.getID()).setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditAccountActivity.this, "Account modified", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(EditAccountActivity.this, "The account couldn\'t be modified", Toast.LENGTH_LONG).show();
                                    enableButton();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditAccountActivity.this, "The account couldn\'t be modified", Toast.LENGTH_LONG).show();
                                enableButton();
                            }
                        });
            }

            @Override
            public void onFailure(String err) {
                Toast.makeText(EditAccountActivity.this, "The account couldn\'t be modified", Toast.LENGTH_LONG).show();
                enableButton();
            }
        });

    }

    private void enableButton(){
        progressBar.setVisibility(View.INVISIBLE);
        btnNext.setEnabled(true);
    }

    private boolean isEmpty(String... texts){
        for(String s: texts)
            if(s.trim().isEmpty())
                return true;
        return false;
    }

    private void selectImage(){
        DialogSelectImage dialogSelectImage = new DialogSelectImage(this);
        dialogSelectImage.setOnPictureListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionCamera();
            }
        });
        dialogSelectImage.setOnGalleryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissionsGallery();
            }
        });
        dialogSelectImage.show();
    }

    private void getPermissionCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA);
        else
            takePicture();
    }

    private void getPermissionsGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        else
            chooseFromGallery();
    }
    private void chooseFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto,GALLERY);
    }

    private void takePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture,CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA){
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    takePicture();
                else
                    Toast.makeText(this, "Camera permission not granted, ", Toast.LENGTH_SHORT).show();
        }
        if(requestCode == GALLERY){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    chooseFromGallery();
            }
            else
                Toast.makeText(this, "Gallery permission not granted, ",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA || requestCode == GALLERY){
            if(resultCode == Activity.RESULT_OK){
                imgProfile.setImageURI(data.getData());
                uri = data.getData();
            }
        }
        if(requestCode == EDUCATION){
            if(resultCode == Activity.RESULT_OK) {
                education = (List<Education>) data.getSerializableExtra("list");
                if (education.size() > 0) {
                    btnEducation.setText("Edit...");
                }
            }
        }
    }
}
