package ec.tec.ami.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ec.tec.ami.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolderGallery> {

    private List<String> mData;
    private Context mContext;
    private LayoutInflater mInflater;


    public GalleryAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public ViewHolderGallery onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.gallery_item, parent, false);
        return new GalleryAdapter.ViewHolderGallery(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderGallery holder, int position) {
        String photoURL = mData.get(position);

        Glide.with(mContext).load(photoURL).into(holder.imgItem);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolderGallery extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgItem;


        ViewHolderGallery(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItemGallery);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
