package com.vpapps.onlinemp3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class PlayerService extends IntentService {
    public static final String ACTION_FIRST_PLAY = "com.apps.onlinemp3.action.ACTION_FIRST";
    public static final String ACTION_SEEKTO = "com.apps.onlinemp3.action.ACTION_SEEKTO";
    public static final String ACTION_PLAY = "com.apps.onlinemp3.action.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.apps.onlinemp3.action.PAUSE";
    public static final String ACTION_STOP = "com.apps.onlinemp3.action.STOP";
    public static final String ACTION_SKIP = "com.apps.onlinemp3.action.SKIP";
    public static final String ACTION_REWIND = "com.apps.onlinemp3.action.REWIND";
    public static final String ACTION_NOTI_PLAY = "com.apps.onlinemp3.action.NOTI_PLAY";

    TrackSelector trackSelector;
    NotificationCompat.Builder notification;
    RemoteViews bigViews, smallViews;
    DBHelper dbHelper;
    private String NOTIFICATION_CHANNEL_ID = "onlinemp3_ch_1";
    static NotificationManager mNotificationManager;

    public PlayerService() {
        super(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            dbHelper = new DBHelper(Constant.context);
            registerReceiver(onCallIncome, new IntentFilter("android.intent.action.PHONE_STATE"));

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            Constant.exoPlayer = ExoPlayerFactory.newSimpleInstance((MainActivity) Constant.context, trackSelector);
            Constant.exoPlayer.addListener(listener);

            if (Constant.arrayList_play.size() != 0) {
                createNoti();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("aaaa", "called");
        String action = intent.getAction();
        switch (action) {
            case ACTION_FIRST_PLAY:
                handleFirstPlay();
                break;
            case ACTION_SEEKTO:
                seekTo(intent.getExtras().getLong("seekto"));
                break;
            case ACTION_PLAY:
                play();
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_STOP:
                stop(intent);
                break;
            case ACTION_REWIND:
                if (!Constant.isOnline || JsonUtils.isNetworkAvailable(Constant.context)) {
                    previous();
                } else {
                    Toast.makeText(Constant.context, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_SKIP:
                if (!Constant.isOnline || JsonUtils.isNetworkAvailable(Constant.context)) {
                    next();
                } else {
                    Toast.makeText(Constant.context, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                }
                break;
            case ACTION_NOTI_PLAY:
                if (Constant.isPlaying) {
                    pause();
                } else {
                    if (!Constant.isOnline || JsonUtils.isNetworkAvailable(Constant.context)) {
                        play();
                    } else {
                        Toast.makeText(Constant.context, getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return START_STICKY;
    }

    private void handleFirstPlay() {
        Constant.isPlayed = true;
//        changePlayPause();
        changeText();
        playAudio();
        showNotification();
    }

    Player.EventListener listener = new Player.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                onCompletion();
            }
            if (playbackState == Player.STATE_READY && playWhenReady) {
//                Constant.exoPlayer.setPlayWhenReady(true);
                setBuffer(false);
                updateNoti();
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }
    };

    private void onCompletion() {
        if (Constant.isRepeat) {
            Constant.exoPlayer.seekTo(0);
        } else {
            if (Constant.isSuffle) {
                Random rand = new Random();
                Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
            } else {
                setNext();
            }
        }

        changeText();
        playAudio();
    }

    private void changeText() {
        if (Constant.isOnline) {
            ((MainActivity) Constant.context).changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(), Constant.arrayList_play.get(Constant.playPos).getCategoryName(), Constant.playPos + 1, Constant.arrayList_play.size(), Constant.arrayList_play.get(Constant.playPos).getDuration(), Constant.arrayList_play.get(Constant.playPos).getImageBig(), Constant.arrayList_play.get(Constant.playPos).getAverageRating(), "");
        } else {
            ((MainActivity) Constant.context).changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(), Constant.arrayList_play.get(Constant.playPos).getArtist(), Constant.playPos + 1, Constant.arrayList_play.size(), Constant.arrayList_play.get(Constant.playPos).getDuration(), Constant.arrayList_play.get(Constant.playPos).getBitmap(), "");
        }
    }

    private void setBuffer(Boolean isBuffer) {
        ((MainActivity) Constant.context).isBuffering(isBuffer);
        if (!isBuffer) {
            ((MainActivity) Constant.context).seekUpdation();
            changeEquilizer();
        }
        Constant.isPlaying = !isBuffer;
    }

    private void playAudio() {
        new LoadSong().execute();
    }

    class LoadSong extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            setBuffer(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... a) {


            notification.setLargeIcon(getBitmapFromURL(Constant.arrayList_play.get(Constant.playPos).getImageSmall()));
            String s = Constant.arrayList_play.get(Constant.playPos).getMp3Url().replace(" ", "%20");
            try {
                JsonUtils.getJSONString(Constant.URL_SONG_1 + Constant.arrayList_play.get(Constant.playPos).getId() + Constant.URL_SONG_2 + "");
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory((MainActivity) Constant.context,
                        Util.getUserAgent((MainActivity) Constant.context, "onlinemp3"), bandwidthMeter);
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource videoSource = new ExtractorMediaSource(Uri.parse(s),
                        dataSourceFactory, extractorsFactory, null, null);
                Constant.exoPlayer.prepare(videoSource);
                Constant.exoPlayer.setPlayWhenReady(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (Constant.isOnline) {
                dbHelper.addToRecent(Constant.arrayList_play.get(Constant.playPos));
            }
            super.onPostExecute(s);
        }
    }

    private void setNext() {
        if (Constant.playPos < (Constant.arrayList_play.size() - 1)) {
            Constant.playPos = Constant.playPos + 1;
        } else {
            Constant.playPos = 0;
        }
        changeEquilizer();
    }

    private void seekTo(long seek) {
        Constant.exoPlayer.seekTo((int) seek);
    }

    private void play() {
        if (Constant.isPlayed) {
            Constant.isPlaying = true;
            Constant.exoPlayer.setPlayWhenReady(true);
//            changePlayPause();
            ((MainActivity) Constant.context).seekUpdation();
        } else {
            changeText();
            handleFirstPlay();
        }
        changeEquilizer();
        updateNotiPlay(Constant.isPlaying);
    }

    private void previous() {
        setBuffer(true);
        if (Constant.isSuffle) {
            Random rand = new Random();
            Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
        } else {
            if (Constant.playPos > 0) {
                Constant.playPos = Constant.playPos - 1;
            } else {
                Constant.playPos = Constant.arrayList_play.size() - 1;
            }
        }
        changeEquilizer();
        handleFirstPlay();
    }

    private void next() {
        setBuffer(true);
        if (Constant.isSuffle) {
            Random rand = new Random();
            Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
        } else {
            if (Constant.playPos < (Constant.arrayList_play.size() - 1)) {
                Constant.playPos = Constant.playPos + 1;
            } else {
                Constant.playPos = 0;
            }
        }
        changeEquilizer();
        handleFirstPlay();
    }

    private void pause() {
        Constant.isPlaying = false;
        changeEquilizer();
        Constant.exoPlayer.setPlayWhenReady(false);
        changePlayPause();
        updateNotiPlay(Constant.isPlaying);
    }

    private void changePlayPause() {
        ((MainActivity) Constant.context).changePlayPauseIcon(Constant.isPlaying);
    }

    private void stop(Intent intent) {
        Constant.isPlaying = false;
        Constant.isPlayed = false;
        Constant.isAppFirst = false;
        changeEquilizer();
        ((MainActivity) Constant.context).changePlayPauseIcon(Constant.isPlaying);
        Constant.exoPlayer.stop();
        Constant.exoPlayer.release();
        unregisterReceiver(onCallIncome);
        stopService(intent);
        stopForeground(true);
    }

    private void showNotification() {
        startForeground(101, notification.build());
    }

    private void createNoti() {
        bigViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
        smallViews = new RemoteViews(getPackageName(), R.layout.layout_noti_small);

        Intent notificationIntent = new Intent(this, SplashActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("isnoti", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(ACTION_REWIND);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(ACTION_NOTI_PLAY);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(ACTION_SKIP);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, PlayerService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon))
                .setContentTitle(getString(R.string.app_name))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification)
                .setTicker(Constant.arrayList_play.get(Constant.playPos).getMp3Name())
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setOnlyAlertOnce(true);

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Online Song";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);

            MediaSessionCompat mMediaSession;
            mMediaSession = new MediaSessionCompat(Constant.context, "ONLINEMP3");
            mMediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            notification.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mMediaSession.getSessionToken())
                    .setShowCancelButton(true)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setCancelButtonIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    Constant.context, PlaybackStateCompat.ACTION_STOP)))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_previous, "Previous",
                            ppreviousIntent))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_pause, "Pause",
                            pplayIntent))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_next, "Next",
                            pnextIntent))
                    .addAction(new NotificationCompat.Action(
                            R.mipmap.ic_noti_close, "Close",
                            pcloseIntent));
        } else {
            bigViews.setOnClickPendingIntent(R.id.imageView_noti_play, pplayIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_next, pnextIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_prev, ppreviousIntent);

            bigViews.setOnClickPendingIntent(R.id.imageView_noti_close, pcloseIntent);
            smallViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

            bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);

            bigViews.setTextViewText(R.id.textView_noti_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());
            smallViews.setTextViewText(R.id.status_bar_track_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());

            bigViews.setTextViewText(R.id.textView_noti_artist, Constant.arrayList_play.get(Constant.playPos).getArtist());
            smallViews.setTextViewText(R.id.status_bar_artist_name, Constant.arrayList_play.get(Constant.playPos).getArtist());

            bigViews.setImageViewResource(R.id.imageView_noti, R.mipmap.app_icon);
            smallViews.setImageViewResource(R.id.status_bar_album_art, R.mipmap.app_icon);

            notification.setCustomContentView(smallViews)
                    .setCustomBigContentView(bigViews);
        }
    }

    private void updateNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setContentTitle(Constant.arrayList_play.get(Constant.playPos).getMp3Name());
            notification.setContentText(Constant.arrayList_play.get(Constant.playPos).getArtist());
        } else {
            bigViews.setTextViewText(R.id.textView_noti_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());
            bigViews.setTextViewText(R.id.textView_noti_artist, Constant.arrayList_play.get(Constant.playPos).getArtist());
            smallViews.setTextViewText(R.id.status_bar_artist_name, Constant.arrayList_play.get(Constant.playPos).getArtist());
            smallViews.setTextViewText(R.id.status_bar_track_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());
        }
        updateNotiPlay(Constant.isPlaying);
    }

    private void updateNotiPlay(Boolean isPlay) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.mActions.remove(1);
            Intent playIntent = new Intent(this, PlayerService.class);
            playIntent.setAction(ACTION_NOTI_PLAY);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, playIntent, 0);
            if (isPlay) {
                notification.mActions.add(1, new NotificationCompat.Action(
                        R.mipmap.ic_noti_pause, "Pause",
                        ppreviousIntent));

            } else {
                notification.mActions.add(1, new NotificationCompat.Action(
                        R.mipmap.ic_noti_play, "Play",
                        ppreviousIntent));
            }
        } else {
            if (isPlay) {
                bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_pause);
            } else {
                bigViews.setImageViewResource(R.id.imageView_noti_play, android.R.drawable.ic_media_play);
            }
        }
//        startForeground(101, notification.build());
        mNotificationManager.notify(101, notification.build());
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    BroadcastReceiver onCallIncome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String a = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (Constant.isPlaying) {
                if (a.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) || a.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Constant.exoPlayer.setPlayWhenReady(false);
                } else if (a.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Constant.exoPlayer.setPlayWhenReady(true);
                }
            }
        }
    };

    private void changeEquilizer() {
        switch (Constant.frag) {
            case "alb":
                FragmentSongByAlbums.adapterSongList.notifyDataSetChanged();
                break;
            case "fav":
                FragmentFav.adapterSongList.notifyDataSetChanged();
                break;
            case "download":
                FragmentDownloads.adapterSongList.notifyDataSetChanged();
                break;
            case "search":
                FragmentSongBySearch.adapterSongList.notifyDataSetChanged();
                break;
        }
        ((MainActivity) Constant.context).changeImageAnimation(Constant.isPlaying);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}