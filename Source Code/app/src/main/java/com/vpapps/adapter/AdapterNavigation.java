package com.vpapps.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.onlinemp3.R;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

public class AdapterNavigation extends RecyclerView.Adapter<AdapterNavigation.ViewHolder> {

    private Activity activity;
    private String[] name;
    private Integer[] image;
    private int row_index = 0;
    private RecyclerClickListener recyclerClickListener;

    public AdapterNavigation(Activity activity, String[] name, Integer[] image, RecyclerClickListener recyclerClickListener) {
        this.activity = activity;
        this.name = name;
        this.image = image;
        this.recyclerClickListener = recyclerClickListener;
        JsonUtils utils = new JsonUtils(activity);
        utils.getColumnWidth();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_navigation_child, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Picasso.get().load(image[position]).into(holder.imageView);
        holder.textView_Name.setText(name[position]);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Constant.columnWidth - 10, Constant.columnWidth - 10);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        holder.cardView.setLayoutParams(params);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerClickListener.onClick(holder.getAdapterPosition());
                if (holder.getAdapterPosition() < 8) {
                    row_index = holder.getAdapterPosition();
                    notifyDataSetChanged();
                }
            }
        });

        if (row_index == position) {
            holder.linearLayout.setBackgroundColor(activity.getResources().getColor(R.color.bg_navi_selected));
            holder.textView_Name.setTextColor(activity.getResources().getColor(R.color.bg_navi_text_selected));
            holder.imageView.setColorFilter(activity.getResources().getColor(R.color.bg_navi_image_selected));
        } else {
            holder.linearLayout.setBackgroundColor(activity.getResources().getColor(R.color.bg_navi_unselected));
            holder.textView_Name.setTextColor(activity.getResources().getColor(R.color.bg_navi_text_unselected));
            holder.imageView.setColorFilter(0);
        }
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView_Name;
        private LinearLayout linearLayout;
        private CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_main_adapter);
            textView_Name = (TextView) itemView.findViewById(R.id.textView_main_adapter);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_main_adapter);
            cardView = (CardView) itemView.findViewById(R.id.cardView_main_adapter);
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public void setSelectedTitle(int pos) {
        row_index = pos;
    }

}
