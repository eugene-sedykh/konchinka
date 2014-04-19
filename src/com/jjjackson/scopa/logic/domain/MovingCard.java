package com.jjjackson.scopa.logic.domain;

public class MovingCard {
    public Card card;
    public int startX;
    public int startY;
    public int endX;
    public int endY;
    public int step = 2000;
    public double speed;
    public double progress = 0;
    public boolean showBack;

    public float startDegree;
    public float endDegree;
    public double rotationSpeed;
}