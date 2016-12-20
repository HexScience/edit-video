package com.hecorat.editvideo.preview;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by TienDam on 11/25/2016.
 */

public class CustomVideoView extends VideoView implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer;

    public CustomVideoView(Context context, AttributeSet attributes) {
        super(context, attributes);

        this.setOnPreparedListener(this);
        this.setOnCompletionListener(this);
        this.setOnErrorListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {  return false;}

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {  }

    public void setVolume(float volume) {
        try {
            if (mediaPlayer != null)
                this.mediaPlayer.setVolume(volume, volume);
        } catch (IllegalStateException e) {
        }
    }

}
