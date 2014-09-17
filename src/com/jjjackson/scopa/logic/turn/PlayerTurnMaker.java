package com.jjjackson.scopa.logic.turn;

import android.graphics.Point;
import android.util.Log;
import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.GameConstants;
import com.jjjackson.scopa.logic.GameState;
import com.jjjackson.scopa.logic.States;
import com.jjjackson.scopa.logic.domain.*;
import com.jjjackson.scopa.logic.util.InputUtil;
import com.jjjackson.scopa.logic.util.PositionCalculator;

import java.util.ArrayList;
import java.util.List;

public class PlayerTurnMaker extends TurnMaker {

    private List<Card> combinedCards = new ArrayList<>();
    private List<Card> turnCombinedCards = new ArrayList<>();
    private List<Card> sortingCards = new ArrayList<>();
    private int startTurnTableCardsNumber;
    private int playCardValue;

    public PlayerTurnMaker(States states) {
        super(states);
    }

    @Override
    public void make(Input input, User currentPlayer, Table table, List<CardHolder> players, float deltaTime) {
        switch (this.states.turn) {
            case WAIT:
                Card touchedCard = getTouchedCard(input.getTouchEvents(), currentPlayer.playCards);
                if (touchedCard != null) {
                    initCardMovement(touchedCard, GameConstants.PLAY_CARD_X, GameConstants.PLAY_CARD_Y);
                    this.states.turn = TurnState.MOVE_PLAY_CARD;
                }
                break;
            case MOVE_PLAY_CARD:
                if (this.movingCard.progress < 1) {
                    this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                } else {
                    finishMovingCard();
                    this.playCard = this.movingCard.card;
                    this.playCardValue = this.playCard.value;
                    this.combinableCards = getCardsToTake(getPlayCardHeap(currentPlayer, players));
                    if (this.playCard.value != GameConstants.JACK_VALUE)
                        if (this.combinableCards.isEmpty()) {
                            this.states.turn = TurnState.PLAY_CARD_TO_TABLE;
                        } else {
                            this.states.turn = TurnState.COMBINE_CARDS;
                            this.startTurnTableCardsNumber = getTableCards(players).size();
                        }
                    else {
                        if (this.combinableCards.isEmpty() && getTableCards(players).isEmpty()) {
                            this.states.turn = TurnState.PLAY_CARD_TO_TABLE;
                        } else {
                            this.states.turn = TurnState.COMBINE_JACK_CARDS;
                            this.startTurnTableCardsNumber = getTableCards(players).size();
                        }
                    }
                }
                break;
            case COMBINE_CARDS:
                combineCards(input.getTouchEvents(),currentPlayer, players);
                break;
            case COMBINE_JACK_CARDS:
                combineJackCards(input.getTouchEvents(), currentPlayer, players);
                break;
            case TAKE_PLAY_CARD:
                if (this.movingCard.progress == -1) {
                    initCardMovement(this.playCard, GameConstants.BOTTOM_BOARD_X,
                            GameConstants.BOTTOM_BOARD_Y);
                } else if (this.movingCard.progress < 1) {
                    this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                } else {
                    finishMovingCard();
                    currentPlayer.boardCards.add(this.movingCard.card);
                    currentPlayer.playCards.remove(this.movingCard.card);
                    this.turnCombinedCards.add(this.movingCard.card);
                    this.playCard = null;
                    this.states.turn = TurnState.COMBINE_CARDS;
                }
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
                        currentPlayer.boardCards.add(this.movingCard.card);
                        this.turnCombinedCards.add(this.movingCard.card);
                        List<Card> tableCards = getTableCards(players);
                        tableCards.remove(this.movingCard.card);
                        if (this.playCard != null && this.playCard == this.movingCard.card) {
                            currentPlayer.playCards.remove(this.playCard);
                        }
                        this.cardMover.changeCenterCardsPosition(tableCards, false);
                        this.combinedCards.remove(0);
                    }
                    return;
                }
                this.combinableCards = getCardsToTake(getPlayCardHeap(currentPlayer, players));
                this.states.turn = this.nextTurnState;
                break;
            case PLAY_CARD_TO_TABLE:
                if (this.movingCard.progress == -1) {
                    List<Card> tableCards = getTableCards(players);
                    this.cardMover.changeCenterCardsPosition(tableCards, false);
                    Point destination = new Point();
                    PositionCalculator.calcCenter(tableCards.size(), destination);
                    initCardMovement(this.playCard, destination.x, destination.y);
                } else if (this.movingCard.progress < 1) {
                    this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                    Log.i("konchinka", "x: " + this.movingCard.card.position.x + "\ty: " + this.movingCard.card.position.y + "\tprogress: " + this.movingCard.progress);
                } else {
                    finishMovingCard();
                    getTableCards(players).add(this.playCard);
                    currentPlayer.playCards.remove(this.playCard);
                    this.playCard = null;
                    this.states.game = GameState.NEXT_TURN;
                }
                break;
            case SHOW_END_TURN_BUTTON:
                List<Input.TouchEvent> touchEvents = input.getTouchEvents();
                if (isEndButtonPressed(touchEvents)) {
                    finishTurn();
                    this.states.game = GameState.NEXT_TURN;
                    return;
                }
                if (isSortButtonPressed(touchEvents)) {
                    this.states.turn = TurnState.SORT_CARDS_OUT;
                    this.nextTurnState = TurnState.SORT_CARDS_PRESS;
                    this.states.isFaded = true;
                }
                break;
            case SHOW_TRICK_BUTTON:
                List<Input.TouchEvent> trickTouchEvents = input.getTouchEvents();
                if (isEndButtonPressed(trickTouchEvents)) {
                    this.states.turn = TurnState.TRICK_CARDS_OUT;
                    this.states.isFaded = true;
                }
                if (isSortButtonPressed(trickTouchEvents)) {
                    this.states.turn = TurnState.SORT_CARDS_OUT;
                    this.states.isFaded = true;
                }
                break;
            case SORT_CARDS_OUT:
                if (!this.turnCombinedCards.isEmpty()) {
                    takenCardsOut(deltaTime);
                    return;
                }
                this.states.turn = TurnState.SORT_CARDS_PRESS;
                this.states.isSortDoneButtonShown = true;
                break;
            case SORT_CARDS_PRESS:
                pressSortingCards(input.getTouchEvents(), currentPlayer, deltaTime);
                break;
            case SORT_CARDS_IN:
                if (!this.sortingCards.isEmpty()) {
                    if (this.movingCard.progress == -1) {
                        initCardMovement(this.sortingCards.get(this.sortingCards.size() - 1),
                                GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y);
                    } else if (this.movingCard.progress < 1) {
                        this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                    } else {
                        finishMovingCard();
                        this.sortingCards.remove(this.movingCard.card);
                        this.turnCombinedCards.add(this.movingCard.card);
                        this.cardMover.changeCenterCardsPosition(this.sortingCards, false, true);
                    }
                    return;
                }
                finishCardSorting();
                break;
            case TRICK_CARDS_OUT:
                if (!this.turnCombinedCards.isEmpty()) {
                    takenCardsOut(deltaTime);
                    return;
                }
                this.states.turn = TurnState.TRICK_CARDS_PRESS;
                break;
            case TRICK_CARDS_PRESS:
                chooseTrick(input.getTouchEvents(), currentPlayer, deltaTime);
                break;
        }
    }

    private void finishTurn() {
        this.turnCombinedCards.clear();
        this.states.turn = TurnState.WAIT;
    }

    private void chooseTrick(List<Input.TouchEvent> touchEvents, User currentPlayer, float deltaTime) {
        if (this.movingCard.progress == -1) {
            Card touchedCard = getTouchedCard(touchEvents, this.sortingCards);
            if (touchedCard == null) return;

            initCardMovement(touchedCard, GameConstants.BOTTOM_TRICK_X, GameConstants.BOTTOM_TRICK_Y);
        } else if (this.movingCard.progress < 1) {
            this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
        } else {
            finishMovingCard();
            this.sortingCards.remove(this.movingCard.card);
            currentPlayer.boardCards.remove(this.movingCard.card);
            currentPlayer.tricks.add(this.movingCard.card);

            this.cardMover.changeCenterCardsPosition(this.sortingCards, false, true);

            this.states.hasTrick = false;
            this.states.turn = TurnState.SORT_CARDS_IN;
        }
    }

    private void takenCardsOut(float deltaTime) {
        if (this.movingCard.progress == -1) {
            this.cardMover.changeCenterCardsPosition(this.sortingCards, true, true);
            Point destination = new Point();
            PositionCalculator.calcCenter(this.sortingCards.size(), destination);
            destination.y += (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP) / 2;
            initCardMovement(this.turnCombinedCards.get(this.turnCombinedCards.size() - 1), destination.x, destination.y);
        } else if (this.movingCard.progress < 1) {
            this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
        } else {
            finishMovingCard();
            this.sortingCards.add(this.movingCard.card);
            this.turnCombinedCards.remove(this.movingCard.card);
        }
    }

    private void pressSortingCards(List<Input.TouchEvent> touchEvents, CardHolder currentPlayer, float deltaTime) {
        if (this.movingCard.progress == -1) {
            if (isSortButtonPressed(touchEvents)) {
                this.states.turn = TurnState.SORT_CARDS_IN;
                return;
            }

            Card touchedCard = getTouchedCard(touchEvents, this.sortingCards);
            if (touchedCard == null) return;

            initCardMovement(touchedCard, GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y);
        } else if (this.movingCard.progress < 1) {
            this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
        } else {
            finishMovingCard();
            this.turnCombinedCards.add(this.movingCard.card);
            this.sortingCards.remove(this.movingCard.card);
            ((User) currentPlayer).boardCards.remove(this.movingCard.card);
            ((User) currentPlayer).boardCards.add(this.movingCard.card);
            this.cardMover.changeCenterCardsPosition(this.sortingCards, false, true);

            if (this.sortingCards.isEmpty()) {
                finishCardSorting();
            }
        }
    }

    private void finishCardSorting() {
        this.states.turn = this.states.hasTrick ? TurnState.SHOW_TRICK_BUTTON : TurnState.SHOW_END_TURN_BUTTON;
        this.states.isSortDoneButtonShown = false;
        this.states.isFaded = false;
    }

    private boolean isSortButtonPressed(List<Input.TouchEvent> touchEvents) {
        for (Input.TouchEvent touchEvent : touchEvents) {
            if (touchEvent.type == Input.TouchEvent.TOUCH_DOWN &&
                    InputUtil.inBounds(touchEvent, GameConstants.SORT_BUTTON_X, GameConstants.SORT_BUTTON_Y,
                            GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEndButtonPressed(List<Input.TouchEvent> touchEvents) {
        for (Input.TouchEvent touchEvent : touchEvents) {
            if (touchEvent.type == Input.TouchEvent.TOUCH_DOWN &&
                    InputUtil.inBounds(touchEvent, GameConstants.END_BUTTON_X, GameConstants.END_BUTTON_Y,
                            GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT)) {
                return true;
            }
        }
        return false;
    }

    private void combineCards(List<Input.TouchEvent> touchEvents, User currentPlayer, List<CardHolder> players) {
        List<Card> playCardHeap = getPlayCardHeap(currentPlayer, players);
        if (this.playCard != null) {
            playCardHeap.add(this.playCard);
        }

        if (this.combinableCards.isEmpty() && this.playCard == null) {
            if (hasTrick(players)) {
                this.states.hasTrick = true;
                this.states.turn = TurnState.SHOW_TRICK_BUTTON;
            } else {
                this.states.hasTrick = false;
                this.states.turn = TurnState.SHOW_END_TURN_BUTTON;
            }
            return;
        }

        Card touchedCard = getTouchedCard(touchEvents, playCardHeap);

        if (touchedCard == null) return;
        if (touchedCard == this.playCard && this.combinedCards.isEmpty()) {
            this.states.turn = TurnState.TAKE_PLAY_CARD;
            return;
        }

        if (this.combinedCards.contains(touchedCard)) {
            this.combinedCards.remove(touchedCard);
            touchedCard.marked = false;
            this.combinableCards = getCardsToTake(playCardHeap);
        } else if (this.combinableCards.contains(touchedCard)) {
            this.combinedCards.add(touchedCard);
            touchedCard.marked = true;
            this.combinableCards = getCardsToTake(playCardHeap);

            int sum = this.cardCombinator.getSum(this.combinedCards);
            if (sum == this.playCard.value) {
                this.states.turn = TurnState.TAKE_COMBINED_CARDS;
                this.nextTurnState = TurnState.COMBINE_CARDS;
            }
        }
    }

    private void combineJackCards(List<Input.TouchEvent> touchEvents, User currentPlayer, List<CardHolder> players) {
        List<Card> playCardHeap = getPlayCardHeap(currentPlayer, players);
        if (this.playCard != null) {
            playCardHeap.add(this.playCard);
        }

        if (playCardHeap.isEmpty()) {
            if (hasTrick(players)) {
                this.states.turn = TurnState.SHOW_TRICK_BUTTON;
            } else {
                this.states.turn = TurnState.SHOW_END_TURN_BUTTON;
            }
            return;
        }

        Card touchedCard = getTouchedCard(touchEvents, playCardHeap);

        if (touchedCard == null) return;
        if (touchedCard == this.playCard && this.combinedCards.isEmpty()) {
            this.states.turn = TurnState.TAKE_PLAY_CARD;
            return;
        }

        List<Card> tableCards = getTableCards(players);
        if (!tableCards.isEmpty() && this.combinedCards.isEmpty() && !containsOpponentCard(this.combinableCards, players)) {
            if (!tableCards.contains(touchedCard)) return;

            this.combinedCards.add(touchedCard);
            this.states.turn = TurnState.TAKE_COMBINED_CARDS;
            this.nextTurnState = TurnState.COMBINE_JACK_CARDS;
            return;
        }

        if (this.combinedCards.contains(touchedCard)) {
            this.combinedCards.remove(touchedCard);
            touchedCard.marked = false;
            this.combinableCards = getCardsToTake(playCardHeap);
        } else if (this.combinableCards.contains(touchedCard)) {
            this.combinedCards.add(touchedCard);
            touchedCard.marked = true;
            this.combinableCards = getCardsToTake(playCardHeap);

            int sum = this.cardCombinator.getSum(this.combinedCards);
            if (sum == this.playCardValue) {
                this.states.turn = TurnState.TAKE_COMBINED_CARDS;
                this.nextTurnState = TurnState.COMBINE_JACK_CARDS;
            }
        }
    }

    private boolean containsOpponentCard(List<Card> cards, List<CardHolder> players) {
        for (CardHolder cardHolder : players) {
            if (cardHolder instanceof User) {
                User user = (User) cardHolder;
                int cardsNumber = user.boardCards.size();
                if (cardsNumber > 0 && cards.contains(user.boardCards.get(cardsNumber - 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasTrick(List<CardHolder> players) {
        List<Card> tableCards = getTableCards(players);
        return this.startTurnTableCardsNumber != 0 && tableCards.size() == 0;
    }

    private List<Card> getCardsToTake(List<Card> playCardHeap) {
        playCardHeap.removeAll(this.combinedCards);

        return this.cardCombinator.filterCombinableCards(playCardHeap, this.playCardValue -
                this.cardCombinator.getSum(this.combinedCards));
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

    private List<Card> getTableCards(List<CardHolder> players) {
        for (CardHolder player : players) {
            if (player instanceof Table) {
                return player.playCards;
            }
        }
        throw new RuntimeException("wtf??? How can I play without a table???");
    }
}
