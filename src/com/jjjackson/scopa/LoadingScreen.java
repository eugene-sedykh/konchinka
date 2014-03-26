package com.jjjackson.scopa;

import com.jjjackson.framework.Graphics;
import com.jjjackson.framework.Pixmap;
import com.jjjackson.framework.Screen;
import com.jjjackson.scopa.logic.domain.CardSuit;

import java.util.HashMap;
import java.util.Map;

public class LoadingScreen extends Screen {
    private static final String PNG = ".png";

    public LoadingScreen(ScopaGame scopaGame) {
        super(scopaGame);
    }

    @Override
    public void update(float deltaTime) {
        Graphics graphics = this.game.getGraphics();
        Assets.mainMenu = loadCardImage(graphics, "play");
        Assets.cards = loadCards(graphics);
        this.game.setScreen(new MainMenuScreen(this.game));
    }

    private Map<String, Pixmap> loadCards(Graphics graphics) {
        Map<String, Pixmap> cards = new HashMap<>();
        for (int i = 1; i < 14; i++) {
            String club = CardSuit.CLUBS.getPrefix() + i;
            cards.put(club, loadCardImage(graphics, club));

            String diamond = CardSuit.DIAMONDS.getPrefix() + i;
            cards.put(diamond, loadCardImage(graphics, diamond));

            String heart = CardSuit.HEARTS.getPrefix() + i;
            cards.put(heart, loadCardImage(graphics, heart));

            String spade = CardSuit.SPADES.getPrefix() + i;
            cards.put(spade, loadCardImage(graphics, spade));
        }
        String back = "b1fv";
        cards.put(back, loadCardImage(graphics, back));
        return cards;
    }

    private Pixmap loadCardImage(Graphics graphics, String name) {
        return graphics.newPixmap(name + PNG, Graphics.PixmapFormat.ARGB4444);
    }

    @Override
    public void present(float deltaTime) {
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
