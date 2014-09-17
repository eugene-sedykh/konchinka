package com.jjjackson.scopa.logic.turn;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.Assets;
import com.jjjackson.scopa.logic.CardMover;
import com.jjjackson.scopa.logic.GameConstants;
import com.jjjackson.scopa.logic.States;
import com.jjjackson.scopa.logic.domain.*;

import java.util.ArrayList;
import java.util.List;

public abstract class TurnMaker {
    protected MovingCard movingCard = new MovingCard();
    protected CardMover cardMover = new CardMover();
    protected States states;
    protected CardCombinator cardCombinator = new CardCombinator();
    protected Card playCard;
    protected TurnState nextTurnState;
    protected List<Card> combinableCards;

    public TurnMaker(States states) {
        this.states = states;
        Assets.turnMovingCard = this.movingCard;
    }

    public abstract void make(Input input, User currentPlayer, Table table, List<CardHolder> players, float deltaTime);

    protected void initCardMovement(Card card, int endX, int endY) {
        card.marked = false;
        this.movingCard.card = card;
        this.movingCard.startX = card.position.x;
        this.movingCard.startY = card.position.y;
        this.movingCard.endX = endX;
        this.movingCard.endY = endY;

        movingCard.startDegree = movingCard.card.degree;
        movingCard.endDegree = getEndDegree(movingCard);

        this.movingCard.progress = 0;
        double dist = this.cardMover.calculateDistance(this.movingCard);
        this.movingCard.speed = this.movingCard.step / dist;
    }

    private float getEndDegree(MovingCard movingCard) {
        return movingCard.endY == GameConstants.BOTTOM_BOARD_Y || movingCard.endY == GameConstants.TOP_BOARD_Y ? 90 : 0;
    }

    protected void finishMovingCard() {
        this.movingCard.card.position.x = this.movingCard.endX;
        this.movingCard.card.position.y = this.movingCard.endY;
        this.movingCard.card.degree = this.movingCard.endDegree;
        this.movingCard.progress = -1;
    }

    protected List<Card> getPlayCardHeap(User currentPlayer, List<CardHolder> players) {
        List<Card> cards = new ArrayList<>();
        for (CardHolder player : players) {
            if (player == currentPlayer) continue;
            if (player instanceof Table) {
                cards.addAll(player.playCards);
            } else {
                User user = (User) player;
                if (user.boardCards.isEmpty()) {
                    continue;
                }
                Card card = user.boardCards.get(user.boardCards.size() - 1);
                cards.add(card);
            }
        }

        return cards;
    }
}
