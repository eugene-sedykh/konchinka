package com.jjjackson.scopa.logic.turn;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.GameConstants;
import com.jjjackson.scopa.logic.States;
import com.jjjackson.scopa.logic.domain.Card;
import com.jjjackson.scopa.logic.domain.CardHolder;
import com.jjjackson.scopa.logic.domain.User;

import java.util.List;

public class ComputerTurnMaker extends TurnMaker {

    public ComputerTurnMaker(States states) {
        super(states);
    }

    @Override
    public void make(Input input, User currentPlayer, List<CardHolder> players, float deltaTime) {
        switch (this.states.turn) {
            case WAIT:
                if (this.movingCard.progress == -1) {
                    Card playCard = choosePlayCard();
                    initCardMovement(playCard, GameConstants.PLAY_CARD_X, GameConstants.PLAY_CARD_Y);
                } else if (this.movingCard.progress < 1) {
                    this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                } else {
                    finishMovingCard();
                    this.states.turn = TurnState.COMBINE_CARDS;
                }
                break;

        }
    }
}
