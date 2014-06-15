package com.jjjackson.scopa.logic.util;

import android.graphics.Point;
import com.jjjackson.scopa.logic.GameConstants;

public class PositionCalculator {

    public static void calcBottom(int cardNumber, Point destination) {
        destination.x = 112 + cardNumber * (GameConstants.CARD_WIDTH + 20);
        destination.y = 800 - GameConstants.CARD_HEIGHT;
    }

    public static void calcLeft(int cardNumber, Point destination) {
        destination.x = 0;
        destination.y = 400 + cardNumber * 25;
    }

    public static void calcTop(int cardNumber, Point destination) {
        destination.x = 25 + cardNumber * (GameConstants.CARD_WIDTH + 20);
        destination.y = 0;
    }

    public static void calcRight(int cardNumber, Point destination) {
        destination.x = 480 - GameConstants.CARD_WIDTH;
        destination.y = 400 + cardNumber * 25;
    }

    public static void calcCenter(int cardNumber, Point destination) {
        int cardsInRow = cardNumber % 4;
        destination.x = 240 - (cardsInRow + 1) * (GameConstants.CARD_WIDTH + GameConstants.TABLE_CARDS_GAP) / 2 +
                (cardsInRow) * (GameConstants.CARD_WIDTH + GameConstants.TABLE_CARDS_GAP);
        destination.y = 225 + (cardNumber / 4 * (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP));
    }

}
