package com.jjjackson.framework.impl;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.jjjackson.framework.Input.TouchEvent;
import com.jjjackson.framework.Pool;
import com.jjjackson.framework.PoolObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class MultiTouchHandler implements TouchHandler {
    private boolean[] isTouched = new boolean[20];
    private int[] touchX = new int[20];
    private int[] touchY = new int[20];
    private Pool<TouchEvent> touchEventPool;
    private List<TouchEvent> touchEvents = new ArrayList<>();
    private List<TouchEvent> touchEventsBuffer = new ArrayList<>();
    private float scaleX;
    private float scaleY;

    public MultiTouchHandler(View view, float scaleX, float scaleY) {
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
            return !(pointer < 0 || pointer >= 20) && isTouched[pointer];
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized (this) {
            if (pointer < 0 || pointer >= 20)
                return 0;
            else
                return touchX[pointer];
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized (this) {
            if (pointer < 0 || pointer >= 20)
                return 0;
            else
                return touchY[pointer];
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            for (TouchEvent touchEvent : touchEvents) {
                touchEventPool.free(touchEvent);
            }
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            int pointerId = event.getPointerId(pointerIndex);
            TouchEvent touchEvent;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    touchEvent = touchEventPool.newObject();
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    touchEvent.pointer = pointerId;
                    touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex) * scaleX);
                    touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex) * scaleY);
                    isTouched[pointerId] = true;
                    touchEventsBuffer.add(touchEvent);
//                    Log.i("Konchinka", "new : x=" + touchEvent.x + "; y=" + touchEvent.y + "; type=" + touchEvent.type);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL:
                    touchEvent = touchEventPool.newObject();
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    touchEvent.pointer = pointerId;
                    touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex) * scaleX);
                    touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex) * scaleY);
                    isTouched[pointerId] = false;
                    touchEventsBuffer.add(touchEvent);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int pointerCount = event.getPointerCount();
                    for (int i = 0; i < pointerCount; i++) {
                        pointerIndex = i;
                        pointerId = event.getPointerId(pointerIndex);
                        touchEvent = touchEventPool.newObject();
                        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                        touchEvent.pointer = pointerId;
                        touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex) * scaleX);
                        touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex) * scaleY);
                        touchEventsBuffer.add(touchEvent);
                    }
                    break;
            }
            return true;
        }
    }
}
