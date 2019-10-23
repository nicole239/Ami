package ec.tec.ami.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ec.tec.ami.R;
import ec.tec.ami.model.User;

public class CreateAccountEmailActivity extends AppCompatActivity {

    private Button btnNext;
    private EditText txtEmail, txtPassword, txtConfirm;
    private DatabaseReference reference;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_email);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccount();
            }
        });
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirm = findViewById(R.id.txtConfirm);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users");
    }

    public void checkAccount(){
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        String confirm = txtConfirm.getText().toString();

        if(email.trim().isEmpty() || password.trim().isEmpty() || confirm.trim().isEmpty())
            return;
        if(!password.equals(confirm)){
            Toast.makeText(this,"Password don\'t match",Toast.LENGTH_LONG).show();
            return;
        }
        Query query = reference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(CreateAccountEmailActivity.this,"The email is already registered",Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(CreateAccountEmailActivity.this,CreateAccountActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    finish();
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
