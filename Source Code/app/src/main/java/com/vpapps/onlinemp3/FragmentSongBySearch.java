package com.vpapps.onlinemp3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vpapps.adapter.AdapterSongList;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.item.ItemSong;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;
import com.vpapps.utils.ZProgressHUD;
import com.google.android.gms.ads.AdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSongBySearch extends Fragment {


    RecyclerView recyclerView;
    ArrayList<ItemSong> arrayList;
    public static AdapterSongList adapterSongList;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager;
    SearchView searchView;
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

        if (getActivity() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.search));
        }

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
                getSongList();
            }
        });

        getSongList();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            if (JsonUtils.isNetworkAvailable(getActivity())) {
                Constant.search_item = s.replace(" ","%20");
                arrayList.clear();
                adapterSongList.notifyDataSetChanged();
                new LoadSongs().execute(Constant.URL_SEARCH + Constant.search_item);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void getSongList() {
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadSongs().execute(Constant.URL_SEARCH + Constant.search_item);
        } else {
//            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            errr_msg = getString(R.string.internet_not_conn);
            setEmpty();
        }
    }

    private class LoadSongs extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressHUD.show();
            ll_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);

                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_ID);
                    String cid = objJson.getString(Constant.TAG_CAT_ID);
                    String cname = objJson.getString(Constant.TAG_CAT_NAME);
                    String artist = objJson.getString(Constant.TAG_ARTIST);
                    String name = objJson.getString(Constant.TAG_SONG_NAME);
                    String url = objJson.getString(Constant.TAG_MP3_URL);
                    String desc = objJson.getString(Constant.TAG_DESC);
                    String duration = objJson.getString(Constant.TAG_DURATION);
                    String total_rate = objJson.getString(Constant.TAG_TOTAL_RATE);
                    String avg_rate = objJson.getString(Constant.TAG_AVG_RATE);
                    String thumb = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    String thumb_small = objJson.getString(Constant.TAG_THUMB_S).replace(" ", "%20");
                    String views = objJson.getString(Constant.TAG_VIEWS);
                    String downloads = objJson.getString(Constant.TAG_DOWNLOADS);

                    ItemSong objItem = new ItemSong(id, cid, cname, artist, url, thumb, thumb_small, name, duration, desc, total_rate, avg_rate,views,downloads);
                    arrayList.add(objItem);
                }

                return "1";
            } catch (JSONException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception ee) {
                ee.printStackTrace();
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (getActivity() != null) {
                if (s.equals("1")) {
                    progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

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
                    errr_msg = getString(R.string.no_data_found);
                } else {
                    progressHUD.dismissWithFailure(getResources().getString(R.string.error));
//                    Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                    errr_msg = getString(R.string.server_no_conn);
                }

                setEmpty();
                super.onPostExecute(s);
            }
        }
    }

    public void setEmpty() {
        if(arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private int getPosition(String id) {
        int count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
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
        Constant.frag = "search";
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(arrayList);
        Constant.playPos = getPosition(adapterSongList.getID(position));
        ((MainActivity) getActivity()).changeText(arrayList.get(position).getMp3Name(), arrayList.get(position).getCategoryName(), position + 1, arrayList.size(), arrayList.get(position).getDuration(), arrayList.get(position).getImageBig(), arrayList.get(position).getAverageRating(), "artist");

        Constant.context = getActivity();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
        getActivity().startService(intent);
    }
}