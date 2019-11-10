package ec.tec.ami.views.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ec.tec.ami.R;

public class DialogInputValue extends Dialog {

    private String title, value, regx = "";
    private TextView tvTitle;
    private EditText txtValue;
    private Button btnOk, btnClose;
    private DialogResult dialogResult;


    public DialogInputValue(@NonNull Context context, String title, @NonNull DialogResult dialogResult) {
        super(context);
        setContentView(R.layout.dialog_input);
        this.title = title;
        this.dialogResult = dialogResult;
        init();
    }

    public DialogInputValue(@NonNull Context context, String title, String regx, @NonNull DialogResult dialogResult) {
        super(context);
        setContentView(R.layout.dialog_input);
        this.title = title;
        this.regx = regx;
        this.dialogResult = dialogResult;
        init();
    }


    private void init(){
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        txtValue = findViewById(R.id.txtText);
        btnOk = findViewById(R.id.btnOk);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.onDismiss();
                DialogInputValue.this.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = txtValue.getText().toString();
                if(value.isEmpty())
                    return;
                if(!regx.isEmpty()){
                    if(value.matches(regx)){
                        DialogInputValue.this.dismiss();
                        dialogResult.onDialogResponse(value);
                    }
                }else{
                    DialogInputValue.this.dismiss();
                    dialogResult.onDialogResponse(value);
                }
            }
        });
    }


    interface DialogResult{
        void onDialogResponse(String value);
        void onDismiss();
    }
}
