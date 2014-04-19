package com.jjjackson.scopa.logic.turn;

import android.util.Log;
import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.CardMover;
import com.jjjackson.scopa.logic.GameConstants;
import com.jjjackson.scopa.logic.domain.*;
import com.jjjackson.scopa.logic.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

public class TurnMaker {
    private TurnState turnState = TurnState.WAIT;
    private MovingCard movingCard = new MovingCard();
    private TurnState nextTurnState;
    private CardMover cardMover = new CardMover();
    private Card playCard;
    private CardCombinator cardCombinator = new CardCombinator();
    private List<Card> combinableCards;
    private List<Card> combinedCards = new ArrayList<>();
    private List<Card> turnCombinedCards = new ArrayList<>();

    public void make(Input input, List<CardHolder> players, float deltaTime) {
        CardHolder currentPlayer = getCurrentPlayer(players);
        if (isMyTurn(currentPlayer)) {
            switch (this.turnState) {
                case WAIT:
                    Card touchedCard = getTouchedCard(input.getTouchEvents(), currentPlayer.playCards);
                    if (touchedCard != null) {
                        initCardMovement(touchedCard, GameConstants.PLAY_CARD_X, GameConstants.PLAY_CARD_Y);
                        this.turnState = TurnState.MOVE_PLAY_CARD;
                        this.nextTurnState = TurnState.PICK_PLAY_CARD;
                    }
                    break;
                case MOVE_PLAY_CARD:
                    if (this.movingCard.progress < 1) {
                        this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                    } else {
                        finishMovingCard();
                        this.playCard = this.movingCard.card;
                        this.combinableCards = getCardsToTake(players);
                        this.turnState = this.combinableCards.isEmpty() ? TurnState.PLAY_CARD_TO_TABLE :
                                TurnState.COMBINE_CARDS;
                    }
                    break;
                case COMBINE_CARDS:
                    combineCards(input.getTouchEvents(), players);
                    break;
                case TAKE_PLAY_CARD:
                    this.playCard.marked = !this.playCard.marked;
                    this.turnState = TurnState.COMBINE_CARDS;
                    break;
                case TAKE_COMBINED_CARDS:
                    if (!this.combinedCards.isEmpty()) {
                        if (this.movingCard.progress == -1) {
                            initCardMovement(this.combinedCards.get(0), GameConstants.BOTTOM_BOARD_X,
                                    GameConstants.BOTTOM_BOARD_Y);
                        } else if (this.movingCard.progress < 1) {
                            this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                            Log.i("konchinka", "x: " + this.movingCard.card.position.x + "\ty: " + this.movingCard.card.position.y + "\tprogress: " + this.movingCard.progress);
                        } else {
                            finishMovingCard();
                            ((User) currentPlayer).boardCards.add(this.movingCard.card);
                            this.turnCombinedCards.add(this.movingCard.card);
                            getTableCards(players).remove(this.movingCard.card);
                            this.combinedCards.remove(0);
                        }
                        return;
                    }
                    this.combinableCards = getCardsToTake(players);
                    this.turnState = TurnState.COMBINE_CARDS;
                    break;
            }
        }
    }

    private List<Card> getTableCards(List<CardHolder> players) {
        for (CardHolder player : players) {
            if (player instanceof Table) {
                return player.playCards;
            }
        }
        throw new RuntimeException("wtf??? How can I play without a table???");
    }

    private void combineCards(List<Input.TouchEvent> touchEvents, List<CardHolder> players) {
        List<Card> playCardHeap = getPlayCardHeap(players);
        if (this.playCard != null) {
            playCardHeap.add(this.playCard);
        }

        Card touchedCard = getTouchedCard(touchEvents, playCardHeap);

        if (touchedCard == null) return;
        if (touchedCard == this.playCard && this.combinedCards.isEmpty()) {
            this.turnState = TurnState.TAKE_PLAY_CARD;
            return;
        }

        if (this.combinedCards.contains(touchedCard)) {
            this.combinedCards.remove(touchedCard);
            touchedCard.marked = false;
            this.combinableCards = getCardsToTake(players);
        } else if (this.combinableCards.contains(touchedCard)) {
            this.combinedCards.add(touchedCard);
            touchedCard.marked = true;
            this.combinableCards = getCardsToTake(players);

            int sum = this.cardCombinator.getSum(this.combinedCards);
            if (sum == this.playCard.value) {
                this.turnState = TurnState.TAKE_COMBINED_CARDS;
            }
        }
    }

    private List<Card> getCardsToTake(List<CardHolder> players) {
        List<Card> playCardHeap = getPlayCardHeap(players);
        playCardHeap.removeAll(this.combinedCards);

        return this.cardCombinator.filterCombinableCards(playCardHeap, this.playCard.value -
                this.cardCombinator.getSum(this.combinedCards));
    }

    private List<Card> getPlayCardHeap(List<CardHolder> players) {
        List<Card> cards = new ArrayList<>();
        for (CardHolder player : players) {
            if (player.cardPosition == CardPosition.BOTTOM) continue;
            if (player instanceof Table) {
                cards.addAll(player.playCards);
            } else {
                cards.addAll(((User) player).boardCards);
            }
        }

        return cards;
    }

    private void finishMovingCard() {
        this.movingCard.card.position.x = this.movingCard.endX;
        this.movingCard.card.position.y = this.movingCard.endY;
        this.movingCard.card.degree = this.movingCard.endDegree;
        this.movingCard.progress = -1;
    }

    private Card getTouchedCard(List<Input.TouchEvent> touchEvents, List<Card> cards) {
        for (Input.TouchEvent touchEvent : touchEvents) {
            if (touchEvent.type != Input.TouchEvent.TOUCH_UP) continue;
            for (Card card : cards) {
                if (InputUtil.inBounds(touchEvent, card)) {
                    return card;
                }
            }
        }
        return null;
    }

    private void initCardMovement(Card card, int endX, int endY) {
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
