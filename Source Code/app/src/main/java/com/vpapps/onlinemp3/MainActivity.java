package com.vpapps.onlinemp3;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vpapps.AsyncTask.GetRating;
import com.vpapps.AsyncTask.LoadAbout;
import com.vpapps.AsyncTask.LoadRating;
import com.vpapps.adapter.AdapterNavigation;
import com.vpapps.interfaces.AboutListener;
import com.vpapps.interfaces.RatingListener;
import com.vpapps.interfaces.RecyclerClickListener;
import com.vpapps.item.ItemAbout;
import com.vpapps.item.ItemSong;
import com.vpapps.utils.AdConsent;
import com.vpapps.interfaces.AdConsentListener;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.JsonUtils;
import com.vpapps.utils.PausableRotateAnimation;
import com.vpapps.utils.SharedPref;
import com.vpapps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FragmentManager fm;
    private DBHelper dbHelper;
    AdapterNavigation adapterNavigation;
    public RelativeLayout slidepanelchildtwo_topviewone, rl_dragview, rl_loading, rl_topviewone;
    public LinearLayout slidepanelchildtwo_topviewtwo, ll_topplayer, ll_adView, ll_download, ll_rate, ll_playlist;

    private Handler seekHandler = new Handler();

    public TextView txt_songname, txt_artistname, txt_song_no, txt_totaltime, txt_duration, txt_artist_small, txt_song_small,
            txt_playesongname_slidetoptwo, txt_songartistname_slidetoptwo;

    public ImageView img_bottom_slideone, img_bottom_slidetwo, btn_playpausePanel, imageView_backward, imageView_download, imageView_rate, imageView_volume, imageView_forward, imageView_previous_bottom, imageView_forward_bottom, imageView_shuffle, imageView_repeat, imageView_playpause,
            imageView_Favorite, imageView_heart, imageView_share, imageView_addtoplay, imageView_song_desc;

    private RoundedImageView imageView_pager;

    public RatingBar ratingBar;

    public View view_round, view_download, view_rate, view_playlist;

    public AppCompatSeekBar seekBar;
    public ViewPager viewpager;
    ImagePagerAdapter adapter;
    SlidingUpPanelLayout mLayout;
    AudioManager am;
    InterstitialAd mInterstitial;
    Handler mExitHandler = new Handler();
    Boolean mRecentlyBackPressed = false, isDownloadAvailable = false, isRotateAnim = false, isExpand = false;
    JsonUtils utils;
    BottomSheetDialog dialog_desc;
    Dialog dialog_rate;
    LoadRating loadRating;
    RecyclerView recyclerView;
    SearchView searchView;
    RecyclerClickListener recyclerClickListener;
    PausableRotateAnimation rotateAnimation;
    Animation animation_scaledown;
    AdConsent adConsent;
    SharedPref sharedPref;
    LoadAbout loadAbout;
    String deviceId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Constant.isAppOpen = true;
        Constant.context = MainActivity.this;

        sharedPref = new SharedPref(this);
        utils = new JsonUtils(this);
        utils.forceRTLIfSupported(getWindow());
        utils.setStatusColor(getWindow());

        animation_scaledown = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_up);

        ll_adView = findViewById(R.id.adView);

        if (Constant.itemAbout == null) {
            Constant.itemAbout = new ItemAbout("", "", "", "", "", "", "", "", "", "");
        }

        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate(ConsentStatus consentStatus) {
                if(!Constant.isBannerAdCalled && JsonUtils.isNetworkAvailable(MainActivity.this)) {
                    Constant.isBannerAdCalled = true;
                    utils.showBannerAd(ll_adView);
                }
                loadInter();
            }
        });

        recyclerClickListener = new RecyclerClickListener() {
            @Override
            public void onClick(int position) {
                onNavigationClick(position);
            }
        };

        recyclerView = findViewById(R.id.recyclerView_main);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        String[] menu_names = {getString(R.string.home), getString(R.string.categories), getString(R.string.artist),getString(R.string.albums),
                getString(R.string.playlist),getString(R.string.my_playlist), getString(R.string.downloads), getString(R.string.favourite), getString(R.string.share_app),
                getString(R.string.rate_app), getString(R.string.action_settings)};

        Integer[] menu_images = {R.mipmap.home, R.mipmap.cat, R.mipmap.artist, R.mipmap.albums, R.mipmap.playlist, R.mipmap.playlist,
                R.mipmap.nav_download, R.mipmap.heart,R.mipmap.share_nav, R.mipmap.rate, R.mipmap.setting};
        adapterNavigation = new AdapterNavigation(MainActivity.this, menu_names, menu_images, recyclerClickListener);
        recyclerView.setAdapter(adapterNavigation);

        mLayout = findViewById(R.id.sliding_layout);
        txt_duration = findViewById(R.id.slidepanel_time_progress);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fm = getSupportFragmentManager();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initiSlidingUpPanel();

        newRotateAnim();
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Constant.isScrolled = true;
            }

            @Override
            public void onPageSelected(int position) {
//                Constant.playPos = position;
//                isRotateAnim = false;
//                if (!Constant.isOnline || JsonUtils.isNetworkAvailable(MainActivity.this)) {
//                    Intent intent = new Intent(MainActivity.this, PlayerService.class);
//                    intent.setAction(PlayerService.ACTION_FIRST_PLAY);
//                    startService(intent);
//                } else {
//                    Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
//                }
                ItemSong itemSong = Constant.arrayList_play.get(position);
                changeTextPager(itemSong.getMp3Name(), itemSong.getCategoryName(), position + 1, Constant.arrayList_play.size(), itemSong.getDuration(), itemSong.getImageBig(), itemSong.getAverageRating(), "home");

                View view = viewpager.findViewWithTag("myview"+position);
                if(view != null) {
                    ImageView iv = view.findViewById(R.id.imageView_vp_play);
                    if (Constant.playPos == position) {
                        iv.setVisibility(View.GONE);
                    } else {
                        iv.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        dbHelper = new DBHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Constant.isFromNoti) {
            loadHomeFrag();
            changePlayPauseIcon(Constant.isPlaying);
            changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(), Constant.arrayList_play.get(Constant.playPos).getCategoryName(), Constant.playPos + 1, Constant.arrayList_play.size(), Constant.arrayList_play.get(Constant.playPos).getDuration(), Constant.arrayList_play.get(Constant.playPos).getImageBig(), Constant.arrayList_play.get(Constant.playPos).getAverageRating(), "cat");
            seekUpdation();
        } else if (Constant.isFromPush) {
            if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
                if (!Constant.pushSID.equals("0")) {
                    new LoadSong().execute(Constant.URL_SONG_1 + Constant.pushSID + Constant.URL_SONG_2 + deviceId);
                } else if (!Constant.pushCID.equals("0")) {
                    loadCatFrag();
                } else if (!Constant.pushAID.equals("0")) {
                    loadArtistFrag();
                }
            } else {
                loadHomeFrag();
            }
        } else if (Constant.isPlaying) {
            loadHomeFrag();
            changePlayPauseIcon(Constant.isPlaying);
            changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(), Constant.arrayList_play.get(Constant.playPos).getCategoryName(), Constant.playPos + 1, Constant.arrayList_play.size(), Constant.arrayList_play.get(Constant.playPos).getDuration(), Constant.arrayList_play.get(Constant.playPos).getImageBig(), Constant.arrayList_play.get(Constant.playPos).getAverageRating(), "cat");
            seekUpdation();
        } else if (Constant.arrayList_play.size() != 0) {
            loadHomeFrag();
            ItemSong itemSong = Constant.arrayList_play.get(Constant.playPos);
            changeText(itemSong.getMp3Name(), itemSong.getArtist(), Constant.playPos, Constant.arrayList_play.size(), itemSong.getDuration(), itemSong.getImageBig(), itemSong.getAverageRating(), "main");
//            viewpager.setAdapter(adapter);
        } else {
            loadHomeFrag();
        }

        dbHelper.getAbout();
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onEnd(Boolean success) {
                    adConsent.checkForConsent();
                    dbHelper.addtoAbout();
                }
            });
            loadAbout.execute(Constant.URL_APP_DETAILS);
        } else {
            adConsent.checkForConsent();
            loadInter();
        }

        checkPer();
    }

    private void loadHomeFrag() {
        FragmentHome f1 = new FragmentHome();
        loadFrag(f1, getResources().getString(R.string.home), fm);
        getSupportActionBar().setTitle(getResources().getString(R.string.home));
    }

    private void loadCatFrag() {
        FragmentSongByAlbums f1 = new FragmentSongByAlbums();
        FragmentTransaction ft = fm.beginTransaction();

        Bundle bundl = new Bundle();
        bundl.putString("type", getString(R.string.category));
        bundl.putString("id", Constant.pushCID);
        bundl.putString("name", Constant.pushCName);
        f1.setArguments(bundl);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.fragment, f1, Constant.pushCName);
