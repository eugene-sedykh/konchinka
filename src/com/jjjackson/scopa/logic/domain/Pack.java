package com.jjjackson.scopa.logic.domain;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Pack extends MovingCard {
    public List<Card> cards = new ArrayList<>();
    public Point position = new Point();
    public double progress;
    public Point displayPosition = new Point();
    public Point hidePosition = new Point();
    public double speed;
    public int step = 500;
    public int x;
    public int y;
}
