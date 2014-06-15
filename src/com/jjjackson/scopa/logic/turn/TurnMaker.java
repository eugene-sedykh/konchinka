package com.jjjackson.scopa.logic.turn;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.Assets;
import com.jjjackson.scopa.logic.CardMover;
import com.jjjackson.scopa.logic.States;
import com.jjjackson.scopa.logic.domain.CardHolder;
import com.jjjackson.scopa.logic.domain.MovingCard;

import java.util.List;

public abstract class TurnMaker {
    protected MovingCard movingCard = new MovingCard();
    protected CardMover cardMover = new CardMover();
    protected States states;

    public TurnMaker(States states) {
        this.states = states;
        Assets.turnMovingCard = this.movingCard;
    }

    public abstract void make(Input input, List<CardHolder> players, float deltaTime);
}
