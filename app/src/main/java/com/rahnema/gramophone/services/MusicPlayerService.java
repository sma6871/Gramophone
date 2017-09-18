package com.rahnema.gramophone.services;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.rahnema.gramophone.models.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mMediaPlayer = null;
    private List<Song> songs;

    int songPosn;
    Song currentSong;
    OnServiceEventListener mListener;
    boolean isLoopEnabled = false;
    private final IBinder musicBind = new MusicBinder();

    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;

    }

    public void setListener(OnServiceEventListener mListener) {
        this.mListener = mListener;
    }
    //    @Override
//    public boolean onUnbind(Intent intent) {
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
//        return false;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn = 0;
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
    }

    public void setList(List<Song> theSongs) {
        songs = theSongs;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public Song getCurrentSong() {
        return currentSong;

    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public long getCurrentPos() {
        return mMediaPlayer.getCurrentPosition();
    }


    /**
     * Called when MediaPlayer is ready
     */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mMediaPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
        if (isLoopEnabled)
            playSong();
        else if (mListener != null) mListener.onMusicPlayComplete();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void togglePlayPause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.pause();
            else mMediaPlayer.start();
        }
    }

    public void toggleRepeat() {
        isLoopEnabled = !isLoopEnabled;
    }

    public boolean isPlaying() {
        return (mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    public void seekTo(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    //skip to previous track
    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {

        songPosn++;
        if (songPosn >= songs.size()) songPosn = 0;

        playSong();
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    public void playSong() {
        mMediaPlayer.reset();

        Song playSong = songs.get(songPosn);
        long currSong = playSong.getId();


        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }


    public interface OnServiceEventListener {

        void onMusicPlayComplete();

    }
}
