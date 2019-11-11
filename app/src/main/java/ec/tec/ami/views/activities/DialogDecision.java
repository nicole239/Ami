package ec.tec.ami.views.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ec.tec.ami.R;

public class DialogDecision extends Dialog {

    private String title, value;
    private TextView tvTitle;
    private TextView txtValue;
    private Button btnOk, btnClose;
    private DialogResult dialogResult;


    public DialogDecision(@NonNull Context context, String title, String value, @NonNull DialogResult dialogResult) {
        super(context);
        setContentView(R.layout.dialog_decision);
        this.title = title;
        this.value = value;
        this.dialogResult = dialogResult;
        init();
    }



    private void init(){
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        txtValue = findViewById(R.id.txtText);
        txtValue.setText(value);
        btnOk = findViewById(R.id.btnOk);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.onCancel();
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.onConfirm();
                dismiss();
            }
        });
    }


    public interface DialogResult{
        void onConfirm();
        void onCancel();
    }
}
