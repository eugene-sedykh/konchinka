package com.jjjackson.framework.impl;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import com.jjjackson.framework.Audio;
import com.jjjackson.framework.Music;
import com.jjjackson.framework.Sound;

import java.io.IOException;

public class AndroidAudio implements Audio {
    AssetManager assets;
    SoundPool soundPool;

    public AndroidAudio(Activity activity) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assets = activity.getAssets();
        this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public Music newMusic(String fileName) {
        try {
            AssetFileDescriptor fileDescriptor = this.assets.openFd(fileName);
            return new AndroidMusic(fileDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load music:" + fileName);
        }
    }

    @Override
    public Sound newSound(String fileName) {
        try {
            AssetFileDescriptor fileDescriptor = this.assets.openFd(fileName);
            int soundId = this.soundPool.load(fileDescriptor, 0);
            return new AndroidSound(soundPool, soundId);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load sound:" + fileName);
        }
    }
}
