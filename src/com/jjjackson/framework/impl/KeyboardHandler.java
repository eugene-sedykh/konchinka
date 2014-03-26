package com.jjjackson.framework.impl;

import android.view.View;
import com.jjjackson.framework.Input;
import com.jjjackson.framework.Input.KeyEvent;
import com.jjjackson.framework.Pool;
import com.jjjackson.framework.PoolObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class KeyboardHandler implements View.OnKeyListener {
    boolean[] pressedKeys = new boolean[128];
    Pool<KeyEvent> keyEventPool;
    List<KeyEvent> keyEventsBuffer = new ArrayList<>();
    List<KeyEvent> keyEvents = new ArrayList<>();

    public KeyboardHandler(View view) {
        PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
            @Override
            public KeyEvent createObject() {
                return new KeyEvent();
            }
        };
        this.keyEventPool = new Pool<KeyEvent>(factory, 100);
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override
    public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
        if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) {
            return false;
        }

        synchronized (this) {
            KeyEvent keyEvent = this.keyEventPool.newObject();
            keyEvent.keyCode = keyCode;
            keyEvent.keyChar = (char) event.getUnicodeChar();
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                keyEvent.type = KeyEvent.KEY_DOWN;
                if (keyCode > 0 && keyCode < 127) {
                    pressedKeys[keyCode] = true;
                }
            }
            if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
                keyEvent.type = KeyEvent.KEY_UP;
                if (keyCode > 0 && keyCode < 127) {
                    pressedKeys[keyCode] = false;
                }
            }
            this.keyEventsBuffer.add(keyEvent);
        }
        return false;
    }

    public boolean isKeyPressed(int keyCode) {
        return !(keyCode < 0 || keyCode > 127) && this.pressedKeys[keyCode];
    }

    public List<KeyEvent> getKeyEvents() {
        synchronized (this) {
            for (KeyEvent keyEvent : this.keyEvents) {
                this.keyEventPool.free(keyEvent);
            }
            this.keyEvents.clear();
            this.keyEvents.addAll(this.keyEventsBuffer);
            this.keyEventsBuffer.clear();
            return this.keyEvents;
        }
    }
}
