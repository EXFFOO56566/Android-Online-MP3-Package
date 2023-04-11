package com.vpapps.onlinemp3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.vpapps.AsyncTask.LoadServerPlaylistSong;
import com.vpapps.AsyncTask.LoadSong;
import com.vpapps.adapter.AdapterSongList;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.interfaces.SongListener;
import com.vpapps.item.ItemSong;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.JsonUtils;
import com.vpapps.utils.ZProgressHUD;

import java.util.ArrayList;

public class FragmentSongByAlbums extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ItemSong> arrayList;
    public static AdapterSongList adapterSongList;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager;
    String name = "", id = "", type = "";
    LoadSong loadSong;
    DBHelper dbHelper;
    LinearLayout ll_empty;
    String errr_msg;
    TextView textView_empty;
    Button button_try;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_song_by_cat, container, false);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        type = getArguments().getString("type");
        id = getArguments().getString("id");
        name = getArguments().getString("name");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(name);

        arrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_songbycat);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSongAPI();
            }
        });

        callSongAPI();

        return rootView;
    }

    private void callSongAPI() {
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            if (type.equals(getString(R.string.my_playlist))) {
                dbHelper = new DBHelper(getActivity());
                arrayList.addAll(dbHelper.loadDataPlaylist(id));
                errr_msg = getString(R.string.no_data_found);
                setAdapter();
                setEmpty();
            } else if (type.equals(getString(R.string.playlist))) {
                getServerPlaylist();
            } else {
                getSongList();
            }
        } else {
//            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            errr_msg = getString(R.string.internet_not_conn);
            setEmpty();
        }
    }

    private void getSongList() {
        loadSong = new LoadSong(new SongListener() {
            @Override
            public void onStart() {
                arrayList.clear();
                ll_empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                progressHUD.show();
            }

            @Override
            public void onEnd(String success, ArrayList<ItemSong> arrayListSong) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        if (arrayListSong != null) {
                            arrayList.addAll(arrayListSong);
                        }
                        progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

                        setAdapter();
                        errr_msg = getString(R.string.no_data_found);
                    } else {
                        progressHUD.dismissWithFailure(getResources().getString(R.string.error));
//                        Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                        errr_msg = getString(R.string.server_no_conn);
                    }

                    setEmpty();
                }
            }
        });
        String url = "";
        if (type.equals(getString(R.string.albums))) {
            url = Constant.URL_SONG_BY_ALBUMS + id;
        } else if (type.equals(getString(R.string.artist))) {
            url = Constant.URL_SONG_BY_ARTIST + name.replace(" ","%20");
        } else if (type.equals(getString(R.string.category))) {
            url = Constant.URL_SONG_BY_CAT + id;
        }
        loadSong.execute(url);
    }

    private void getServerPlaylist() {
        LoadServerPlaylistSong loadSong = new LoadServerPlaylistSong(new SongListener() {
            @Override
            public void onStart() {
                arrayList.clear();
                ll_empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                progressHUD.show();
            }

            @Override
            public void onEnd(String success, ArrayList<ItemSong> arrayListSong) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        if (arrayListSong != null) {
                            arrayList.addAll(arrayListSong);
                        }
                        progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

                        setAdapter();
                        errr_msg = getString(R.string.no_data_found);
                    } else {
                        progressHUD.dismissWithFailure(getResources().getString(R.string.error));
//                        Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();errr_msg = getString(R.string.server_no_conn);
                        errr_msg = getString(R.string.server_no_conn);
                    }

                    setEmpty();
                }
            }
        });

        loadSong.execute(Constant.URL_SONG_BY_PLAYLIST + id);
    }

    private void setAdapter() {
        adapterSongList = new AdapterSongList(getActivity(), arrayList, new RecyclerClickListener() {
            @Override
            public void onClick(int position) {
                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    showInter(position);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        }, "online");
        recyclerView.setAdapter(adapterSongList);
        if (arrayList.size() > 0 && Constant.arrayList_play.size() == 0) {
            Constant.isAppFirst = false;
            Constant.arrayList_play.addAll(arrayList);
            ((MainActivity) getActivity()).changeText(arrayList.get(0).getMp3Name(), arrayList.get(0).getCategoryName(), 1, arrayList.size(), arrayList.get(0).getDuration(), arrayList.get(0).getImageBig(), arrayList.get(0).getAverageRating(), "home");
            Constant.context = getActivity();
        }
    }

    private void setEmpty() {
        if(arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private void showInter(final int pos) {
        Constant.adCount = Constant.adCount + 1;
        if (Constant.adCount % Constant.adDisplay == 0) {
            ((MainActivity) getActivity()).mInterstitial.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    playIntent(pos);
                    super.onAdClosed();
                }
            });
            if (((MainActivity) getActivity()).mInterstitial.isLoaded()) {
                ((MainActivity) getActivity()).mInterstitial.show();
            } else {
                playIntent(pos);
            }
            ((MainActivity) getActivity()).loadInter();
        } else {
            playIntent(pos);
        }
    }

    private void playIntent(int position) {
        Constant.isOnline = true;
        Constant.frag = "alb";
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(arrayList);
        Constant.playPos = position;
        ((MainActivity) getActivity()).changeText(arrayList.get(position).getMp3Name(), arrayList.get(position).getCategoryName(), position + 1, arrayList.size(), arrayList.get(position).getDuration(), arrayList.get(position).getImageBig(), arrayList.get(position).getAverageRating(), "artist");

        Constant.context = getActivity();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
        getActivity().startService(intent);
    }
}