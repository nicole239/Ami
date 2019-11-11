package ec.tec.ami.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ec.tec.ami.R;
import ec.tec.ami.data.dao.UserDAO;
import ec.tec.ami.data.event.UserEvent;
import ec.tec.ami.model.User;
import ec.tec.ami.views.activities.ShowProfileActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context context;
    private List<User> friends;
    private boolean editable = false;
    private UserListener listener;

    public UserAdapter(Context context, List<User> friends) {
        this.context = context;
        this.friends = friends;
    }

    public void setEditable(boolean editable){
        this.editable = editable;
    }

    public void setListener(UserListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, int position) {
        if(friends.get(position).getName() == null || friends.get(position).getName().isEmpty()){
            UserDAO.getInstance().getUser(friends.get(position).getEmail(),new UserEvent(){
                @Override
                public void onSuccess(User user) {
                    loadContent(holder,user);
                }
            });
        }else{
            loadContent(holder,friends.get(position));
        }
        if (editable) {
            holder.btnClose.setVisibility(View.VISIBLE);
        } else {
            holder.btnClose.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    private void loadContent(@NonNull UserAdapter.ViewHolder holder, final User user){
        Glide.with(context)
                .load(user.getProfilePhoto())
                .into(holder.profile);
        holder.name.setText(user.getName() + " "+user.getLastNameA() + " "+user.getLastNameB());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowProfileActivity.class);
                intent.putExtra("showPerfilUser", user.getEmail());
                context.startActivity(intent);
            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowProfileActivity.class);
                intent.putExtra("showPerfilUser", user.getEmail());
                context.startActivity(intent);
            }
        });
    }

    public void clear(){
        friends.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        CircleImageView profile;
        Button btnClose;
        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.tvName);
            profile = view.findViewById(R.id.imgPhoto);
            btnClose = view.findViewById(R.id.btnDelete);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener !=null){
                        listener.onDelete(getAdapterPosition());
                    }
                }
            });
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Call to profile;
        }
    }

    public interface UserListener
    {
        void onDelete(int position);
    }
}
