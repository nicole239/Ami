package ec.tec.ami.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.tec.ami.R;
import ec.tec.ami.data.dao.PostDAO;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.PostEvent;
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
        final Post post = mData.get(position);

        //TODO: MOSTRAR FOTO PERFIL
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
        if(post.checkUserLike(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            String hola = String.valueOf(post.getTotalLikes());
            holder.txtLikes.setText(hola+" You liked this ");
        }
        else{
            holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
        }

        if(post.checkUserDislike(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            String dislikes = String.valueOf(post.getTotalDislikes());
            holder.txtDislikes.setText(dislikes + "You disliked this");
        }
        else{
            holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
        }
        
        holder.labelLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PostDAO.getInstance().likePost(mData.get(position),new PostEvent(){
                    @Override
                    public void onSuccess(boolean status) {
                        if(status){
                            Toast.makeText(view.getContext(), "Likes has beed added", Toast.LENGTH_LONG).show();
                            holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
                        }else {
                            Toast.makeText(view.getContext(), "Something went wrond", Toast.LENGTH_LONG).show();
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
                            holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
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
        RelativeLayout multimediaFrame;
        TextView txtLikes;
        TextView txtDislikes;
        ImageView imgComments;
        ListView listComments;
        ImageView labelLikes;
        ImageView labelDislikes;

        @SuppressLint("SetJavaScriptEnabled")
        ViewHolder(final View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgPostUser);
            txtName = itemView.findViewById(R.id.txtPostName);
            txtTime = itemView.findViewById(R.id.txtPostTime);
            txtDescription = itemView.findViewById(R.id.txtPostDescription);
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
