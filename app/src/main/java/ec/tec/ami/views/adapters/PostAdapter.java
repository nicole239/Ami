package ec.tec.ami.views.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;
import ec.tec.ami.model.User;
import ec.tec.ami.views.utils.ReadableDateFormat;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

    // data is passed into the constructor
    public PostAdapter(Context context, List<Post> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Post post = mData.get(position);

        //TODO: MOSTRAR FOTO PERFIL
        //holder.imgUser.setImageBitmap(post.getUsuario().getImage());
        holder.multimediaFrame.setVisibility(View.GONE);
        holder.txtName.setText(post.getUser());
        holder.txtTime.setText(ReadableDateFormat.toHumanFormat(post.getDate()));
        holder.txtDescription.setText(post.getDescription());
        //TODO: DISTINGUIR VIDEO/IMAGEN EN POST
        if(post.getMedia() != null && post.getType() != Type.TEXT && !post.getMedia().isEmpty()){
            holder.multimediaFrame.setVisibility(View.VISIBLE);
            if(post.getType() == Type.PHOTO){
                //TODO: MOSTRAR IMAGEN
                holder.img.setVisibility(View.VISIBLE);
            }else if (post.getType() == Type.VIDEO){
                holder.video.getSettings().setJavaScriptEnabled(true);
                holder.video.setWebChromeClient(new WebChromeClient(){

                });
                String videoID = urlToEmbeded(post.getMedia());

                String urll = "https://www.youtube.com/embed/"+videoID;
                holder.video.loadData("<iframe width=\"100%\" height=\"100%\" src=\""+urll+"\"frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8");

            }
        }
        holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
        holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));

        holder.labelLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference reference = database.getReference().child("posts").child(mData.get(position).getId()).child("totalLikes");

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()>0){
                            List<String> td = (ArrayList<String>) dataSnapshot.getValue();
                            mData.get(position).setLikes(td);
                            for(int i=0; i<td.size();i++){
                                Log.d("Likes", td.get(i));
                            }

                            if(mData.get(position).getLikes().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                Toast.makeText(view.getContext(), "Ya le di like", Toast.LENGTH_LONG).show();
                            }
                            else{
                                mData.get(position).addLike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                reference.setValue(mData.get(position).getLikes())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(view.getContext(), "Likes has beed added", Toast.LENGTH_LONG).show();

                                                } else {
                                                    Toast.makeText(view.getContext(), "Something went wrond", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(view.getContext(), "Failure on addind likes", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }

                        }
                        else{
                            mData.get(position).setLikes(new ArrayList<String>());
                            mData.get(position).addLike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            reference.setValue(mData.get(position).getLikes())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(view.getContext(), "Likes has beed added", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(view.getContext(), "Something went wrond", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(view.getContext(), "Failure on addind likes", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        holder.labelDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference reference = database.getReference().child("posts").child(mData.get(position).getId()).child("totalDislikes");

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount()>0){
                            List<String> td = (ArrayList<String>) dataSnapshot.getValue();
                            mData.get(position).setDislikes(td);
                            for(int i=0; i<td.size();i++){
                                Log.d("Dislikes", td.get(i));
                            }

                            if(mData.get(position).getDislikes().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                Toast.makeText(view.getContext(), "Ya le di dislike", Toast.LENGTH_LONG).show();
                            }
                            else{
                                mData.get(position).addDislike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                reference.setValue(mData.get(position).getDislikes())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(view.getContext(), "Disikes has beed added", Toast.LENGTH_LONG).show();

                                                } else {
                                                    Toast.makeText(view.getContext(), "Something went wrond", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(view.getContext(), "Failure on addind dislikes", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }

                        }
                        else{
                            mData.get(position).setDislikes(new ArrayList<String>());
                            mData.get(position).addDislike(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            reference.setValue(mData.get(position).getDislikes())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(view.getContext(), "Disikes has beed added", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(view.getContext(), "Something went wrond", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(view.getContext(), "Failure on addind dislikes", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



                ;/*
                if(post.getMedia()!=""){
                    String embed = "iframe width=\"100%\" height=\"100%\" src=\" post.getMedia()\"frameborder=\"0\" allowfullscreen></iframe>";
                    holder.video.loadData(embed, "text/html","utf-8");
                }
*/

                //holder.video.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/eWEF1Zrmdow\"frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8");




        holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
        holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
//        CommentAdapter adapter = new CommentAdapter(context, post.getComments());
//        holder.listComments.setAdapter(adapter);

        UserDAO.getInstance().getUser(post.getUser(),new UserEvent(){
            @Override
            public void onSuccess(User user) {
                if(user.getProfilePhoto()!= null && !user.getProfilePhoto().isEmpty()){
                    Glide.with(mContext).load(user.getProfilePhoto()).into(holder.imgUser);
                }else{
                    holder.imgUser.setImageResource(R.drawable.default_perfil);
                }
                holder.txtName.setText(user.getName() + " "+user.getLastNameA());
            }
        });
    }


    public String urlToEmbeded(String url){

        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url);

        for(String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if(matcher.find()){
                return matcher.group(1);
            }
        }

        return null;

    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgUser;
        TextView txtName;
        TextView txtTime;
        TextView txtDescription;
        ImageView img;
        WebView video;
        LinearLayout multimediaFrame;
        TextView txtLikes;
        TextView txtDislikes;
        ImageView imgComments;
        ListView listComments;
        ImageView labelLikes;
        ImageView labelDislikes;

        ViewHolder(final View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgPostUser);
            txtName = itemView.findViewById(R.id.txtPostName);
            txtTime = itemView.findViewById(R.id.txtPostTime);
            txtDescription = itemView.findViewById(R.id.txtPostDescription);
            img = itemView.findViewById(R.id.imgPost);
            video = itemView.findViewById(R.id.videoPost);
            txtLikes = itemView.findViewById(R.id.txtPostLikes);
            txtDislikes = itemView.findViewById(R.id.txtPostDislikes);
            imgComments = itemView.findViewById(R.id.lblPostComments);
            listComments = itemView.findViewById(R.id.listPostComments);
            labelLikes = itemView.findViewById(R.id.lblLikes);
            labelDislikes = itemView.findViewById(R.id.lblDislikes);
            multimediaFrame = itemView.findViewById(R.id.frameMultimedia);
            multimediaFrame.setVisibility(View.GONE);

            itemView.setOnClickListener(this);

            imgComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listComments.getVisibility() == View.VISIBLE)
                        listComments.setVisibility(View.GONE);
                    else
                        listComments.setVisibility(View.VISIBLE);
                }
            });
            /*
            labelLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(), "Hice click en like", Toast.LENGTH_LONG).show();
                    Toast.makeText(itemView.getContext(), mData.get(0).getUser(), Toast.LENGTH_LONG).show();
                }
            });*/

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Post getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
