package ec.tec.ami.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.views.fragments.NotificationsFragment;

public class DialogSendFriendRequest extends AppCompatActivity {

    public static final String NAME_TAG = "TO_NAME_TAG";
    public static final String TO_EMAIL_TAG = "TO_EMAIL_TAG";
    public static final String FROM_EMAIL_TAG = "FROM_EMAIL_TAG";

    private String email_to, email_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_send_friend_request);

        Intent intent = getIntent();
        String name = intent.getStringExtra(NAME_TAG);
        email_from = intent.getStringExtra(FROM_EMAIL_TAG);
        email_to = intent.getStringExtra(TO_EMAIL_TAG);

        TextView txtName = findViewById(R.id.txtRequestName);
        txtName.setText(name);

    }

    public void btnCancel_onClick(View view){
        finish();
    }

    public void btnConfirm_onClick(View view){
        UserDAO.getInstance().addNotification(email_from,email_to,new UserEvent(){
            @Override
            public void onSuccess() {
                Toast.makeText(DialogSendFriendRequest.this,"Friend request sent",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(DialogSendFriendRequest.this,"Could not send friend request",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
