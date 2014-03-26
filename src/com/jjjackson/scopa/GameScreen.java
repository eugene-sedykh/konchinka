package com.jjjackson.scopa;

import android.util.Log;
import com.jjjackson.framework.Game;
import com.jjjackson.framework.Graphics;
import com.jjjackson.framework.Pixmap;
import com.jjjackson.framework.Screen;
import com.jjjackson.scopa.logic.*;
import com.jjjackson.scopa.logic.domain.Card;
import com.jjjackson.scopa.logic.domain.CardHolder;
import com.jjjackson.scopa.logic.domain.MovingCard;
import com.jjjackson.scopa.logic.domain.User;

public class GameScreen extends Screen {

    public static final String BACK = "b1fv";
    private final GameBoard gameBoard;
    private boolean showFace;
    private Pixmap pixmap;

    public GameScreen(Game game) {
        super(game);
        gameBoard = new GameBoard();
    }

    @Override
    public void update(float deltaTime) {

        switch (this.gameBoard.state) {
            case NONE:
                this.gameBoard.deal(deltaTime);
                break;
            case TURN:
                this.gameBoard.makeTurn(this.game.getInput(), deltaTime);
                break;
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics graphics = this.game.getGraphics();

        graphics.clear(0);

        for (CardHolder player : this.gameBoard.players) {
            this.showFace = !(player instanceof User && ((User) player).userType != UserType.PLAYER);
            for (Card card : player.playCards) {
                this.pixmap = this.showFace ? Assets.cards.get(card.shortName) : Assets.cards.get(BACK);
                graphics.drawPixmap(pixmap, card.position.x, card.position.y);
            }
        }

        if (this.gameBoard.cardDealer.isCardMoving) {
            MovingCard movingCard = this.gameBoard.cardDealer.movingCard;
            this.pixmap = movingCard.showBack ? Assets.cards.get(BACK) : Assets.cards.get(movingCard.card.shortName);
            graphics.drawPixmap(this.pixmap, movingCard.card.position.x, movingCard.card.position.y);
        }
        if (this.gameBoard.state == GameState.NONE) {
            graphics.drawPixmap(Assets.cards.get(BACK), this.gameBoard.pack.x, this.gameBoard.pack.y);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
