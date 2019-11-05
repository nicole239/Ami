package ec.tec.ami.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.model.Comentario;

public class CommentAdapter extends ArrayAdapter<Comentario> {

    public CommentAdapter(@NonNull Context context, List<Comentario> comments) {
        super(context, 0,comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Comentario comment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
        }
        // Lookup view for data population
        TextView txtName = (TextView) convertView.findViewById(R.id.txtCommentName);
        TextView txtTime = (TextView) convertView.findViewById(R.id.txtCommentDate);
        TextView txtComment = (TextView) convertView.findViewById(R.id.txtComment);
        ImageView imgView = (ImageView) convertView.findViewById(R.id.imgComment);
        // Populate the data into the template view using the data object
        txtName.setText(comment.getUsuario().getName());
        txtTime.setText(comment.getFecha().toString());
        txtComment.setText(comment.getComentario());
        //TODO: DISPLAY PHOTO PERFIL
        //imgView.setImageBitmap(comment.getUsuario().getProfilePhoto());

        // Return the completed view to render on screen
        return convertView;

    }
}
