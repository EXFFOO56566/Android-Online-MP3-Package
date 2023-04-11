package com.vpapps.onlinemp3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vpapps.adapter.AdapterSongList;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.item.ItemSong;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.JsonUtils;
import com.vpapps.utils.SharedPref;
import com.vpapps.utils.ZProgressHUD;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FragmentDownloads extends Fragment {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    ArrayList<ItemSong> arrayList;
    public static AdapterSongList adapterSongList;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_downloads, container, false);

        dbHelper = new DBHelper(getActivity());

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_downloads);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        new LoadSongs().execute();
        return rootView;
    }

    private class LoadSongs extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressHUD.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            loadDownloaded();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (getActivity() != null) {
                progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

                adapterSongList = new AdapterSongList(getActivity(), arrayList, new RecyclerClickListener() {
                    @Override
                    public void onClick(int position) {
                        Constant.isOnline = false;
                        Constant.frag = "download";
                        Constant.arrayList_play.clear();
                        Constant.arrayList_play.addAll(arrayList);
                        Constant.playPos = position;
                        ((MainActivity) getActivity()).changeText(arrayList.get(position).getMp3Name(), arrayList.get(position).getArtist(), position + 1, arrayList.size(), arrayList.get(position).getDuration(), arrayList.get(position).getBitmap(), "download");

                        Constant.context = getActivity();
                        Intent intent = new Intent(getActivity(), PlayerService.class);
                        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                        getActivity().startService(intent);
                    }
                }, "offline");
                recyclerView.setAdapter(adapterSongList);
                if (arrayList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
                super.onPostExecute(s);
            }
        }
    }

    private void loadDownloaded() {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
        File[] songs = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        });

        if (songs != null) {
            for (int i = 0; i < songs.length; i++) {

                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(songs[i].getAbsolutePath());
                String title = songs[i].getName();
                String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                duration = JsonUtils.milliSecondsToTimerDownload(Long.parseLong(duration));
                String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String url = songs[i].getAbsolutePath();

                byte[] byte_image = md.getEmbeddedPicture();
                Bitmap songImage;
                if (byte_image != null) {
                    songImage = BitmapFactory.decodeByteArray(byte_image, 0, byte_image.length);
                } else {
                    songImage = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.app_icon);
                }

                String desc = getString(R.string.title) + " - " + title + "</br>" +
                        getString(R.string.artist) + " - " + artist;

                ItemSong itemSong = new ItemSong(String.valueOf(i), artist, url, songImage, title, duration, desc);
                arrayList.add(itemSong);
            }
        }
    }
}