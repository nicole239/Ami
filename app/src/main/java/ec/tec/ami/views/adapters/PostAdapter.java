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
import android.widget.VideoView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ec.tec.ami.R;
import ec.tec.ami.model.Post;
import ec.tec.ami.model.Type;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

    // data is passed into the constructor
    public PostAdapter(Context context, List<Post> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post post = mData.get(position);

        //TODO: MOSTRAR FOTO PERFIL
        //holder.imgUser.setImageBitmap(post.getUsuario().getImage());
        holder.txtName.setText(post.getUser());
        holder.txtTime.setText(post.getDate().toString());
        holder.txtDescription.setText(post.getDescription());
        //TODO: DISTINGUIR VIDEO/IMAGEN EN POST

        if(post.getMedia() != null){
            holder.multimediaFrame.setVisibility(View.VISIBLE);
            if(post.getType() == Type.PHOTO){
                //TODO: MOSTRAR IMAGEN
                holder.img.setVisibility(View.VISIBLE);
            }else if (post.getType() == Type.VIDEO){
                //TODO: MOSTRAR VIDEO EN YOUTUBE
                holder.video.setVisibility(View.VISIBLE);
                holder.video.getSettings().setJavaScriptEnabled(true);
                holder.video.setWebChromeClient(new WebChromeClient(){

                });/*
                if(post.getMedia()!=""){
                    String embed = "iframe width=\"100%\" height=\"100%\" src=\" post.getMedia()\"frameborder=\"0\" allowfullscreen></iframe>";
                    holder.video.loadData(embed, "text/html","utf-8");
                }
*/
                String videoID = urlToEmbeded(post.getMedia());

                String urll = "https://www.youtube.com/embed/"+videoID;
               holder.video.loadData("<iframe width=\"100%\" height=\"100%\" src=\""+urll+"\"frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8");
                //holder.video.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/eWEF1Zrmdow\"frameborder=\"0\" allowfullscreen></iframe>", "text/html","utf-8");
                
            }

        }
        holder.txtLikes.setText(String.valueOf(post.getTotalLikes()));
        holder.txtDislikes.setText(String.valueOf(post.getTotalDislikes()));
        //CommentAdapter adapter = new CommentAdapter(context, post.getComments());
        //holder.listComments.setAdapter(adapter);
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

        ViewHolder(View itemView) {
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
            multimediaFrame = itemView.findViewById(R.id.frameMultimedia);

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

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
