package com.jjjackson.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;
import com.jjjackson.framework.*;

public abstract class AndroidGame extends Activity implements Game {
    AndroidFastRenderView renderView;
    Graphics graphics;
    Audio audio;
    Input input;
    FileIO fileIO;
    Screen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? 800 : 480;
        int frameBufferHeight = isLandscape ? 480 : 800;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565);

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        float scaleX = frameBufferWidth / displaySize.x;
        float scaleY = frameBufferHeight / displaySize.y;

        this.renderView = new AndroidFastRenderView(this, frameBuffer);
        this.graphics = new AndroidGraphics(getAssets(), frameBuffer);
        this.fileIO = new AndroidFileIO(getAssets());
        this.audio = new AndroidAudio(this);
        this.input = new AndroidInput(this, this.renderView, scaleX, scaleY);
        this.screen = getStartScreen();

        setContentView(this.renderView);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.screen.resume();
        this.renderView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.renderView.pause();
        this.screen.pause();

        if (isFinishing()) {
            this.screen.dispose();
        }
    }

    @Override
    public Input getInput() {
        return this.input;
    }

    @Override
    public FileIO getFileIo() {
        return this.fileIO;
    }

    @Override
    public Graphics getGraphics() {
        return this.graphics;
    }

    @Override
    public Audio getAudio() {
        return this.audio;
    }

    @Override
    public void setScreen(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    @Override
    public Screen getCurrentScreen() {
        return this.screen;
    }
}
