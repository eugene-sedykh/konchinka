package com.jjjackson.scopa.logic.turn;

import com.jjjackson.scopa.logic.States;
import com.jjjackson.scopa.logic.UserType;

public class TurnMakerFactory {

    private TurnMaker computerTurnMaker;
    private TurnMaker playerTurnMaker;

    public TurnMakerFactory(States states) {
        this.computerTurnMaker = new ComputerTurnMaker(states);
        this.playerTurnMaker = new PlayerTurnMaker(states);
    }

    public TurnMaker getTurnMaker(UserType userType) {
        switch (userType) {
            case COMPUTER:
                return this.computerTurnMaker;
            default:
                return this.playerTurnMaker;
        }
    }
}
