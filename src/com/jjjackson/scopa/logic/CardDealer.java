package com.jjjackson.scopa.logic;

import android.graphics.Point;
import com.jjjackson.scopa.Assets;
import com.jjjackson.scopa.logic.deal.DealState;
import com.jjjackson.scopa.logic.domain.*;
import com.jjjackson.scopa.logic.util.PositionCalculator;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CardDealer {
    public final MovingCard movingCard = new MovingCard();
    private final List<CardHolder> players;
    private final Pack pack;
    private States states;
    private final CardMover cardMover = new CardMover();
    private int jackX;
    private int jackY;
    private DealState state = DealState.NONE;
    public boolean isCardMoving;
    private int currentPlayerIndex;

    public CardDealer(List<CardHolder> players, Pack pack, CardPosition dealerPosition, States states) {
        this.players = players;
        this.pack = pack;
        this.states = states;
        computePackCoordinates(pack, dealerPosition);
        calculatePackSpeed(pack);
    }

    private void computePackCoordinates(Pack pack, CardPosition dealerPosition) {
        switch (dealerPosition) {
            case BOTTOM:
                pack.displayPosition.x = 220;
                pack.displayPosition.y = 600;
                pack.hidePosition.x = 220;
                pack.hidePosition.y = 810;
                break;
            case LEFT:
                pack.displayPosition.x = 100;
                pack.displayPosition.y = 350;
                pack.hidePosition.x = -100;
                pack.hidePosition.y = 350;
                break;
            case TOP:
                pack.displayPosition.x = 220;
                pack.displayPosition.y = 100;
                pack.hidePosition.x = 220;
                pack.hidePosition.y = -100;
                break;
            case RIGHT:
                pack.displayPosition.x = 350;
                pack.displayPosition.y = 350;
                pack.hidePosition.x = 490;
                pack.hidePosition.y = 350;
                break;
        }
        pack.x = -100;
        pack.y = -100;
    }

    private void calculatePackSpeed(Pack pack) {
        double dist = calculateDistance(pack.hidePosition, pack.displayPosition);
        pack.speed = pack.step / dist;
    }

    public void deal(float deltaTime) {
        switch (this.state) {
            case NONE:
                movePack(deltaTime, this.pack.position, this.pack.displayPosition, false);
                break;
            case DEAL:
                dealCards(deltaTime);
                break;
            case JACK_OUT:
                replaceJackOut(deltaTime);
                break;
            case JACK_IN:
                replaceJackIn(deltaTime);
                break;
            case PACK_OUT:
                movePack(deltaTime, this.pack.position, this.pack.hidePosition, true);
                break;
        }
    }

    private void movePack(float deltaTime, Point position, Point destination, boolean isPackOut) {
        if (this.pack.progress == 0) {
            if (isPackOut) {
                for (CardHolder cardHolder : this.players) {
                    if (cardHolder.cardPosition == CardPosition.BOTTOM) {
                        cardHolder.playCards.get(1).value = 11;
                    }
                }
            }
            this.pack.position.x = !isPackOut ? this.pack.hidePosition.x : this.pack.displayPosition.x;
            this.pack.position.y = !isPackOut ? this.pack.hidePosition.y : this.pack.displayPosition.y;
        }
        if (this.pack.progress < 1) {
            updatePackCoordinates(deltaTime, position, destination);
        } else {
            if (isPackOut) {
                this.states.game = GameState.TURN;
            } else {
                this.state = DealState.DEAL;
            }
            this.pack.progress = 0;
            this.pack.position.x = isPackOut ? this.pack.hidePosition.x : this.pack.displayPosition.x;
            this.pack.position.y = isPackOut ? this.pack.hidePosition.y : this.pack.displayPosition.y;
        }
    }

    private void updatePackCoordinates(float deltaTime, Point position, Point destination) {
        this.pack.progress = this.pack.progress + this.pack.speed * deltaTime;
        this.pack.x = (int) (position.x + (destination.x - position.x) * this.pack.progress);
        this.pack.y = (int) (position.y + (destination.y - position.y) * this.pack.progress);
    }

    private void dealCards(float deltaTime) {
        if (!this.isCardMoving) {
            if (this.players.get(this.players.size() - 1).playCards.size() == 4) {
                if (isJackOnTable()) {
                    this.state = DealState.JACK_OUT;
                } else {
                    this.state = DealState.PACK_OUT;
                }
                return;
            }
            prepareCard();
            this.isCardMoving = true;
        } else {
            if (this.movingCard.progress < 1) {
                cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
            } else {
                finishMovement();
                getUser(this.currentPlayerIndex).playCards.add(this.movingCard.card);
                this.currentPlayerIndex = this.currentPlayerIndex == this.players.size() - 1 ? 0 : this.currentPlayerIndex + 1;
                this.isCardMoving = false;
            }
        }
    }

    private CardHolder getUser(int currentPlayerIndex) {
        return this.players.get(currentPlayerIndex);
    }

    private void replaceJackOut(float deltaTime) {
        if (!this.isCardMoving) {
            Card jack = getJackFromTable();
            prepareJackOut(jack);
            this.isCardMoving = true;
        } else {
            if (this.movingCard.progress < 1) {
                cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
            } else {
                finishMovement();
                this.state = DealState.JACK_IN;
                this.isCardMoving = false;
                backToPack(this.movingCard.card);
            }
        }
    }

    private void replaceJackIn(float deltaTime) {
        if (!this.isCardMoving) {
            prepareCard(true);
            this.isCardMoving = true;
        } else {
            if (this.movingCard.progress < 1) {
                cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
            } else {
                finishMovement();
                this.state = DealState.DEAL;
                this.isCardMoving = false;
                this.players.get(this.players.size() - 2).playCards.add(this.movingCard.card);
            }
        }
    }

    private boolean isJackOnTable() {
        for (Card card : getUser(this.players.size() - 2).playCards) {
            if (card.value == GameConstants.JACK_VALUE) {
                return true;
            }
        }
        return false;
    }

    public void finishMovement() {
        this.movingCard.card.position.x = this.movingCard.endX;
        this.movingCard.card.position.y = this.movingCard.endY;
        this.movingCard.progress = 0;
    }

    public void prepareCard(boolean jackReplacement) {
        CardHolder cardHolder = getUser(this.currentPlayerIndex);
        Point destination = calculateDestination(cardHolder);
        this.movingCard.card = getMovingCard(pack.cards);
        this.movingCard.endX = destination.x;
        this.movingCard.endY = destination.y;
        this.movingCard.card.position.x = this.pack.position.x;
        this.movingCard.card.position.y = this.pack.position.y;
        this.movingCard.startX = this.pack.position.x;
        this.movingCard.startY = this.pack.position.y;
        this.movingCard.progress = 0;
        if (jackReplacement) {
            this.movingCard.endX = this.jackX;
            this.movingCard.endY = this.jackY;
        }

        this.movingCard.showBack = cardHolder instanceof User && ((User) cardHolder).userType != UserType.PLAYER;
        double dist = this.cardMover.calculateDistance(this.movingCard);
        this.movingCard.speed = this.movingCard.step / dist;
    }

    public void prepareCard() {
        prepareCard(false);
    }

    double calculateDistance(Point position, Point destination) {
        int dx = destination.x - position.x;
        int dy = destination.y - position.y;
        return Math.abs(Math.sqrt(dx * dx + dy * dy));
    }

    private Card getMovingCard(List<Card> pack) {
        return pack.remove(0);
    }

    private Point calculateDestination(CardHolder player) {
        int cardNumber = player.playCards.size();
        Point destination = new Point();
        switch (player.cardPosition) {
            case BOTTOM:
                PositionCalculator.calcBottom(cardNumber, destination);
                break;
            case LEFT:
                PositionCalculator.calcLeft(cardNumber, destination);
                break;
            case TOP:
                PositionCalculator.calcTop(cardNumber, destination);
                break;
            case RIGHT:
                PositionCalculator.calcRight(cardNumber, destination);
                break;
            case CENTER:
                this.cardMover.changeCenterCardsPosition(player.playCards, true);
                PositionCalculator.calcCenter(cardNumber, destination);
                break;
        }
        return destination;
    }

    public void prepareJackOut(Card jack) {
        this.movingCard.card = jack;
        this.movingCard.showBack = false;
        this.movingCard.startX = jack.position.x;
        this.movingCard.startY = jack.position.y;
        this.movingCard.endX = this.pack.position.x;
        this.movingCard.endY = this.pack.position.y;
        this.jackX = jack.position.x;
        this.jackY = jack.position.y;
        double dist = this.cardMover.calculateDistance(this.movingCard);
        this.movingCard.speed = this.movingCard.step / dist;
    }

    private void backToPack(Card card) {
        this.pack.cards.add(getRandomPosition(10, 30), card);
    }

    private int getRandomPosition(int from, int to) {
        return to - new Random().nextInt(to - from);
    }

    private Card getJackFromTable() {
        CardHolder table = this.players.get(this.players.size() - 2);
        Iterator<Card> cards = table.playCards.iterator();
        while (cards.hasNext()) {
            Card card = cards.next();
            if (card.value == GameConstants.JACK_VALUE) {
                cards.remove();
                return card;
            }
        }
        return null;
    }
}