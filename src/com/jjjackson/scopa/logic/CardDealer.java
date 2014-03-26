package com.jjjackson.scopa.logic;

import android.graphics.Point;
import com.jjjackson.scopa.Assets;
import com.jjjackson.scopa.logic.deal.DealState;
import com.jjjackson.scopa.logic.domain.*;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CardDealer {
    public final MovingCard movingCard = new MovingCard();
    private final List<CardHolder> players;
    private final Pack pack;
    private final CardMover cardMover = new CardMover();
    private int cardWidth = Assets.cards.get("c2").getWidth();
    private int cardHeight = Assets.cards.get("c2").getHeight();
    private int jackX;
    private int jackY;
    private GameBoard gameBoard;
    private DealState state = DealState.NONE;
    public boolean isCardMoving;
    private int currentPlayerIndex;

    public CardDealer(List<CardHolder> players, Pack pack, CardPosition dealerPosition, GameBoard gameBoard) {
        this.players = players;
        this.pack = pack;
        this.gameBoard = gameBoard;
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
            this.pack.position.x = !isPackOut ? this.pack.hidePosition.x : this.pack.displayPosition.x;
            this.pack.position.y = !isPackOut ? this.pack.hidePosition.y : this.pack.displayPosition.y;
        }
        if (this.pack.progress < 1) {
            updatePackCoordinates(deltaTime, position, destination);
        } else {
            if (isPackOut) {
                this.gameBoard.state = GameState.TURN;
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
                cardMover.updateCoordinates(this.movingCard, deltaTime);
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
                cardMover.updateCoordinates(this.movingCard, deltaTime);
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
                cardMover.updateCoordinates(this.movingCard, deltaTime);
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
            if (card.value == 11) {
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
                calculateBottom(cardNumber, destination);
                break;
            case LEFT:
                calculateLeft(cardNumber, destination);
                break;
            case TOP:
                calculateTop(cardNumber, destination);
                break;
            case RIGHT:
                calculateRight(cardNumber, destination);
                break;
            case CENTER:
                calculateCenter(cardNumber, destination);
                break;
        }
        return destination;
    }

    void calculateBottom(int cardNumber, Point destination) {
        destination.x = 112 + cardNumber * (this.cardWidth + 20);
        destination.y = 800 - this.cardHeight;
    }

    void calculateLeft(int cardNumber, Point destination) {
        destination.x = 0;
        destination.y = 400 + cardNumber * 25;
    }

    void calculateTop(int cardNumber, Point destination) {
        destination.x = 25 + cardNumber * (this.cardWidth + 20);
        destination.y = 0;
    }

    void calculateRight(int cardNumber, Point destination) {
        destination.x = 480 - this.cardWidth;
        destination.y = 400 + cardNumber * 25;
    }

    void calculateCenter(int cardNumber, Point destination) {
        destination.x = 90 + cardNumber * (this.cardWidth + 4);
        destination.y = 225;
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
            if (card.value == 11) {
                cards.remove();
                return card;
            }
        }
        return null;
    }
}