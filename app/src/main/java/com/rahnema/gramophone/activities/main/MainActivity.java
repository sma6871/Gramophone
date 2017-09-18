package com.rahnema.gramophone.activities.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rahnema.gramophone.R;
import com.rahnema.gramophone.activities.about.AboutActivity;
import com.rahnema.gramophone.activities.main.fragments.MusicListFragment;
import com.rahnema.gramophone.activities.main.fragments.MusicPlayerFragment;
import com.rahnema.gramophone.base.BaseActivity;
import com.rahnema.gramophone.enums.MessageType;
import com.rahnema.gramophone.ext.ActivityUtils;
import com.rahnema.gramophone.ext.BundleExtraKeys;
import com.rahnema.gramophone.models.Song;
import com.rahnema.gramophone.services.MusicPlayerService;

import org.parceler.Parcel;
import org.parceler.Parcels;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements MusicListFragment.OnListFragmentInteractionListener,
        MusicPlayerFragment.OnFragmentInteractionListener, MusicPlayerService.OnServiceEventListener {


    private MusicPlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    //activity and playback pause flags
    private boolean paused = false, playbackPaused = false;

    MusicPlayerFragment musicPlayerFragment;

    @BindView(R.id.miniPlayer)
    RelativeLayout miniPlayerLayout;

    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtArtistName)
    TextView txtArtistName;

    Song currentSong;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewReady(@Nullable Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        musicPlayerFragment = new MusicPlayerFragment();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), new MusicListFragment(), R.id.listFragment, false);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_bourse:
                showMessage(MessageType.Info, R.string.menu_bourse);
                return true;
            case R.id.navigation_safe_gardoon:
                showMessage(MessageType.Info, R.string.menu_safe_gardoon);
                return true;
            case R.id.navigation_dast_aval:
                showMessage(MessageType.Info, R.string.menu_dast_aval);
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
    };


    @Override
    public void onListFragmentInteraction(Song song) {
        currentSong = song;
        musicSrv.setCurrentSong(song);
        musicSrv.playSong();
        Bundle args = new Bundle();
        args.putParcelable(BundleExtraKeys.Song, Parcels.wrap(song));
        musicPlayerFragment.setArguments(args);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), musicPlayerFragment, R.id.miniPlayer, true);

        showMiniLayout();
    }

    @Override
    public void onPlayClick() {
        musicSrv.togglePlayPause();
    }

    @Override
    public void onLoopClick() {
        musicSrv.toggleRepeat();
    }

    @Override
    public long refreshPos() {
        return musicSrv.getCurrentPos();
    }

    @Override
    public void seekTo(int progress) {
        musicSrv.seekTo(progress);
    }

    @Override
    public void onPrevClick() {
        musicSrv.playPrev();
    }

    @Override
    public void onNextClick() {
        musicSrv.playNext();
    }

    //user song select
    public void songPicked(View view){
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if(playbackPaused){

            playbackPaused=false;
        }

    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            musicSrv.setListener(MainActivity.this);
            //pass list
            musicSrv.setList(MusicListFragment.ITEM_MAP);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void showMiniLayout() {
        Bundle args = new Bundle();
        Song song = musicSrv.getCurrentSong();
        args.putParcelable(BundleExtraKeys.Song, Parcels.wrap(song));
        musicPlayerFragment.setArguments(args);
        txtTitle.setText(song.getTitle());
        txtArtistName.setText(song.getArtistName());
        miniPlayerLayout.setAlpha(0f);
        miniPlayerLayout.setVisibility(View.VISIBLE);
        miniPlayerLayout.animate().alpha(1f).setDuration(500).start();
        miniPlayerLayout.setOnClickListener(v -> {
            if (getSupportFragmentManager().getFragments().contains(musicPlayerFragment))
                return;
            args.putParcelable(BundleExtraKeys.Song, Parcels.wrap(song));
            musicPlayerFragment.setArguments(args);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), musicPlayerFragment, R.id.miniPlayer, true);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unbindService(musicConnection);
//        stopService(playIntent);
//        musicSrv=null;
        super.onDestroy();
    }

    @Override
    public void onMusicPlayComplete() {
        musicPlayerFragment.togglePlayIcon();
    }
}
