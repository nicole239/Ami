package ec.tec.ami.views.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.User;

public class LoginActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private static final int GOOGLE_LOGIN = 1;

    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;
    private Button btnGoogleLogin, btnSignIn, btnLogIn;
    private EditText txtEmail, txtPassword;
    private RelativeLayout layout;
    private TextView tvForgot;
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
                loginGoogle();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateAccount();
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithEmail();
            }
        });
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        layout = findViewById(R.id.lytPlaceHolder);

        tvForgot = findViewById(R.id.tvForgot);

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInputValue dialogInputValue = new DialogInputValue(LoginActivity.this, "Recover account", new DialogInputValue.DialogResult() {
                    @Override
                    public void onDialogResponse(String value) {
                        forgotPassword(value);
                    }

                    @Override
                    public void onDismiss() {

                    }
                });
                dialogInputValue.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            checkAccount(account,false);
//            signInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//
//                }
//            });
        }else{
            layout.setVisibility(View.GONE);
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

    private void loginGoogle(){
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent,GOOGLE_LOGIN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try {
            if(completedTask.isSuccessful()) {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                checkAccount(account, true);
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
        UserDAO.getInstance().checkEmail(account.getEmail(),new UserEvent() {
            @Override
            public void onSuccess(boolean state){
                if(state){
                    UserDAO.getInstance().getUserType(account.getEmail(),new UserEvent(){
                        @Override
                        public void onSuccess(User user) {
                            if(user.getType() == User.Type.GMAIL){
                                signInWithGoogle(account);
                            }else{
                                Toast.makeText(LoginActivity.this,"This email is already registered",Toast.LENGTH_SHORT).show();
                                signOut();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            signOut();
                        }
                    });
                }else if(register){
                    Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                    intent.putExtra("email", account.getEmail());
                    intent.putExtra("password", "");
                    intent.putExtra("isGoogle",true);
                    startActivity(intent);
                }
            }
        });
    }

    private void signInWithGoogle(GoogleSignInAccount account){
        UserDAO.getInstance().loginWithGoogle(account, new UserEvent(){
            @Override
            public void onSuccess() {
                loginSuccess();
            }

            @Override
            public void onFailure(Exception e) {
               signOut();
            }
        });
    }

    private void signOut(){
        GoogleSignIn.getClient(LoginActivity.this, signInOptions).signOut()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                layout.setVisibility(View.GONE);
            }
        });
    }

    private void signInWithEmail(){
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(email.trim().isEmpty() || password.trim().isEmpty()){
            return;
        }
        UserDAO.getInstance().loginWithEmail(email, password, new UserEvent(){
            @Override
            public void onSuccess() {
                loginSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(LoginActivity.this,"Email or password incorrect",Toast.LENGTH_LONG).show();
			}
        });
    }

    private void loginSuccess(){
        finish();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));

    }

    private void forgotPassword(String email){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this,"The email could not be sent",Toast.LENGTH_LONG).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,"The email could not be sent",Toast.LENGTH_LONG).show();
            }
        });
    }
}
