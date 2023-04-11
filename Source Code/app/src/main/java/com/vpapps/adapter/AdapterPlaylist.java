package com.vpapps.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vpapps.interfaces.ClickListenerPlayList;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.item.ItemPlayList;
import com.vpapps.onlinemp3.R;
import com.vpapps.utils.DBHelper;

import java.util.ArrayList;


public class AdapterPlaylist extends RecyclerView.Adapter<AdapterPlaylist.MyViewHolder> {

    private DBHelper dbHelper;
    private Context context;
    private ArrayList<ItemPlayList> arrayList;
    private ArrayList<ItemPlayList> filteredArrayList;
    private NameFilter filter;
    private ClickListenerPlayList clickListenerPlayList;
    private Boolean isDialog;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView_playlist_title);
            imageView = view.findViewById(R.id.imageView_more_playlist);
        }
    }

    public AdapterPlaylist(Context context, ArrayList<ItemPlayList> arrayList, ClickListenerPlayList clickListenerPlayList, Boolean isDialog) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        this.clickListenerPlayList = clickListenerPlayList;
        this.isDialog = isDialog;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_playlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.textView.setText(arrayList.get(position).getName());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListenerPlayList.onClick(holder.getAdapterPosition());
            }
        });

        if(isDialog) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionPopUp(holder.imageView, holder.getAdapterPosition());
                }
            });
        }
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
                ArrayList<ItemPlayList> filteredItems = new ArrayList<>();

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

            arrayList = (ArrayList<ItemPlayList>) results.values;
            notifyDataSetChanged();
        }
    }

    private void openOptionPopUp(ImageView imageView, final int pos) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_playlist, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_option_playlist:
                        dbHelper.removePlayList(arrayList.get(pos).getId());
                        arrayList.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(context, context.getString(R.string.remove_playlist), Toast.LENGTH_SHORT).show();
                        if(arrayList.size() == 0) {
                            clickListenerPlayList.onItemZero();
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}