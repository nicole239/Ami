package ec.tec.ami.views.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ec.tec.ami.R;

public class DialogInput extends Dialog {

    private String title, value, regx = "";
    private TextView tvTitle;
    private EditText txtValue;
    private Button btnOk, btnClose;
    private InputListener listener = null;

    public DialogInput(@NonNull Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_input);
        this.title = title;
        init();
    }
//
//    public DialogInput(@NonNull Context context, String title, String regx) {
//        super(context);
//        this.title = title;
//        this.regx = regx;
//        setContentView(R.layout.dialog_input);
//        init();
//    }

    private void init(){
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        txtValue = findViewById(R.id.txtText);
        btnOk = findViewById(R.id.btnOk);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInput.this.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValue();
            }
        });
    }

    private void checkValue(){
        value = txtValue.getText().toString();
        if(android.util.Patterns.WEB_URL.matcher(value).matches()){
            listener.onFinished(value);
            dismiss();
        }

    }

    public void setOnInputListener(InputListener listener){
        this.listener = listener;
    }

    interface InputListener{
        void onFinished(String value);
    }
}
