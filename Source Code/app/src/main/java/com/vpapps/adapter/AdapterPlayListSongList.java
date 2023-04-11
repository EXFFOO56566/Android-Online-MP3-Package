package com.vpapps.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vpapps.interfaces.ClickListenerPlayList;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.item.ItemPlayList;
import com.vpapps.item.ItemSong;
import com.vpapps.onlinemp3.R;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;


public class AdapterPlayListSongList extends RecyclerView.Adapter<AdapterPlayListSongList.MyViewHolder> {

    private DBHelper dbHelper;
    private Context context;
    private ArrayList<ItemSong> arrayList;
    private ArrayList<ItemSong> filteredArrayList;
    private ClickListenerPlayList clickListenerPlayList;
    private NameFilter filter;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_song, textView_duration, textView_catname, textView_total_rate, textView_views, textView_downloads;
        EqualizerView equalizer;
        ImageView imageView, imageView_option;
        LinearLayout linearLayout;
        RatingBar ratingBar;

        MyViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.ll_songlist);
            textView_song = view.findViewById(R.id.textView_songname);
            textView_duration = view.findViewById(R.id.textView_songduration);
            equalizer = view.findViewById(R.id.equalizer_view);
            textView_catname = view.findViewById(R.id.textView_catname);
            textView_total_rate = view.findViewById(R.id.textView_totalrate_songlist);
            imageView = view.findViewById(R.id.imageView_songlist);
            imageView_option = view.findViewById(R.id.imageView_option_songlist);
            ratingBar = view.findViewById(R.id.ratingBar_songlist);
            textView_views = view.findViewById(R.id.textView_views);
            textView_downloads = view.findViewById(R.id.textView_downloads);
        }
    }

    public AdapterPlayListSongList(Context context, ArrayList<ItemSong> arrayList, ClickListenerPlayList clickListenerPlayList) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        dbHelper = new DBHelper(context);
        this.clickListenerPlayList = clickListenerPlayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_songlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

//        holder.textView_total_rate.setVisibility(View.GONE);
//        holder.ratingBar.setVisibility(View.GONE);
        holder.textView_total_rate.setText(arrayList.get(position).getTotalRate());
        holder.ratingBar.setRating(Float.parseFloat(arrayList.get(position).getAverageRating()));
        holder.textView_song.setText(arrayList.get(position).getMp3Name());
        holder.textView_duration.setText(arrayList.get(position).getDuration());
        holder.textView_views.setText(arrayList.get(position).getViews());
        holder.textView_downloads.setText(arrayList.get(position).getDownloads());

        Picasso.get()
                .load(arrayList.get(position).getImageSmall())
                .into(holder.imageView);

        if (Constant.isPlaying && Constant.arrayList_play.get(Constant.playPos).getId().equals(arrayList.get(position).getId())) {
            holder.equalizer.animateBars();
            holder.equalizer.setVisibility(View.VISIBLE);
        } else {
            holder.equalizer.stopBars();
            holder.equalizer.setVisibility(View.GONE);
        }

        if (arrayList.get(position).getCategoryName() != null) {
            holder.textView_catname.setText(arrayList.get(position).getCategoryName());
        } else {
            holder.textView_catname.setText(arrayList.get(position).getArtist());
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListenerPlayList.onClick(holder.getAdapterPosition());
            }
        });

        holder.imageView_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptionPopUp(holder.imageView_option, holder.getAdapterPosition());
            }
        });
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

    private void openOptionPopUp(ImageView imageView, final int pos) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_song, popup.getMenu());
        popup.getMenu().findItem(R.id.popup_add_song).setTitle(context.getString(R.string.remove));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_add_song:
                        dbHelper.removeFromPlayList(arrayList.get(pos).getId());
                        arrayList.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(context, context.getString(R.string.remove_from_playlist), Toast.LENGTH_SHORT).show();
                        if (arrayList.size() == 0) {
                            clickListenerPlayList.onItemZero();
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
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
                ArrayList<ItemSong> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getMp3Name();
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

            arrayList = (ArrayList<ItemSong>) results.values;
            notifyDataSetChanged();
        }
    }
}