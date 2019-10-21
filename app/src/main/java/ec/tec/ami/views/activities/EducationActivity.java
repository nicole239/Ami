package ec.tec.ami.views.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ec.tec.ami.R;
import ec.tec.ami.model.Education;
import ec.tec.ami.views.fragments.EducationAdapter;

public class EducationActivity extends AppCompatActivity implements EducationAdapter.ItemListener {

    private List<Education> list;
    EditText txtDate, txtInstitution;
    Button btnAdd;
    EducationAdapter adapter;
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(EducationActivity.this,R.style.AlertDialogCustom)
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

        Intent intent = getIntent();
        list = (List<Education>)intent.getSerializableExtra("list");

        adapter = new EducationAdapter(this,list,true);
        adapter.setOnDeleteItem(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setEnabled(false);
        txtDate = findViewById(R.id.txtDate);
        txtInstitution = findViewById(R.id.txtInstitution);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }
        });
        txtInstitution.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkValidity();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        txtDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkValidity();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEducation();
            }
        });
    }

    private void addEducation(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);

        Education education = new Education(calendar.getTime(),txtInstitution.getText().toString());
        list.add(education);
        adapter.notifyDataSetChanged();

        txtDate.getText().clear();
        txtInstitution.getText().clear();

        InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtInstitution.getWindowToken(),0);
    }

    private void checkValidity(){
        if(!TextUtils.isEmpty(txtInstitution.getText().toString().trim()) && !TextUtils.isEmpty(txtDate.getText().toString().trim())){
            btnAdd.setEnabled(true);
        }else{
            btnAdd.setEnabled(false);
        }
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
                txtDate.setText(formatter.format(calendar.getTime()));
                year = i;
                month = i1;
                day = i2;
            }
        },year,month,day);
        datePickerDialog.show();
    }

    public void onConfirm(View view){
        Intent intent = new Intent();
        intent.putExtra("list",(Serializable)list);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    @Override
    public void onDelete(int position, Education item) {
        list.remove(position);
        adapter.notifyDataSetChanged();
    }
}
