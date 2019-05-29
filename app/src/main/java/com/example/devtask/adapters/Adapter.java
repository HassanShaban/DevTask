package com.example.devtask.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devtask.R;
import com.example.devtask.models.TaskModel;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> implements Filterable {


     ArrayList<TaskModel> data , data_format;
     Context context;

    public Adapter(ArrayList<TaskModel> data, Context context) {
        this.data = data;
        this.data_format = data;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = parent;
        Holder holder;
        v = LayoutInflater.from(context).inflate(R.layout.adapter , parent , false);
        holder = new Holder(v);
        return  holder;

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {


        holder.repo_name.setText(data.get(position).getName());
        holder.description.setText(data.get(position).getDescription());
        holder.user_name.setText(data.get(position).getOwner().getLogin());

        if(!data.get(position).isFork()){

            holder.linear.setBackgroundColor(context.getResources().getColor(R.color.greencolor));
        }

        else {

            holder.linear.setBackgroundColor(Color.WHITE);

        }


    }

    @Override
    public int getItemCount() {

        return data_format.size();

    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                // user clear searchView
                if (charString.isEmpty()) {

                     data_format = data;
                }

                else {

                    ArrayList<TaskModel> filteredList = new ArrayList<>();

                        for (TaskModel row : data) {

                            if (row.getOwner().getLogin().toLowerCase().contains(charString))
                                filteredList.add(row);

                        }
                    // set Result to Array
                    data_format = filteredList;


                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = data_format;


                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                data_format = (ArrayList<TaskModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView repo_name , description , user_name ;
        LinearLayout linear;

        public Holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            initialzing();
        }

        private void initialzing() {

            repo_name = itemView.findViewById(R.id.repo_name);
            description = itemView.findViewById(R.id.description);
            user_name = itemView.findViewById(R.id.user_name);
            linear = itemView.findViewById(R.id.linear);

        }

        @Override
        public boolean onLongClick(View v) {

                TaskDialog taskDialog = new TaskDialog(context , data_format.get(getLayoutPosition()).getHtml_url() , data_format.get(getLayoutPosition()).getOwner().getHtml_url() );
                taskDialog.setCanceledOnTouchOutside(false);
                taskDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                taskDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                taskDialog.show();
                return false;

        }
    }


    class TaskDialog extends Dialog implements View.OnClickListener {

        LayoutInflater inflator;
        String repoUrl , ownerUrl;
         View view;
        public TaskDialog( @NonNull Context context , String repoUrl , String ownerUrl) {
            super(context);
            this.repoUrl = repoUrl;
            this.ownerUrl = ownerUrl;
        }



        Button owner_url , repo_url;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            inflator = getLayoutInflater();
            view = inflator.inflate(R.layout.dialog, null , false);
            setContentView(view);
            owner_url = view.findViewById(R.id.owner_url);
            repo_url = view.findViewById(R.id.repo_url);
            owner_url.setOnClickListener(this);
            repo_url.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.repo_url:
                    Intent browserIntent1 = new Intent("android.intent.action.VIEW", Uri.parse(repoUrl));
                    context.startActivity(browserIntent1);
                    break;

                case R.id.owner_url:
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(ownerUrl));
                    context.startActivity(browserIntent);
                    break;

            }

        }
    }

}
