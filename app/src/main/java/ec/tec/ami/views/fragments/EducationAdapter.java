package ec.tec.ami.views.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import ec.tec.ami.R;
import ec.tec.ami.model.Education;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder>{

    private Context context;
    private List<Education> list;
    private boolean editable;
    private SimpleDateFormat format;
    private ItemListener listener;

    public EducationAdapter(Context context, List<Education> list, boolean editable) {
        this.context = context;
        this.list = list;
        this.editable = editable;
        this.format = new SimpleDateFormat("dd/MM/yyyy");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.education_item,parent,false);
        return new EducationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Education education = list.get(position);
        holder.date.setText(format.format(education.getDate()));
        holder.institution.setText(education.getInstitute());
        if(!editable){
            holder.btnDelete.setVisibility(View.GONE);
        }else{
            if(listener != null){
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onDelete(position,education);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnDeleteItem(ItemListener listener){
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView institution;
        Button btnDelete;
        public ViewHolder(View view){
            super(view);
            date = view.findViewById(R.id.tvEducationDate);
            institution = view.findViewById(R.id.tvEducationInsitution);
            btnDelete = view.findViewById(R.id.btnDelete);
        }

    }

    public interface ItemListener{
        void onDelete(int position, Education item);
    }
}

