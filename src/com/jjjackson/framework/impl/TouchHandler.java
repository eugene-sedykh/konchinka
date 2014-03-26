package com.jjjackson.framework.impl;

import android.view.View;
import com.jjjackson.framework.Input.TouchEvent;

import java.util.List;

public interface TouchHandler extends View.OnTouchListener {
    public boolean isTouchDown(int pointer);

    public int getTouchX(int pointer);

    public int getTouchY(int pointer);

    public List<TouchEvent> getTouchEvents();
}
