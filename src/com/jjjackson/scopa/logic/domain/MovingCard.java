package com.jjjackson.scopa.logic.domain;

public class MovingCard {
    public Card card;
    public int startX;
    public int startY;
    public int endX;
    public int endY;
    public int step = 1000;
    public double speed;
    public double progress = 0;
    public double x;
    public double y;
    public boolean showBack;
}