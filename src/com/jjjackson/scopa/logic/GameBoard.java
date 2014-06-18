package com.jjjackson.scopa.logic;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.domain.*;
import com.jjjackson.scopa.logic.turn.PlayerTurnMaker;
import com.jjjackson.scopa.logic.turn.TurnMaker;
import com.jjjackson.scopa.logic.turn.TurnMakerFactory;

import java.util.*;

public class GameBoard {

    public final CardDealer cardDealer;
    public Pack pack = new Pack();
    public User currentPlayer;
    public List<CardHolder> players = new ArrayList<>();
    public States states;
    private TurnMakerFactory turnMakerFactory;
    private TurnMaker turnMaker;

    public GameBoard() {
        fillPack();
        Collections.shuffle(this.pack.cards);
        this.players.add(new User(UserType.COMPUTER, CardPosition.LEFT));
        this.players.add(new User(UserType.COMPUTER, CardPosition.TOP));
        this.players.add(new User(UserType.COMPUTER, CardPosition.RIGHT));
        this.players.add(new Table());
        User user = new User(UserType.PLAYER, CardPosition.BOTTOM);
        user.isCurrent = true;
        this.players.add(user);
        this.currentPlayer = user;
        this.states = new States();
        this.cardDealer = new CardDealer(this.players, this.pack, CardPosition.BOTTOM, this.states);
        this.turnMakerFactory = new TurnMakerFactory(this.states);
        this.turnMaker = this.turnMakerFactory.getTurnMaker(this.currentPlayer.userType);
    }

    private void fillPack() {
        for (int i = 1; i < 14; i++) {
            this.pack.cards.add(new Card(CardSuit.CLUBS, i));
            this.pack.cards.add(new Card(CardSuit.DIAMONDS, i));
            this.pack.cards.add(new Card(CardSuit.HEARTS, i));
            this.pack.cards.add(new Card(CardSuit.SPADES, i));
        }
    }

    public void deal(float deltaTime) {
        this.cardDealer.deal(deltaTime);
    }

    public void makeTurn(Input input, float deltaTime) {
        this.turnMaker.make(input, this.currentPlayer, this.players, deltaTime);
    }

    public void switchPlayer() {
        this.currentPlayer = getNextPlayer();
        this.turnMaker = this.turnMakerFactory.getTurnMaker(this.currentPlayer.userType);
        this.states.game = GameState.TURN;
    }

    private User getNextPlayer() {
        int playerIndex = 0;
        while (true) {
            if (this.players.get(playerIndex) == this.currentPlayer) {
                if (playerIndex == this.players.size() - 1) {
                    playerIndex = 0;
                    return getUser(playerIndex);
                } else {
                    playerIndex++;
                    return getUser(playerIndex);
                }

            }
            playerIndex++;
        }
    }

    private User getUser(int playerIndex) {
        return (this.players.get(playerIndex) instanceof User) ? (User) this.players.get(playerIndex) :
                (User) this.players.get(playerIndex + 1);
    }
}
