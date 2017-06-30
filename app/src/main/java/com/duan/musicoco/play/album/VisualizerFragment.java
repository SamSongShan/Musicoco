package com.duan.musicoco.play.album;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.duan.musicoco.R;
import com.duan.musicoco.aidl.Song;
import com.duan.musicoco.app.MediaManager;
import com.duan.musicoco.app.SongInfo;

/**
 * Created by DuanJiaNing on 2017/5/30.
 */

public class VisualizerFragment extends Fragment implements ViewContract {

    public static final String TAG = "VisualizerFragment";

    private PresenterContract presenter;

    private View view;

    private ImageSwitcher albumView;

    private AlbumPictureController albumPictureController;

    // PlayActivity 需要改颜色数组
    private int[] currColors = new int[4];

    private MediaManager mediaManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_play_visualizer, null);

        mediaManager = MediaManager.getInstance(getActivity().getApplicationContext());

        initViews(view, null);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSpinner();
    }

    @Override
    public void setPresenter(PresenterContract presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initViews(@Nullable View view, Object obj) {

        albumView = (ImageSwitcher) view.findViewById(R.id.play_album_is);
        albumView.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int pad = (int) getActivity().getResources().getDimension(R.dimen.play_album_padding);
                imageView.setPadding(pad, pad, pad, pad);
                return imageView;
            }
        });

        //防止 getWidth == 0
        albumView.post(new Runnable() {
            @Override
            public void run() {
                albumPictureController = new AlbumPictureController(getActivity(), albumView);
            }
        });

    }

    @Override
    public void startSpin() {
        albumPictureController.startSpin();
    }

    @Override
    public void stopSpin() {
        albumPictureController.stopSpin();
    }

    @Override
    public void songChanged(Song song, int dir, boolean updateColors) {
        SongInfo info = song == null ? null : mediaManager.getSongInfo(song);
        if (info == null)
            return;

        if (dir == 0) {
            currColors = albumPictureController.pre(info, updateColors);
        } else {
            currColors = albumPictureController.next(info, updateColors);
        }

    }

    @Override
    public void updateSpinner() {
        if (albumPictureController != null && albumPictureController.isSpin())
            startSpin();
    }

    public int[] getCurrColors() {
        return currColors;
    }
}