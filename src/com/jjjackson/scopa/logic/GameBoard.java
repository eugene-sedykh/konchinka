package com.jjjackson.scopa.logic;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.domain.*;
import com.jjjackson.scopa.logic.turn.TurnMaker;

import java.util.*;

public class GameBoard {

    public final CardDealer cardDealer;
    public Pack pack = new Pack();
    public User user = new User(UserType.HUMAN, CardPosition.BOTTOM);
    public List<CardHolder> players = new ArrayList<>();
    public GameState state;
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
        this.state = GameState.NONE;
        this.cardDealer = new CardDealer(this.players, this.pack, CardPosition.BOTTOM, this);
        this.turnMaker = new TurnMaker();
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
        this.turnMaker.make(input, this.players, deltaTime);
    }
}
