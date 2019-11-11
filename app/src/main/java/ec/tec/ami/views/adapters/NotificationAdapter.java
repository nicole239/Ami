package ec.tec.ami.views.adapters;

import android.content.Context;
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
import ec.tec.ami.model.Notification;
import ec.tec.ami.model.User;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context context;
    private List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> friends) {
        this.context = context;
        this.notifications = friends;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ViewHolder holder, int position) {

        UserDAO.getInstance().getUser(notifications.get(position).getEmail(),new UserEvent(){
            @Override
            public void onSuccess(User user) {
                loadContent(holder,user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void loadContent(@NonNull NotificationAdapter.ViewHolder holder, User user){
        Glide.with(context)
                .load(user.getProfilePhoto())
                .into(holder.profile);
        holder.name.setText(user.getName() + " "+user.getLastNameA() + " "+user.getLastNameB());
    }

    public void clear(){
        notifications.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView friendsInCommon;
        Button accept, reject;
        CircleImageView profile;
        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.tvName);
            profile = view.findViewById(R.id.imgPhoto);
            friendsInCommon = view.findViewById(R.id.tvFriends);
            accept = view.findViewById(R.id.btnAccept);
            reject = view.findViewById(R.id.btnReject);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //Call to profile;
        }
    }

}
