package com.jjjackson.scopa.logic;

import com.jjjackson.scopa.logic.turn.TurnState;

public class States {

    public GameState game = GameState.NONE;

    public TurnState turn = TurnState.WAIT;

    public boolean isFaded;

    public boolean isSortButtonShown;
    public boolean isSortDoneButtonShown;
    public boolean isDoneButtonShown;
    public boolean hasTrick;
}
