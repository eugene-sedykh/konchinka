package com.jjjackson.framework;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import com.jjjackson.framework.impl.*;

import java.util.List;

public class AndroidInput implements Input {
    private AccelerometerHandler accelerometerHandler;
    private KeyboardHandler keyboardHandler;
    private TouchHandler touchHandler;

    public AndroidInput(Context context, View view, float scaleX, float scaleY) {
        this.accelerometerHandler = new AccelerometerHandler(context);
        this.keyboardHandler = new KeyboardHandler(view);
        if (Build.VERSION.SDK_INT < 5) {
            this.touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
        } else {
            this.touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
        }
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return this.keyboardHandler.isKeyPressed(keyCode);
    }

    @Override
    public boolean isTouchDown(int pointer) {
        return this.touchHandler.isTouchDown(pointer);
    }

    @Override
    public int getTouchX(int pointer) {
        return this.touchHandler.getTouchX(pointer);
    }

    @Override
    public int getTouchY(int pointer) {
        return this.touchHandler.getTouchY(pointer);
    }

    @Override
    public float getAccelX() {
        return this.accelerometerHandler.getAccelX();
    }

    @Override
    public float getAccelY() {
        return this.accelerometerHandler.getAccelY();
    }

    @Override
    public float getAccelZ() {
        return this.accelerometerHandler.getAccelZ();
    }

    @Override
    public List<KeyEvent> getKeyEvents() {
        return this.keyboardHandler.getKeyEvents();
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        //        for (TouchEvent touchEvent : touchEvents) {
//            String msg = "x: " + touchEvent.x + "; y: " + touchEvent.y + "; type: " + touchEvent.type;
//            Log.i(this.getClass().getName(), msg);
//        }
        return this.touchHandler.getTouchEvents();
    }
}
