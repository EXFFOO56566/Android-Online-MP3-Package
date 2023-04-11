package com.vpapps.onlinemp3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vpapps.adapter.AdapterArtistLatest;
import com.vpapps.adapter.AdapterRecent;
import com.vpapps.item.ItemArtist;
import com.vpapps.item.ItemSong;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.JsonUtils;
import com.vpapps.utils.RecyclerItemClickListener;
import com.vpapps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    DBHelper dbHelper;
    RecyclerView recyclerView, recyclerView_artist;
    ArrayList<ItemSong> arrayList, arrayList_recent;
    ArrayList<ItemArtist> arrayList_artist;
    AdapterRecent adapterRecent;
    AdapterArtistLatest adapterArtistLatest;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager, llm_artist;
    public ViewPager viewpager;
    ImagePagerAdapter adapter;
    TextView textView_empty, textView_empty_artist;
    TextView textView_empty_msg;
    Button button_try;
    LinearLayout ll_empty, ll_home;
    String errr_msg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        dbHelper = new DBHelper(getActivity());

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        textView_empty = rootView.findViewById(R.id.textView_recent_empty);
        textView_empty_artist = rootView.findViewById(R.id.textView_artist_empty);

        adapter = new ImagePagerAdapter();
        viewpager = rootView.findViewById(R.id.viewPager_home);
        viewpager.setPadding(40, 10, 40, 10);
        viewpager.setClipToPadding(false);
        viewpager.setPageMargin(20);
        viewpager.setClipChildren(false);

        arrayList = new ArrayList<>();
        arrayList_recent = new ArrayList<>();
        arrayList_artist = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.recyclerView_home_recent);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        recyclerView_artist = rootView.findViewById(R.id.recyclerView_home_artist);
        llm_artist = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_artist.setLayoutManager(llm_artist);
        recyclerView_artist.setItemAnimator(new DefaultItemAnimator());
        recyclerView_artist.setHasFixedSize(true);

        ll_home = rootView.findViewById(R.id.ll_home);
        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty_msg = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadLatestNews().execute(Constant.URL_LATEST);
        } else {
//            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            errr_msg = getString(R.string.internet_not_conn);
            setEmpty();
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    Constant.isOnline = true;
                    Constant.arrayList_play.clear();
                    Constant.arrayList_play.addAll(arrayList_recent);
                    Constant.playPos = position;
                    ((MainActivity) getActivity()).changeText(arrayList_recent.get(position).getMp3Name(), arrayList_recent.get(position).getCategoryName(), position + 1, arrayList_recent.size(), arrayList_recent.get(position).getDuration(), arrayList_recent.get(position).getImageBig(), arrayList_recent.get(position).getAverageRating(), "home");

                    Constant.context = getActivity();
                    if (position == 0) {
                        Intent intent = new Intent(getActivity(), PlayerService.class);
                        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                        getActivity().startService(intent);
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        }));

        recyclerView_artist.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    FragmentManager fm = getFragmentManager();
                    FragmentSongByAlbums f1 = new FragmentSongByAlbums();
                    FragmentTransaction ft = fm.beginTransaction();

                    Bundle bundl = new Bundle();

                    bundl.putString("type", getString(R.string.artist));
                    bundl.putString("id", arrayList_artist.get(position).getId());
                    bundl.putString("name", arrayList_artist.get(position).getName());
                    f1.setArguments(bundl);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.hide(getFragmentManager().findFragmentByTag(getResources().getString(R.string.home)));
                    ft.add(R.id.fragment, f1, arrayList_artist.get(position).getName());
                    ft.addToBackStack(arrayList_artist.get(position).getName());
                    ft.commit();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        }));

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    new LoadLatestNews().execute(Constant.URL_LATEST);
                } else {
//            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                    errr_msg = getString(R.string.internet_not_conn);
                    setEmpty();
                }
            }
        });

        return rootView;
    }

    private class LoadLatestNews extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            ll_empty.setVisibility(View.GONE);
            ll_home.setVisibility(View.VISIBLE);
            progressHUD.show();
            arrayList.clear();
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
                    String image = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    String image_small = objJson.getString(Constant.TAG_THUMB_S).replace(" ", "%20");
                    String views = objJson.getString(Constant.TAG_VIEWS);
                    String downloads = objJson.getString(Constant.TAG_DOWNLOADS);

                    ItemSong objItem = new ItemSong(id, cid, cname, artist, url, image, image_small, name, duration, desc, total_rate, avg_rate, views, downloads);
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
                    if (Constant.isAppFirst) {
                        if (arrayList.size() > 0 && Constant.arrayList_play.size() == 0) {
                            Constant.isAppFirst = false;
                            Constant.arrayList_play.addAll(arrayList);
                            ((MainActivity) getActivity()).changeText(arrayList.get(0).getMp3Name(), arrayList.get(0).getCategoryName(), 1, arrayList.size(), arrayList.get(0).getDuration(), arrayList.get(0).getImageBig(), arrayList.get(0).getAverageRating(), "home");
                            Constant.context = getActivity();
                        }
                    }
                    viewpager.setAdapter(adapter);
                } else {
                    progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                }

                new LoadArtist().execute(Constant.URL_LATEST_ARTIST);
                super.onPostExecute(s);
            }
        }
    }

    private class LoadArtist extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            arrayList_artist.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);
                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_ID);
                    String name = objJson.getString(Constant.TAG_ARTIST_NAME);
                    String image = objJson.getString(Constant.TAG_ARTIST_IMAGE);
                    String thumb = objJson.getString(Constant.TAG_ARTIST_THUMB);

                    ItemArtist objItem = new ItemArtist(id, name, image, thumb);
                    arrayList_artist.add(objItem);
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
                loadRecent();
                if (s.equals("1")) {
                    adapterArtistLatest = new AdapterArtistLatest(getActivity(), arrayList_artist);
                    recyclerView_artist.setAdapter(adapterArtistLatest);
                    progressHUD.dismissWithSuccess(getResources().getString(R.string.success));
                } else {
                    progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(s);
            }
        }
    }

    private void loadRecent() {
        arrayList_recent = dbHelper.loadDataRecent();
        adapterRecent = new AdapterRecent(getActivity(), arrayList_recent);
        recyclerView.setAdapter(adapterRecent);

        if (arrayList_recent.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            textView_empty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }

        if (arrayList_artist.size() == 0) {
            recyclerView_artist.setVisibility(View.GONE);
            textView_empty_artist.setVisibility(View.VISIBLE);
        } else {
            recyclerView_artist.setVisibility(View.VISIBLE);
            textView_empty_artist.setVisibility(View.GONE);
        }
    }

    public void setEmpty() {
        if(arrayList.size() > 0) {
            ll_home.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            ll_home.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_home, container, false);
            assert imageLayout != null;
            RoundedImageView imageView = imageLayout.findViewById(R.id.imageView_pager_home);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading_home);
            TextView title = imageLayout.findViewById(R.id.textView_pager_home_title);
            TextView cat = imageLayout.findViewById(R.id.textView_pager_home_cat);
            RelativeLayout rl = imageLayout.findViewById(R.id.rl_homepager);

            title.setText(arrayList.get(position).getMp3Name());
            cat.setText(arrayList.get(position).getCategoryName());

            Picasso.get()
                    .load(arrayList.get(position).getImageBig())
                    .placeholder(R.mipmap.app_icon)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (JsonUtils.isNetworkAvailable(getActivity())) {
                        showInter();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void showInter() {
        Constant.adCount = Constant.adCount + 1;
        if (Constant.adCount % Constant.adDisplay == 0) {
            ((MainActivity) getActivity()).mInterstitial.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    playIntent();
                    super.onAdClosed();
                }
            });
            if (((MainActivity) getActivity()).mInterstitial.isLoaded()) {
                ((MainActivity) getActivity()).mInterstitial.show();
            } else {
                playIntent();
            }
            ((MainActivity) getActivity()).loadInter();
        } else {
            playIntent();
        }
    }

    private void playIntent() {
        Constant.isOnline = true;
        int pos = viewpager.getCurrentItem();
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(arrayList);
        Constant.playPos = pos;
        ((MainActivity) getActivity()).changeText(arrayList.get(pos).getMp3Name(), arrayList.get(pos).getCategoryName(), pos + 1, arrayList.size(), arrayList.get(pos).getDuration(), arrayList.get(pos).getImageBig(), arrayList.get(pos).getAverageRating(), "home");

        Constant.context = getActivity();
//        if (pos == 0) {
            Intent intent = new Intent(getActivity(), PlayerService.class);
            intent.setAction(PlayerService.ACTION_FIRST_PLAY);
            getActivity().startService(intent);
//        }
    }
}