package ec.tec.ami.views.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostCursor;
import ec.tec.ami.model.Post;
import ec.tec.ami.views.activities.PostActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeLineFragment extends Fragment {

    private EditText txtNewPost;
    private View view;

    public TimeLineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_time_line, container, false);
        txtNewPost = view.findViewById(R.id.txtNewPost);
        txtNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPost();
            }
        });
        return view;
    }

    private void newPost(){
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        PostCursor cursor = new PostCursor(new ArrayList<String>(), FirebaseAuth.getInstance().getCurrentUser().getEmail(),10);
        cursor.setEvent(new PostCursor.PostEvent() {
            @Override
            public void onDataFetched(List<Post> posts) {
                System.out.println("aaaa");
            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onEmptyData() {

            }
        });
        cursor.next();
    }
}
