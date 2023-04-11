package com.vpapps.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vpapps.item.ItemAlbums;
import com.vpapps.item.ItemArtist;
import com.vpapps.onlinemp3.R;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import java.util.ArrayList;


public class AdapterAlbum extends RecyclerView.Adapter<AdapterAlbum.MyViewHolder> {

    private Context context;
    private ArrayList<ItemAlbums> arrayList;
    private ArrayList<ItemAlbums> filteredArrayList;
    private NameFilter filter;
    private JsonUtils jsonUtils;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        View vieww;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView_artist_name);
            imageView = view.findViewById(R.id.imageView_artist);
            vieww = view.findViewById(R.id.view_artist);
        }
    }

    public AdapterAlbum(Context context, ArrayList<ItemAlbums> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        jsonUtils = new JsonUtils(context);
        jsonUtils.getColumnWidth();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_artist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(Constant.columnWidth, Constant.columnWidth));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Constant.columnWidth, Constant.columnWidth/2);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        holder.vieww.setLayoutParams(params);

        holder.textView.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(arrayList.get(position).getThumb())
                .into(holder.imageView);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemAlbums> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemAlbums>) results.values;
            notifyDataSetChanged();
        }
    }
}