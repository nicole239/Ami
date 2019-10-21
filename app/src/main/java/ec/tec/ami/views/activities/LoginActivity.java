package ec.tec.ami.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;

import ec.tec.ami.R;

public class LoginActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private static final int GOOGLE_LOGIN = 1;

    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;
    private Button btnGoogleLogin, btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInClient =  GoogleSignIn.getClient(this, signInOptions);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnSignIn = findViewById(R.id.btnSignIn);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
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
}
