package com.jjjackson.framework.impl;

import android.view.MotionEvent;
import android.view.View;
import com.jjjackson.framework.Input;
import com.jjjackson.framework.Input.TouchEvent;
import com.jjjackson.framework.Pool;
import com.jjjackson.framework.PoolObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class SingleTouchHandler implements TouchHandler {
    boolean isTouched;
    int touchX;
    int touchY;
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<>();
    float scaleX;
    float scaleY;

    public SingleTouchHandler(View view, float scaleX, float scaleY) {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        };
        touchEventPool = new Pool<>(factory, 100);
        view.setOnTouchListener(this);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized (this) {
            return pointer == 0 && this.isTouched;
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            return this.touchX;
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            return this.touchY;
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            int length = this.touchEvents.size();
            for (TouchEvent touchEvent : this.touchEvents) {
                this.touchEventPool.free(touchEvent);
            }
            this.touchEvents.clear();
            this.touchEvents.addAll(this.touchEventsBuffer);
            this.touchEventsBuffer.clear();
            return this.touchEvents;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            TouchEvent touchEvent = this.touchEventPool.newObject();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    this.isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    this.isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    this.isTouched = false;
                    break;
            }
            touchEvent.x = this.touchX = (int) (event.getX() * this.scaleX);
            touchEvent.y = this.touchY = (int) (event.getY() * this.scaleY);
            this.touchEventsBuffer.add(touchEvent);

            return true;
        }
    }
}
