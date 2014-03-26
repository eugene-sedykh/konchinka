package com.jjjackson.scopa.logic.util;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.Assets;
import com.jjjackson.scopa.logic.domain.Card;

public class InputUtil {

    private static final int CARD_WIDTH = Assets.cards.get("c2").getWidth();
    private static final int CARD_HEIGHT = Assets.cards.get("c2").getHeight();

    public static boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    public static boolean inBounds(Input.TouchEvent event, Card card) {
        return inBounds(event, card.position.x, card.position.y, CARD_WIDTH, CARD_HEIGHT);
    }
}
