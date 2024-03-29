package ec.tec.ami.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostDAO;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.PostEvent;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.Comment;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;
import ec.tec.ami.model.User;
import ec.tec.ami.views.activities.DialogDecision;
import ec.tec.ami.views.activities.ShowCommentsActivity;
import ec.tec.ami.views.activities.ShowProfileActivity;
import ec.tec.ami.views.utils.ReadableDateFormat;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};
    private PostAdapter adapter;

    // data is passed into the constructor
    public PostAdapter(Context context, List<Post> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
        this.adapter = this;
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
        final Post post = mData.get(position);
        String email = "";
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        if(email.isEmpty()){
            return;
        }

        //holder.imgUser.setImageBitmap(post.getUsuario().getImage());
        holder.multimediaFrame.setVisibility(View.GONE);
        holder.txtName.setText(post.getUser());
        holder.txtTime.setText(ReadableDateFormat.toHumanFormat(post.getDate()));
        holder.txtDescription.setText(post.getDescription());
        if(post.getMedia() != null && post.getType() != Type.TEXT && !post.getMedia().isEmpty()){
            holder.multimediaFrame.setVisibility(View.VISIBLE);
            if(post.getType() == Type.PHOTO){
                holder.img.setVisibility(View.VISIBLE);
                holder.video.setVisibility(View.GONE);
                Glide.with(mContext).load(post.getMedia()).into(holder.img);
            }else if (post.getType() == Type.VIDEO){
                holder.img.setVisibility(View.GONE);
                holder.video.setVisibility(View.VISIBLE);

                String videoID = urlToEmbeded(post.getMedia());

                holder.video.loadUrl("about:blank");
                String urll = "https://www.youtube.com/embed/"+videoID;
                holder.video.loadDataWithBaseURL(null,"<iframe width=\"100%\" height=\"100%\" src=\""+urll+"\" frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8",null);
                holder.video.measure(100,100);
                holder.video.getSettings().setLoadWithOverviewMode(true);
//=======
//                //String videoID = urlToEmbeded(post.getMedia());
//                String videoID = "aatr_2MstrI";
//                String urll = "https://www.youtube.com/embed/"+videoID;
//                //holder.video.loadData("<iframe width=\"100%\" height=\"100%\" src=\""+urll+"\"frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8");
//                holder.video.loadDataWithBaseURL(null,"<iframe width=\"100%\" height=\"100%\" src=\""+urll+"\"frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8",null);
//>>>>>>> 3166a8b483098b9b3105cf2413b691c536258721

                Log.d("Video", urll);
            }
        }
        if(post.checkUserLike(email)){
            String hola = String.valueOf(post.getTotalLikes());
            holder.txtLikes.setText(hola+" You liked this ");
        }
        else{
            holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
        }

        if(post.checkUserDislike(email)){
            String dislikes = String.valueOf(post.getTotalDislikes());
            holder.txtDislikes.setText(dislikes + "You disliked this");
        }
        else{
            holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
        }

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowProfileActivity.class);
                Log.d("hola", mData.get(position).getUser());
                intent.putExtra("showPerfilUser", mData.get(position).getUser());
                mContext.startActivity(intent);
            }
        });

        PostDAO.getInstance().loadCommentNum(post, new PostEvent(){
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext,"Error en cargar Comments",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int numComments) {
                holder.postCommentsNum.setText(String.valueOf(numComments));
            }
        });

        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowProfileActivity.class);
                Log.d("hola", mData.get(position).getUser());
                intent.putExtra("showPerfilUser", mData.get(position).getUser());
                mContext.startActivity(intent);
            }
        });

        final String finalEmail = email;
        holder.labelLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {



            PostDAO.getInstance().likePost(mData.get(position),new PostEvent(){
                @Override
                public void onSuccess(boolean status) {
                    if(status){
                        Toast.makeText(view.getContext(), "Likes has beed added", Toast.LENGTH_LONG).show();
                        holder.txtLikes.setText(post.getTotalLikes()+" You liked this");

                        PostDAO.getInstance().deleteDislike(finalEmail,mData.get(position),new PostEvent(){
                            @Override
                            public void onSuccess(boolean status) {
                                if(status){
                                    if(post.getDislikes().size()>0){
                                        post.getDislikes().remove(finalEmail);
                                        holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                super.onFailure(e);
                            }
                        });

                    }else {
                        Toast.makeText(view.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onRepeated() {
                    Toast.makeText(view.getContext(), "Ya le di like", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(view.getContext(), "Failure on addind likes", Toast.LENGTH_LONG).show();
                }
            });
            }
        });


        holder.labelDislikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PostDAO.getInstance().dislikePost(post,new PostEvent(){
                    @Override
                    public void onRepeated() {
                        Toast.makeText(view.getContext(), "Ya le di dislike", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(boolean status) {
                        if(status){
                            Toast.makeText(view.getContext(), "Disikes has beed added", Toast.LENGTH_LONG).show();
                            holder.txtDislikes.setText(post.getTotalDislikes()+" You dislike this");

                            PostDAO.getInstance().deleteLike(finalEmail,mData.get(position), new PostEvent(){
                                @Override
                                public void onSuccess(boolean status) {
                                    if(status){
                                        if(post.getLikes().size()>0){
                                            post.getLikes().remove(finalEmail);
                                            holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    super.onFailure(e);
                                }
                            });

                        }else {
                            Toast.makeText(view.getContext(), "Something went wrond", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(view.getContext(), "Failure on addind likes", Toast.LENGTH_LONG).show();
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




        //holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
        //holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
//        CommentAdapter adapter = new CommentAdapter(context, post.getComments());
//        holder.listComments.setAdapter(adapter);

        holder.imgComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowCommentsActivity.class);
                intent.putExtra("ID",post.getId());
                mContext.startActivity(intent);
//                if(holder.listComments.getVisibility() == View.VISIBLE) {
//                    holder.listComments.setVisibility(View.GONE);
//                    holder.layoutComment.setVisibility(View.GONE);
//                    Log.d("hola", "entre");
//
//                }
//                else {
//                    holder.listComments.setVisibility(View.VISIBLE);
//                    holder.layoutComment.setVisibility(View.VISIBLE);
//                    FirebaseDatabase firebaseDatabasee = FirebaseDatabase.getInstance();
//                    DatabaseReference reference = firebaseDatabasee.getReference().child("posts").child(post.getId()).child("comentarios");
//
//                    reference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            List<Comment> comments = new ArrayList<Comment>();
//                            for (DataSnapshot child : dataSnapshot.getChildren()){
//                                comments.add(child.getValue(Comment.class));
//                            }
//                            Log.d("hola", "numero es: "+comments.size());
//
//                            for(int i=0; i<comments.size();i++){
//                                Log.d("hola",comments.get(i).getComment());
//                            }
//
//                            CommentAdapter adapter = new CommentAdapter(mContext, comments);
//                            holder.listComments.setAdapter(adapter);
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }

                Toast.makeText(mContext,"numero de coments: "+post.getComments().size(),Toast.LENGTH_LONG).show();
            }
        });


        holder.userCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDAO.getInstance().getUser(finalEmail, new UserEvent(){

                    @Override
                    public void onSuccess(User user) {
                        String detalle = holder.userCommentDetail.getText().toString();
                        Date date = Calendar.getInstance().getTime();
                        Comment comment = new Comment(user, date,detalle);
                        String id = post.getId();

                        PostDAO.getInstance().addComment(id,comment,new PostEvent(){
                            @Override
                            public void onSuccess(boolean status) {
                                if(status){
                                    Toast.makeText(mContext,"Added Comment",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(mContext,"something went wrong",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(mContext,"Failure to add comment",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

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

        String currentMail =  finalEmail;
        if(currentMail.equals(post.getUser())){
            holder.btnDeletePost.setVisibility(View.VISIBLE);
        }else{
            holder.btnDeletePost.setVisibility(View.INVISIBLE);
        }

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
        ImageView imgUser, img, labelLikes, labelDislikes, imgComments;
        TextView txtName, txtTime, txtDescription, txtLikes, txtDislikes,userCommentNameTxt, postCommentsNum;
        WebView video;
        RelativeLayout multimediaFrame, layoutComment;
        ListView listComments;
        EditText userCommentDetail;
        Button userCommentSend, btnDeletePost;
        

        @SuppressLint("SetJavaScriptEnabled")
        ViewHolder(final View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgPostUser);
            txtName = itemView.findViewById(R.id.txtPostName);
            txtTime = itemView.findViewById(R.id.txtPostTime);
            txtDescription = itemView.findViewById(R.id.txtPostDescription);
            postCommentsNum = itemView.findViewById(R.id.txtPostCommentsNum);
            img = itemView.findViewById(R.id.imgPost);
            video = itemView.findViewById(R.id.videoPost);
            video.getSettings().setJavaScriptEnabled(true);
            video.setWebChromeClient(new WebChromeClient(){

            });
            video.getSettings().setDomStorageEnabled(true);
            txtLikes = itemView.findViewById(R.id.txtPostLikes);
            txtDislikes = itemView.findViewById(R.id.txtPostDislikes);
            imgComments = itemView.findViewById(R.id.lblPostComments);
            listComments = itemView.findViewById(R.id.listPostComments);
            labelLikes = itemView.findViewById(R.id.lblLikes);
            labelDislikes = itemView.findViewById(R.id.lblDislikes);
            multimediaFrame = itemView.findViewById(R.id.frameMultimedia);
            multimediaFrame.setVisibility(View.GONE);
            layoutComment = itemView.findViewById(R.id.layout_comment_user);
            userCommentNameTxt = itemView.findViewById(R.id.userTxtCommentName);
            userCommentDetail = itemView.findViewById(R.id.userCommentEditText);
            userCommentSend = itemView.findViewById(R.id.userComentarioSendBtn);
            layoutComment.setVisibility(View.GONE);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);

            video.setWebViewClient(new WebViewClient());
            video.setWebChromeClient(new WebChromeClient());
            video.getSettings().setJavaScriptEnabled(true);
            video.getSettings().setDomStorageEnabled(true);



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

            btnDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePost(getAdapterPosition());
                }
            });

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

    private void deletePost(final int position){
        final Post postToDelete = mData.get(position);
        DialogDecision dialogDecision = new DialogDecision(mContext, "Confirmation", "Do you want to delete this post?", new DialogDecision.DialogResult() {
            @Override
            public void onConfirm() {
                PostDAO.getInstance().deletePost( postToDelete, new PostEvent(){
                    @Override
                    public void onSuccess(boolean status) {
                        if(status){
                            Toast.makeText(mContext,"Post deleted",Toast.LENGTH_LONG).show();
                            mData.remove(position);
                            adapter.notifyDataSetChanged();

                        }
                        else{
                            Toast.makeText(mContext,"Failed",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(mContext,"Failed to delete post",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });
        dialogDecision.show();

    }
}
