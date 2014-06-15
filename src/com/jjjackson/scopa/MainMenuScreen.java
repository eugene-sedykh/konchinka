package com.jjjackson.scopa;

import android.util.Log;
import com.jjjackson.framework.Game;
import com.jjjackson.framework.Graphics;
import com.jjjackson.framework.Input.TouchEvent;
import com.jjjackson.framework.Screen;

import java.util.List;

import static com.jjjackson.scopa.logic.util.InputUtil.inBounds;

public class MainMenuScreen extends Screen {
    public MainMenuScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = this.game.getInput().getTouchEvents();

        for (TouchEvent touchEvent : touchEvents) {
            if (touchEvent.type == TouchEvent.TOUCH_UP) {
                if (inBounds(touchEvent, 210, 300, 59, 31)) {
                    this.game.setScreen(new GameScreen(this.game));
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics graphics = this.game.getGraphics();

        graphics.drawPixmap(Assets.mainMenu, 210, 300, 0);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
