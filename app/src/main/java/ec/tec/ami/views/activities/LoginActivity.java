package ec.tec.ami.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.AuthProvider;

import ec.tec.ami.R;

public class LoginActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private static final int GOOGLE_LOGIN = 1;

    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;
    private Button btnGoogleLogin, btnSignIn, btnLogIn;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient =  GoogleSignIn.getClient(this, signInOptions);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnLogIn = findViewById(R.id.btnLogin);
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginGoole();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateAccount();
            }
        });
        //CAMBIAR METODO
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSuccess();
            }
        });
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users");
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            checkAccount(account,false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_LOGIN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void loginGoole(){
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent,GOOGLE_LOGIN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            if(completedTask.isSuccessful()) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                String user = "";
                user = "Name: "+account.getDisplayName();
                user += "\nEmail: "+account.getEmail();
                user += "\nId: " + account.getId();
                checkAccount(account, true);
                Toast.makeText(this,user,Toast.LENGTH_LONG).show();
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private void openCreateAccount(){
        Intent intent = new Intent(this,CreateAccountEmailActivity.class);
        startActivity(intent);
    }

    private void checkAccount(final GoogleSignInAccount account, final boolean register){
        Query query = reference.orderByChild("email").equalTo(account.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    signInWithGoogle(account);
                }else{
                    if(register) {
                        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                        intent.putExtra("email", account.getEmail());
                        intent.putExtra("password", "");
                        intent.putExtra("isGoogle",true);
                        startActivity(intent);
                    }else{
                        GoogleSignIn.getClient(LoginActivity.this,signInOptions).signOut()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signInWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_LONG).show();
                    loginSuccess();
                }
            }
        });
    }

    private void loginSuccess(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));

    }
}
