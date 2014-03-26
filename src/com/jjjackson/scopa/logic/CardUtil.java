package com.jjjackson.scopa.logic;

import com.jjjackson.scopa.logic.domain.Card;

public class CardUtil {

    public static final int JACK_VALUE = 11;

    public static boolean isJack(Card card) {
        return JACK_VALUE == card.value;
    }
}