//        ft.addToBackStack(Constant.pushCName);
        ft.commit();

        getSupportActionBar().setTitle(Constant.pushCName);
        adapterNavigation.setSelectedTitle(1);
        adapterNavigation.notifyDataSetChanged();
    }

    private void loadArtistFrag() {
        FragmentSongByAlbums f1 = new FragmentSongByAlbums();
        FragmentTransaction ft = fm.beginTransaction();

        Bundle bundl = new Bundle();
        bundl.putString("type", getString(R.string.artist));
        bundl.putString("id", Constant.pushAID);
        bundl.putString("name", Constant.pushANAME);
        f1.setArguments(bundl);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.fragment, f1, Constant.pushANAME);
        ft.commit();

        getSupportActionBar().setTitle(Constant.pushANAME);
        adapterNavigation.setSelectedTitle(2);
        adapterNavigation.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_item = s.replace(" ", "%20");
            FragmentSongBySearch fsearch = new FragmentSongBySearch();
            loadFrag(fsearch, getString(R.string.search), fm);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mRecentlyBackPressed = false;
        }
    };

    private void onNavigationClick(int pos) {
        switch (pos) {
            case 0:
                FragmentHome fh = new FragmentHome();
                loadFrag(fh, getResources().getString(R.string.home), fm);
                break;
            case 1:
                FragmentCat fcat = new FragmentCat();
                loadFrag(fcat, getResources().getString(R.string.categories), fm);
                break;
            case 2:
                FragmentArtist fart = new FragmentArtist();
                loadFrag(fart, getResources().getString(R.string.artist), fm);
                break;
            case 3:
                FragmentAlbums falbums = new FragmentAlbums();
                loadFrag(falbums, getResources().getString(R.string.albums), fm);
                break;
            case 4:
                FragmentServerPlaylist fserverplay = new FragmentServerPlaylist();
                loadFrag(fserverplay, getResources().getString(R.string.playlist), fm);
                break;
            case 5:
                FragmentPlaylist fplay = new FragmentPlaylist();
                loadFrag(fplay, getResources().getString(R.string.my_playlist), fm);
                break;
            case 6:
                if (isDownloadAvailable) {
                    FragmentDownloads fdownload = new FragmentDownloads();
                    loadFrag(fdownload, getResources().getString(R.string.downloads), fm);
                } else {
                    checkPer();
                }
                break;
            case 7:
                FragmentFav ffav = new FragmentFav();
                loadFrag(ffav, getResources().getString(R.string.favourite), fm);
                break;
            case 8:
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(ishare);
                break;
            case 9:
                final String appName = getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
                break;
            case 10:
                Intent intent_setting = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent_setting);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        if (!name.equals(getString(R.string.search))) {
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (name.equals(getString(R.string.search))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.fragment, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.fragment, f1, name);
        }
        ft.commit();

        getSupportActionBar().setTitle(name);

        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    protected void onDestroy() {
        if (!Constant.isPlaying && Constant.isPlayed) {
            Intent intent = new Intent(getApplicationContext(), PlayerService.class);
            intent.setAction(PlayerService.ACTION_STOP);
            startService(intent);
        }
        Constant.isAppOpen = false;
        super.onDestroy();
    }



    private void initiSlidingUpPanel() {
        ll_topplayer = findViewById(R.id.ll_topplayer);
        rl_loading = findViewById(R.id.rl_loading);
        rl_topviewone = findViewById(R.id.rl_topviewone);
        rl_dragview = findViewById(R.id.include_sliding_panel_childtwo);
        view_round = findViewById(R.id.vBgLike);
        img_bottom_slideone = findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = findViewById(R.id.img_bottom_slidetwo);

        ratingBar = findViewById(R.id.ratingBar_song);

        txt_totaltime = findViewById(R.id.slidepanel_time_total);
        txt_songname = findViewById(R.id.textView_songname_full);
        txt_artistname = findViewById(R.id.textView_artistname_full);
        txt_song_no = findViewById(R.id.textView_song_count);

        imageView_backward = findViewById(R.id.btn_backward);
        imageView_forward = findViewById(R.id.btn_forward);
        imageView_forward_bottom = findViewById(R.id.bottombar_next);
        imageView_previous_bottom = findViewById(R.id.bottombar_previous);
        imageView_forward = findViewById(R.id.btn_forward);
        imageView_repeat = findViewById(R.id.btn_repeat);
        imageView_shuffle = findViewById(R.id.btn_shuffle);
        imageView_playpause = findViewById(R.id.btn_play);
        imageView_download = findViewById(R.id.imageView_download);
        imageView_rate = findViewById(R.id.imageView_rate);
        imageView_volume = findViewById(R.id.imageView_volume);
        imageView_heart = findViewById(R.id.ivLike);

        ll_rate = findViewById(R.id.ll_rate);
        ll_download = findViewById(R.id.ll_download);
        ll_playlist = findViewById(R.id.ll_playlist);
        view_download = findViewById(R.id.view_download);
        view_rate = findViewById(R.id.view_rate);
        view_playlist = findViewById(R.id.view_playlist);

        adapter = new ImagePagerAdapter();
        viewpager = findViewById(R.id.viewPager_song);

        seekBar = findViewById(R.id.audio_progress_control);

        btn_playpausePanel = findViewById(R.id.bottombar_play);
        imageView_Favorite = findViewById(R.id.bottombar_img_Favorite);
        imageView_share = findViewById(R.id.bottombar_shareicon);
        imageView_addtoplay = findViewById(R.id.bottombar_addtoplay);
        imageView_song_desc = findViewById(R.id.bottombar_img_desc);
        imageView_song_desc.setColorFilter(getResources().getColor(R.color.white));

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        seekBar.setProgress(0);

//        audio_progress.setOnValueChangedListener(this);

        imageView_previous_bottom.setOnClickListener(this);
        imageView_forward_bottom.setOnClickListener(this);
        imageView_backward.setOnClickListener(this);
        imageView_forward.setOnClickListener(this);
        imageView_repeat.setOnClickListener(this);
        imageView_shuffle.setOnClickListener(this);
        imageView_Favorite.setOnClickListener(this);
        imageView_share.setOnClickListener(this);
        imageView_playpause.setOnClickListener(this);
        btn_playpausePanel.setOnClickListener(this);
        imageView_download.setOnClickListener(this);
        imageView_rate.setOnClickListener(this);
        imageView_volume.setOnClickListener(this);
        imageView_song_desc.setOnClickListener(this);
        imageView_addtoplay.setOnClickListener(this);
        ll_topplayer.setOnClickListener(this);

        txt_artist_small = findViewById(R.id.txt_artist_small);
        txt_song_small = findViewById(R.id.txt_songname_small);
        txt_song_small.setSelected(true);

        txt_playesongname_slidetoptwo = findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_playesongname_slidetoptwo.setSelected(true);
        txt_songartistname_slidetoptwo = findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        findViewById(R.id.bottombar_play).setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    Intent intent = new Intent(MainActivity.this, PlayerService.class);
                    intent.setAction(PlayerService.ACTION_SEEKTO);
                    intent.putExtra("seekto", JsonUtils.getSeekFromPercentage(progress, JsonUtils.calculateTime(Constant.arrayList_play.get(Constant.playPos).getDuration())));
                    startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;
                    img_bottom_slideone.startAnimation(animation_scaledown);
                    btn_playpausePanel.startAnimation(animation_scaledown);
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    if (isExpand) {
                        btn_playpausePanel.setScaleX(1.0f);
                        btn_playpausePanel.setScaleY(1.0f);
                        img_bottom_slideone.setScaleX(1.0f);
                        img_bottom_slideone.setScaleY(1.0f);
                        img_bottom_slidetwo.setScaleX(0.0f + slideOffset);
                        img_bottom_slidetwo.setScaleY(0.0f + slideOffset);
                        imageView_Favorite.setScaleX(0.0f + slideOffset);
                        imageView_Favorite.setScaleY(0.0f + slideOffset);
                        imageView_song_desc.setScaleX(0.0f + slideOffset);
                        imageView_song_desc.setScaleY(0.0f + slideOffset);
                    } else {
                        btn_playpausePanel.setScaleX(1.0f - slideOffset);
                        btn_playpausePanel.setScaleY(1.0f - slideOffset);
                        img_bottom_slideone.setScaleX(1.0f - slideOffset);
                        img_bottom_slideone.setScaleY(1.0f - slideOffset);
                        img_bottom_slidetwo.setScaleX(1.0f);
                        img_bottom_slidetwo.setScaleY(1.0f);
                        imageView_Favorite.setScaleX(1.0f);
                        imageView_Favorite.setScaleY(1.0f);
                        imageView_song_desc.setScaleX(1.0f);
                        imageView_song_desc.setScaleY(1.0f);
                    }
                } else {
                    isExpand = true;
                    img_bottom_slidetwo.startAnimation(animation_scaledown);
                    imageView_song_desc.startAnimation(animation_scaledown);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    if (imageView_Favorite.getVisibility() == View.VISIBLE) {
                        imageView_Favorite.startAnimation(animation_scaledown);
                    }
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    viewpager.setCurrentItem(Constant.playPos);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottombar_img_Favorite:
                if (Constant.arrayList_play.size() > 0) {
                    if (Constant.isOnline) {
                        JsonUtils.animateHeartButton(view);
                        JsonUtils.animatePhotoLike(view_round, imageView_heart);
                        view.setSelected(!view.isSelected());
                        findViewById(R.id.ivLike).setSelected(view.isSelected());
                        fav();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bottombar_play:
                playPause();
                break;
            case R.id.btn_play:
                playPause();
                break;
            case R.id.btn_shuffle:
                setShuffle();
                break;
            case R.id.btn_repeat:
                setRepeat();
                break;
            case R.id.btn_forward:
                next();
                break;
            case R.id.bottombar_next:
                next();
                break;
            case R.id.btn_backward:
                previous();
                break;
            case R.id.bottombar_previous:
                previous();
                break;
            case R.id.bottombar_shareicon:
                shareSong();
                break;
            case R.id.bottombar_img_desc:
                if(Constant.arrayList_play.size() > 0) {
                    showBottomSheetDialog();
                }
                break;
            case R.id.bottombar_addtoplay:
                if (Constant.arrayList_play.size() > 0) {
                    utils.openPlaylists(Constant.arrayList_play.get(viewpager.getCurrentItem()));
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.imageView_download:
                if (isDownloadAvailable) {
                    download();
                } else {
                    checkPer();
                }
                break;
            case R.id.imageView_rate:
                if(Constant.arrayList_play.size() > 0) {
                    openRateDialog();
                }
                break;
            case R.id.imageView_volume:
                changeVolume();
                break;
            case R.id.ll_topplayer:
                if (mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                } else {
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                }
                break;
        }
    }

    public void changeTextPager(String sname, String aname, int pos, int total, String totaltime, final String image, String rating, String page) {
        ratingBar.setRating(Integer.parseInt(rating));

        txt_artistname.setText(aname);
        txt_songname.setText(sname);
        txt_song_no.setText(pos + "/" + total);
    }

    public void changeText(String sname, String aname, int pos, int total, String totaltime, final String image, String rating, String page) {
        ratingBar.setRating(Integer.parseInt(rating));
        txt_artist_small.setText(aname);
        txt_song_small.setText(sname);

        txt_duration.setText("00:00");
        txt_totaltime.setText(totaltime);

        txt_artistname.setText(aname);
        txt_songname.setText(sname);
        txt_song_no.setText(pos + "/" + total);

        txt_playesongname_slidetoptwo.setText(sname);
        txt_songartistname_slidetoptwo.setText(aname);

        Picasso.get()
                .load(image)
                .into(img_bottom_slideone);

        checkFav();

        if (imageView_download.getVisibility() == View.GONE) {
            imageView_download.setVisibility(View.VISIBLE);
            imageView_rate.setVisibility(View.VISIBLE);
            imageView_Favorite.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            imageView_addtoplay.setVisibility(View.VISIBLE);
            ll_download.setVisibility(View.VISIBLE);
            ll_rate.setVisibility(View.VISIBLE);
            ll_playlist.setVisibility(View.VISIBLE);
            view_rate.setVisibility(View.VISIBLE);
            view_download.setVisibility(View.VISIBLE);
            view_playlist.setVisibility(View.VISIBLE);
        }

        if(Constant.isSongDownload) {
            ll_download.setVisibility(View.VISIBLE);
            view_download.setVisibility(View.VISIBLE);
        } else {
            ll_download.setVisibility(View.GONE);
            view_download.setVisibility(View.GONE);
        }

        if (!page.equals(Constant.loadedSongPage) || !page.equals("")) {
            viewpager.setAdapter(adapter);
            viewpager.setOffscreenPageLimit(Constant.arrayList_play.size());
        }
        viewpager.setCurrentItem(Constant.playPos);
    }

    public void changeText(String sname, String aname, int pos, int total, String totaltime, Bitmap image, String page) {
        txt_artist_small.setText(aname);
        txt_song_small.setText(sname);

        txt_duration.setText("00:00");
        txt_totaltime.setText(totaltime);

        txt_artistname.setText(aname);
        txt_songname.setText(sname);
        txt_song_no.setText(pos + "/" + total);

        txt_playesongname_slidetoptwo.setText(sname);
        txt_songartistname_slidetoptwo.setText(aname);

        img_bottom_slideone.setImageBitmap(image);

        checkFav();

        if (imageView_download.getVisibility() == View.VISIBLE) {
            imageView_rate.setVisibility(View.GONE);
            imageView_download.setVisibility(View.GONE);
            imageView_Favorite.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            imageView_addtoplay.setVisibility(View.GONE);
            ll_download.setVisibility(View.GONE);
            ll_rate.setVisibility(View.GONE);
            ll_playlist.setVisibility(View.GONE);
            view_rate.setVisibility(View.GONE);
            view_download.setVisibility(View.GONE);
            view_playlist.setVisibility(View.GONE);
        }

        if (!page.equals(Constant.loadedSongPage) || !page.equals("")) {
            viewpager.setAdapter(adapter);
            viewpager.setOffscreenPageLimit(Constant.arrayList_play.size());
        }

        viewpager.setCurrentItem(Constant.playPos);
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        seekBar.setProgress(JsonUtils.getProgressPercentage(Constant.exoPlayer.getCurrentPosition(), JsonUtils.calculateTime(Constant.arrayList_play.get(Constant.playPos).getDuration())));
        txt_duration.setText(JsonUtils.milliSecondsToTimer(Constant.exoPlayer.getCurrentPosition()));
        Log.e("duration", "" + JsonUtils.milliSecondsToTimer(Constant.exoPlayer.getCurrentPosition()));
        seekBar.setSecondaryProgress(Constant.exoPlayer.getBufferedPercentage());
        if (Constant.isPlaying && Constant.isAppOpen) {
            seekHandler.postDelayed(run, 1000);
        }
    }

    public void isBuffering(Boolean isBuffer) {
        Constant.isPlaying = !isBuffer;
        if (isBuffer) {
            rl_loading.setVisibility(View.VISIBLE);
            imageView_playpause.setVisibility(View.INVISIBLE);
        } else {
            rl_loading.setVisibility(View.INVISIBLE);
            changePlayPauseIcon(!isBuffer);
            imageView_playpause.setVisibility(View.VISIBLE);
        }
        imageView_forward_bottom.setEnabled(!isBuffer);
        imageView_previous_bottom.setEnabled(!isBuffer);
        imageView_backward.setEnabled(!isBuffer);
        imageView_forward.setEnabled(!isBuffer);
        imageView_download.setEnabled(!isBuffer);
        btn_playpausePanel.setEnabled(!isBuffer);
        seekBar.setEnabled(!isBuffer);
    }

    public void setRepeat() {
        if (Constant.isRepeat) {
            Constant.isRepeat = false;
            imageView_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.repeat));
        } else {
            Constant.isRepeat = true;
            imageView_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.repeat_hover));
        }
    }

    public void setShuffle() {
        if (Constant.isSuffle) {
            Constant.isSuffle = false;
            imageView_shuffle.setImageDrawable(getResources().getDrawable(R.mipmap.shuffle));
        } else {
            Constant.isSuffle = true;
            imageView_shuffle.setImageDrawable(getResources().getDrawable(R.mipmap.shuffle_hover));
        }
    }

    public void next() {
        if (Constant.arrayList_play.size() > 0) {
            if (!Constant.isOnline || JsonUtils.isNetworkAvailable(MainActivity.this)) {
                isRotateAnim = false;
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_SKIP);
                startService(intent);
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void previous() {
        if (Constant.arrayList_play.size() > 0) {
            if (!Constant.isOnline || JsonUtils.isNetworkAvailable(MainActivity.this)) {
                isRotateAnim = false;
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_REWIND);
                startService(intent);
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void playPause() {
        if (Constant.arrayList_play.size() > 0) {
            Intent intent = new Intent(MainActivity.this, PlayerService.class);
            if (Constant.isPlayed) {
                if (Constant.isPlaying) {
                    intent.setAction(PlayerService.ACTION_PAUSE);
                    startService(intent);
                } else {
                    if (!Constant.isOnline || JsonUtils.isNetworkAvailable(MainActivity.this)) {
                        intent.setAction(PlayerService.ACTION_PLAY);
                        startService(intent);
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (!Constant.isOnline || JsonUtils.isNetworkAvailable(MainActivity.this)) {
                    intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                    startService(intent);
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void newRotateAnim() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();
        }
        rotateAnimation = new PausableRotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(Constant.rotateSpeed);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
    }

    public void changeImageAnimation(Boolean isPlay) {
        if (!isPlay) {
            rotateAnimation.pause();
        } else {
            if (!isRotateAnim) {
                isRotateAnim = true;
                if (imageView_pager != null) {
                    imageView_pager.setAnimation(null);
                }
                View view_pager = viewpager.findViewWithTag("myview" + Constant.playPos);
                newRotateAnim();
                imageView_pager = view_pager.findViewById(R.id.image);
                imageView_pager.startAnimation(rotateAnimation);
            } else {
                rotateAnimation.resume();
            }
        }
    }

    public void changePlayPauseIcon(Boolean isPlay) {
        if (!isPlay) {
            imageView_playpause.setImageDrawable(getResources().getDrawable(R.drawable.selector_play));
            btn_playpausePanel.setImageDrawable(getResources().getDrawable(R.drawable.play_pink));
        } else {
            imageView_playpause.setImageDrawable(getResources().getDrawable(R.drawable.selector_pause));
            btn_playpausePanel.setImageDrawable(getResources().getDrawable(R.drawable.pause_pink));
        }
    }

    public void fav() {
        if (Constant.isFav) {
            dbHelper.removeFromFav(Constant.arrayList_play.get(Constant.playPos).getId());
            Toast.makeText(MainActivity.this, getResources().getString(R.string.removed_fav), Toast.LENGTH_SHORT).show();
            Constant.isFav = false;
            changeFav();
        } else {
            dbHelper.addToFav(Constant.arrayList_play.get(Constant.playPos));
            Toast.makeText(MainActivity.this, getResources().getString(R.string.added_fav), Toast.LENGTH_SHORT).show();
            Constant.isFav = true;
            changeFav();
        }
    }

    public void checkFav() {
        Constant.isFav = dbHelper.checkFav(Constant.arrayList_play.get(Constant.playPos).getId());

        changeFav();
    }

    public void changeFav() {
        if (Constant.isFav) {
            imageView_Favorite.setImageDrawable(getResources().getDrawable(R.drawable.fav_hover));
        } else {
            imageView_Favorite.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayList_play.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_item, container, false);
            assert imageLayout != null;
            RoundedImageView imageView = imageLayout.findViewById(R.id.image);
            final ImageView imageView_play = imageLayout.findViewById(R.id.imageView_vp_play);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading);

            if(Constant.playPos == position) {
                imageView_play.setVisibility(View.GONE);
            }

            if (Constant.arrayList_play.get(position).getImageBig() != null) {
                Picasso.get()
                        .load(Constant.arrayList_play.get(position).getImageBig())
                        .placeholder(R.drawable.cd)
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
            } else {
                imageView.setImageBitmap(Constant.arrayList_play.get(position).getBitmap());
                spinner.setVisibility(View.GONE);
            }

            imageView_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.playPos = viewpager.getCurrentItem();
                    isRotateAnim = false;
                    if (!Constant.isOnline || JsonUtils.isNetworkAvailable(MainActivity.this)) {
                        Intent intent = new Intent(MainActivity.this, PlayerService.class);
                        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                        startService(intent);
                        imageView_play.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (position == 0) {
                isRotateAnim = false;
                imageView_pager = imageView;
            }

            imageLayout.setTag("myview" + position);
            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void shareSong() {
        if (Constant.arrayList_play.size() > 0) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_song));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.listening) + " - " + Constant.arrayList_play.get(viewpager.getCurrentItem()).getMp3Name() + "\n\nvia " + getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_song)));
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();
        }
    }

    private void download() {
        if (Constant.arrayList_play.size() > 0) {

            File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name));
            if (!root.exists()) {
                root.mkdirs();
            }

            File file = new File(root, Constant.arrayList_play.get(viewpager.getCurrentItem()).getMp3Name() + ".mp3");

            if (!file.exists()) {

                String url = Constant.arrayList_play.get(viewpager.getCurrentItem()).getMp3Url();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription(getResources().getString(R.string.downloading) + " - " + Constant.arrayList_play.get(viewpager.getCurrentItem()).getMp3Name());
                request.setTitle(Constant.arrayList_play.get(viewpager.getCurrentItem()).getMp3Name());
                // in order for this if to run, you must use the android 3.2 to compile your app

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//                request.setDestinationInExternalPublicDir(getString(R.string.download_desti), Constant.arrayList_play.get(Constant.playPos).getMp3Name() + ".mp3");
                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getString(R.string.app_name) + "/" + Constant.arrayList_play.get(viewpager.getCurrentItem()).getMp3Name() + ".mp3"));

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        String json = JsonUtils.getJSONString(Constant.URL_DOWNLOAD_COUNT + Constant.arrayList_play.get(viewpager.getCurrentItem()).getId());
                        Log.e("aaa - ", json);
                        return null;
                    }
                }.execute();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.already_download), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_song_selected), Toast.LENGTH_SHORT).show();
        }
    }

    private void changeVolume() {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_dailog_volume);
        dialog.setTitle(getResources().getString(R.string.volume));

        SeekBar seekBar = dialog.findViewById(R.id.seekBar_volume);
        seekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(volume_level);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Constant.volume = i;
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void loadInter() {
        mInterstitial = new InterstitialAd(MainActivity.this);
        if (Constant.isInterAd) {
            AdRequest adRequest;
            if (ConsentInformation.getInstance(MainActivity.this).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                adRequest = new AdRequest.Builder().build();
            } else {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            }
            mInterstitial.setAdUnitId(Constant.ad_inter_id);
            mInterstitial.loadAd(adRequest);
        }
    }

    private class LoadSong extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
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
                    String thumb_small = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    String views = objJson.getString(Constant.TAG_VIEWS);
                    String downloads = objJson.getString(Constant.TAG_DOWNLOADS);

                    ItemSong objItem = new ItemSong(id, cid, cname, artist, url, thumb, thumb_small, name, duration, desc, total_rate, avg_rate, views, downloads);
                    Constant.arrayList_play.add(objItem);
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
            loadHomeFrag();
            if (s.equals("1")) {
                ItemSong itemSong = Constant.arrayList_play.get(0);
                changeText(itemSong.getMp3Name(), itemSong.getArtist(), 0, 1, itemSong.getDuration(), itemSong.getImageBig(), itemSong.getAverageRating(), "home");
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                startService(intent);
            }
            super.onPostExecute(s);
        }
    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"}, 1);
            }
        } else {
            isDownloadAvailable = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }

                if (!canUseExternalStorage) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.cannot_use_save), Toast.LENGTH_SHORT).show();
                } else {
                    isDownloadAvailable = true;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (dialog_desc != null && dialog_desc.isShowing()) {
            dialog_desc.dismiss();
        } else if (mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (fm.getBackStackEntryCount() != 0) {
            getSupportActionBar().setTitle(fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag());
            super.onBackPressed();
        } else {
            if (mRecentlyBackPressed) {
                mExitHandler.removeCallbacks(mExitRunnable);
                mRecentlyBackPressed = false;
                moveTaskToBack(true);
            } else {
                mRecentlyBackPressed = true;
                Toast.makeText(this, getResources().getString(R.string.press_again_exit), Toast.LENGTH_SHORT).show();
                mExitHandler.postDelayed(mExitRunnable, 2000L);
            }
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        dialog_desc = new BottomSheetDialog(this);
        dialog_desc.setContentView(view);
        dialog_desc.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_desc.show();

        AppCompatButton button = dialog_desc.findViewById(R.id.button_detail_close);
        TextView textView = dialog_desc.findViewById(R.id.textView_detail_title);
        textView.setText(Constant.arrayList_play.get(Constant.playPos).getMp3Name());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_desc.dismiss();
            }
        });

        WebView webview_song_desc = dialog_desc.findViewById(R.id.webView_bottom);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";
        String text = "<html><head>"
                + "<style> body{color: #000 !important;text-align:left}"
                + "</style></head>"
                + "<body>"
                + Constant.arrayList_play.get(Constant.playPos).getDescription()
                + "</body></html>";

        webview_song_desc.loadData(text, mimeType, encoding);
    }

    private void openRateDialog() {
        dialog_rate = new Dialog(MainActivity.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_review);

        final TextView textView = dialog_rate.findViewById(R.id.textView_rate);
        final RatingBar ratingBar = dialog_rate.findViewById(R.id.rating_add);
        final Button button = dialog_rate.findViewById(R.id.button_submit_rating);
        final Button button_later = dialog_rate.findViewById(R.id.button_later_rating);

        ratingBar.setStepSize(Float.parseFloat("1"));

        if (Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating().equals("")) {
            new GetRating(new RatingListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String message, int rating) {
                    if(rating > 0) {
                        ratingBar.setRating(rating);
                        textView.setText(getString(R.string.thanks_for_rating));
                    } else {
                        ratingBar.setRating(1);
                    }
                    Constant.arrayList_play.get(viewpager.getCurrentItem()).setUserRating(String.valueOf(rating));

                }
            }).execute(Constant.URL_SONG_1 + Constant.arrayList_play.get(viewpager.getCurrentItem()).getId() + Constant.URL_SONG_2 + deviceId);
        } else {
            if(Integer.parseInt(Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating())!= 0 && !Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating().equals("")) {
                textView.setText(getString(R.string.thanks_for_rating));
                ratingBar.setRating(Integer.parseInt(Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating()));
            } else {
                ratingBar.setRating(1);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ratingBar.getRating() != 0) {
                    if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
                        loadRatingApi(String.valueOf((int)ratingBar.getRating()));
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.select_rating), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_rate.dismiss();
            }
        });

        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadRatingApi(final String rate) {
        final ZProgressHUD progressHUD;
        progressHUD = ZProgressHUD.getInstance(MainActivity.this);
        progressHUD.setMessage(getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        loadRating = new LoadRating(new RatingListener() {
            @Override
            public void onStart() {
                progressHUD.show();
            }

            @Override
            public void onEnd(String success, String message, int rating) {

                if (success.equals("true")) {
                    if (progressHUD.isShowing()) {
                        progressHUD.dismissWithSuccess(getResources().getString(R.string.success));
                    }
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (!message.contains("already")) {
                        Constant.arrayList_play.get(viewpager.getCurrentItem()).setAverageRating(String.valueOf(rating));
                        Constant.arrayList_play.get(viewpager.getCurrentItem()).setTotalRate(String.valueOf(Integer.parseInt(Constant.arrayList_play.get(viewpager.getCurrentItem()).getTotalRate() + 1)));
                        Constant.arrayList_play.get(viewpager.getCurrentItem()).setUserRating(String.valueOf(rate));
                        ratingBar.setRating(rating);
                        changeRating();
                    }
                } else {
                    progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                }
                dialog_rate.dismiss();
            }
        });
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        loadRating.execute(Constant.URL_RATING_1 + Constant.arrayList_play.get(viewpager.getCurrentItem()).getId() + Constant.URL_RATING_2 + deviceId + Constant.URL_RATING_3 + rate);
    }

    private void changeRating() {
        switch (Constant.frag) {
            case "alb":
                FragmentSongByAlbums.adapterSongList.notifyDataSetChanged();
                break;
            case "fav":
                FragmentFav.adapterSongList.notifyDataSetChanged();
                break;
            case "search":
                FragmentSongBySearch.adapterSongList.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onResume() {
        if(ll_adView != null && !Constant.isBannerAdCalled && JsonUtils.isNetworkAvailable(MainActivity.this)) {
            Constant.isBannerAdCalled = true;
            utils.showBannerAd(ll_adView);
        }
        super.onResume();
    }
}