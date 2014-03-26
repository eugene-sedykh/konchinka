package com.jjjackson.scopa;

import com.jjjackson.framework.Screen;
import com.jjjackson.framework.impl.AndroidGame;

public class ScopaGame extends AndroidGame {
    @Override
    public Screen getStartScreen() {
        return new LoadingScreen(this);
    }
}
