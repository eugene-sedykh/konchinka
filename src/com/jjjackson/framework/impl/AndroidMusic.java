package com.jjjackson.framework.impl;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import com.jjjackson.framework.Music;

import java.io.IOException;

public class AndroidMusic implements Music, MediaPlayer.OnCompletionListener {
    private boolean isPrepared;
    private MediaPlayer mediaPlayer;

    public AndroidMusic(AssetFileDescriptor fileDescriptor) {
        this.mediaPlayer = new MediaPlayer();
        try {
            this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            this.mediaPlayer.prepare();
            this.isPrepared = true;
            this.mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load music");
        }
    }

    @Override
    public void play() {
        if (this.mediaPlayer.isPlaying()) {
            return;
        }

        try {
            synchronized (this) {
                if (!this.isPrepared) {
                    this.mediaPlayer.prepare();
                }
                this.mediaPlayer.start();
            }
        } catch (IllegalStateException | IOException e) {
            throw new RuntimeException("Cannot play music");
        }
    }

    @Override
    public void stop() {
        this.mediaPlayer.stop();
        synchronized (this) {
            this.isPrepared = false;
        }
    }

    @Override
    public void pause() {
        this.mediaPlayer.pause();
    }

    @Override
    public void setVolume(float volume) {
        this.mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    @Override
    public boolean isStopped() {
        return !this.isPrepared;
    }

    @Override
    public boolean isLooping() {
        return this.mediaPlayer.isLooping();
    }

    @Override
    public void setLooping(boolean looping) {
        this.mediaPlayer.setLooping(looping);
    }

    @Override
    public void dispose() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }
        this.mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        synchronized (this) {
            this.isPrepared = false;
        }
    }
}
