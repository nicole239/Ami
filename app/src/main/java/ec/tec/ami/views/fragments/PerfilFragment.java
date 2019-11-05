package ec.tec.ami.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.model.Education;
import ec.tec.ami.model.User;
import ec.tec.ami.views.adapters.PostAdapter;


public class PerfilFragment extends Fragment {

    RelativeLayout layoutPerfilInfo;
    private boolean showingInfo = false;
    TextView txtName;
    TextView txtEmail;
    TextView txtBirthday;
    TextView txtGender;
    TextView txtCity;
    TextView txtPhone;
    ListView listEducation;
    RecyclerView listPosts;
    PostAdapter postAdapter;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        layoutPerfilInfo =  view.findViewById(R.id.layoutPerfilInfo);
        layoutPerfilInfo.setVisibility(View.GONE);

        txtName = view.findViewById(R.id.txtPerfilName);
        txtEmail = view.findViewById(R.id.txtCorreo);
        txtBirthday = view.findViewById(R.id.txtPerfilBirthday);
        txtGender = view.findViewById(R.id.txtPerfilGender);
        txtCity = view.findViewById(R.id.txtPerfilCity);
        txtPhone = view.findViewById(R.id.txtPerfilPhone);
        listEducation  = view.findViewById(R.id.listPerfilEducation);
        listPosts = view.findViewById(R.id.listPerfilPosts);

        Button btnPerfilViewData = view.findViewById(R.id.btnPerfilViewData);
        btnPerfilViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPerfilViewData_onClick();
            }
        });

        return view;
    }


    public void btnPerfilViewData_onClick(){
        if(showingInfo)
            layoutPerfilInfo.setVisibility(View.GONE);
        else
            layoutPerfilInfo.setVisibility(View.VISIBLE);
        showingInfo =  !showingInfo;
    }

    private void setData(User user){
        txtName.setText(user.getName() +" "+ user.getLastNameA() +" "+ user.getLastNameB());
        txtEmail.setText(user.getEmail());
        txtBirthday.setText( user.getBirthDay().toString());
        txtGender.setText(user.getGender());
        txtCity.setText(user.getCity());
        txtPhone.setText(String.valueOf(user.getTelephone()));

        List<String> items = new ArrayList<>();
        for(Education edu : user.getEducation()){
            items.add(edu.toString());
        }
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
        listEducation.setAdapter(educationAdapter);
        listPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), user.getPosts());
        //postAdapter.setClickListener(this);
        listPosts.setAdapter(postAdapter);
    }



}
