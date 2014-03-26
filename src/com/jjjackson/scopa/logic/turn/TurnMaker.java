package com.jjjackson.scopa.logic.turn;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.CardMover;
import com.jjjackson.scopa.logic.domain.Card;
import com.jjjackson.scopa.logic.domain.CardHolder;
import com.jjjackson.scopa.logic.domain.CardPosition;
import com.jjjackson.scopa.logic.domain.MovingCard;
import com.jjjackson.scopa.logic.util.InputUtil;

import java.util.List;

public class TurnMaker {
    public static final int PLAY_CARD_X = 210;
    public static final int PLAY_CARD_Y = 530;
    private TurnState turnState = TurnState.WAIT;
    private MovingCard movingCard = new MovingCard();
    private TurnState nextTurnState;
    private CardMover cardMover = new CardMover();

    public void make(Input input, List<CardHolder> players, float deltaTime) {
        CardHolder currentPlayer = getCurrentPlayer(players);
        if (isMyTurn(currentPlayer)) {
            switch (this.turnState) {
                case WAIT:
                    chosePlayingCard(input.getTouchEvents(), currentPlayer.playCards);
                    break;
                case MOVE_PLAY_CARD:
                    if (this.movingCard.progress < 1) {
                        this.cardMover.updateCoordinates(this.movingCard, deltaTime);
                    } else {
                        finishMovingCard();
                        this.turnState = this.nextTurnState;
                    }

                    break;
            }
        }

    }

    private void finishMovingCard() {
        this.movingCard.card.position.x = this.movingCard.endX;
        this.movingCard.card.position.y = this.movingCard.endY;
        this.movingCard.progress = 0;
    }

    private void chosePlayingCard(List<Input.TouchEvent> touchEvents, List<Card> cards) {
        for (Input.TouchEvent touchEvent : touchEvents) {
            if (touchEvent.type != Input.TouchEvent.TOUCH_UP) continue;
            for (Card card : cards) {
                if (InputUtil.inBounds(touchEvent, card)) {
                    initMovement(card);
                    this.nextTurnState = TurnState.PICK_PLAY_CARD;
                    return;
                }
            }
        }
    }

    private void initMovement(Card card) {
        this.movingCard.card = card;
        this.movingCard.startX = card.position.x;
        this.movingCard.startY = card.position.y;
        this.movingCard.endX = PLAY_CARD_X;
        this.movingCard.endY = PLAY_CARD_Y;
        double dist = this.cardMover.calculateDistance(this.movingCard);
        this.movingCard.speed = this.movingCard.step / dist;
        this.turnState = TurnState.MOVE_PLAY_CARD;
    }

    private CardHolder getCurrentPlayer(List<CardHolder> players) {
        for (CardHolder player : players) {
            if (player.isCurrent) {
                return player;
            }
        }
        return null;
    }

    private boolean isMyTurn(CardHolder player) {
        return player.cardPosition == CardPosition.BOTTOM;
    }
}
