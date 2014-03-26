package com.jjjackson.framework.impl;

import android.media.SoundPool;
import com.jjjackson.framework.Sound;

public class AndroidSound implements Sound {
    private SoundPool soundPool;
    private int soundId;

    public AndroidSound(SoundPool soundPool, int soundId) {
        this.soundPool = soundPool;
        this.soundId = soundId;
    }

    @Override
    public void play(float volume) {
        this.soundPool.play(this.soundId, volume, volume, 0, 0, 1);
    }

    @Override
    public void dispose() {
        this.soundPool.unload(this.soundId);
    }
}
