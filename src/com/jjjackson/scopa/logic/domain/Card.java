package com.jjjackson.scopa.logic.domain;

import android.graphics.Point;

public class Card {

    public CardSuit cardSuit;
    public int value;
    public String shortName;
    public Point position = new Point();
    public boolean marked;
    public float degree;

    public Card(CardSuit cardSuit, int value) {
        this.cardSuit = cardSuit;
        this.value = value;
        this.shortName = cardSuit.getPrefix() + value;
    }
}
