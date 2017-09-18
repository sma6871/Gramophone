package com.rahnema.gramophone.activities.main.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jackandphantom.circularprogressbar.CircleProgressbar;
import com.rahnema.gramophone.R;
import com.rahnema.gramophone.base.BaseFragment;
import com.rahnema.gramophone.ext.BundleExtraKeys;
import com.rahnema.gramophone.ext.Utils;
import com.rahnema.gramophone.models.Song;

import org.parceler.Parcels;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MusicPlayerFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;
    Song song;

    boolean isPlaying = false;
    boolean isLoopEnable = false;

    @BindView(R.id.imgCover)
    CircleImageView imgCover;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.txtArtistName)
    TextView txtArtistName;
    @BindView(R.id.txtDuration)
    TextView txtDuration;

    @BindView(R.id.fabPlay)
    FloatingActionButton fabPlay;
    @BindView(R.id.fabRepeat)
    FloatingActionButton fabRepeat;

    @BindView(R.id.progressBar)
    CircleProgressbar progressbar;

    private Handler mHandler = new Handler();

    long duration = 0;
    long pos = 0;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        ButterKnife.bind(this, view);

        song = Parcels.unwrap(getArguments().getParcelable(BundleExtraKeys.Song));

        Utils.loadAlbumArt(getContext(), imgCover, song.getAlbumId());

        txtArtistName.setText(song.getArtistName());
        txtTitle.setText(song.getTitle());
        long duration = song.getDuration();

        updateDurationText();

        progressbar.setOnProgressbarChangeListener(new CircleProgressbar.OnProgressbarChangeListener() {
            @Override
            public void onProgressChanged(CircleProgressbar circleSeekbar, float progress, boolean fromUser) {

            }

            @Override
            public void onStartTracking(CircleProgressbar circleSeekbar) {

            }

            @Override
            public void onStopTracking(CircleProgressbar circleSeekbar) {
                float progress = circleSeekbar.getProgress();
                mListener.seekTo((int) (progress * duration / 100));
            }
        });

        mHandler.postDelayed(mUpdateTimeTask, 100);


        return view;
    }

    private void updateDurationText() {
        long remain = duration - pos;
        txtDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(remain),
                TimeUnit.MILLISECONDS.toSeconds(remain) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remain))
        ));
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            duration = song.getDuration();
            long currentDuration = pos = mListener.refreshPos();

            progressbar.setProgress(((float) currentDuration * 100f / (float) duration));

            updateDurationText();

            mHandler.postDelayed(this, 100);
        }
    };

    @OnClick(R.id.fabPlay)
    public void onPlayPressed() {
        if (mListener != null) {
            mListener.onPlayClick();
            togglePlayIcon();
        }
    }

    @OnClick(R.id.fabRepeat)
    public void onLoopPressed() {
        if (mListener != null) {
            mListener.onLoopClick();
            toggleLoopIcon();
        }
    }

    @OnClick(R.id.fabNext)
    public void onNextPressed() {
        if (mListener != null) {
            mListener.onNextClick();

        }
    }
    @OnClick(R.id.fabNext)
    public void onPrevPressed() {
        if (mListener != null) {
            mListener.onPrevClick();

        }
    }


    private void toggleLoopIcon() {
        if (isLoopEnable) {
            fabRepeat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.fabDisableColor)));
        } else
            fabRepeat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        isLoopEnable = !isLoopEnable;
    }


    public void togglePlayIcon() {
        if (isPlaying) {
            fabPlay.setImageResource(R.drawable.ic_pause_black_24dp);
        } else
            fabPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        isPlaying = !isPlaying;
    }

    public void setPlayIcon() {
        fabPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    public void setPauseIcon() {
        fabPlay.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onPlayClick();

        void onLoopClick();

        long refreshPos();

        void seekTo(int progress);

        void onPrevClick();

        void onNextClick();
    }
}
