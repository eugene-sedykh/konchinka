package com.jjjackson.scopa;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import com.jjjackson.framework.Game;
import com.jjjackson.framework.Graphics;
import com.jjjackson.framework.Pixmap;
import com.jjjackson.framework.Screen;
import com.jjjackson.scopa.logic.*;
import com.jjjackson.scopa.logic.domain.*;
import com.jjjackson.scopa.logic.turn.TurnState;

import java.util.Collections;
import java.util.List;

public class GameScreen extends Screen {

    public static final String BACK = "b1fv";
    private final GameBoard gameBoard;
    private boolean showFace;
    private Pixmap pixmap;
    private States states;
    private int markedColor;
    private Paint textPaint;
    private Rect textRect = new Rect();

    public GameScreen(Game game) {
        super(game);
        this.gameBoard = new GameBoard();
        this.states = this.gameBoard.states;
        this.markedColor = Color.parseColor("#80888888");

        initTextPaint();
    }

    private void initTextPaint() {
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(20);
    }

    @Override
    public void update(float deltaTime) {

        switch (this.states.game) {
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
                drawCard(graphics, card);
            }

            if (player instanceof User && !((User) player).tricks.isEmpty()) {
                drawTrick(graphics, player.cardPosition);
            }
        }

        for (CardHolder player : this.gameBoard.players) {
            if (!(player instanceof User) || ((User) player).userType == UserType.PLAYER ||
                    ((User) player).boardCards.size() == 0) continue;
            drawCard(graphics, ((User)player).boardCards.get(((User)player).boardCards.size() - 1));
        }

        if (this.states.isFaded) {
            graphics.drawRect( 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT, this.markedColor);
        }
        if (this.states.isSortDoneButtonShown) {
            graphics.drawRect(GameConstants.SORT_BUTTON_X, GameConstants.SORT_BUTTON_Y, GameConstants.BUTTON_WIDTH,
                    GameConstants.BUTTON_HEIGHT, Color.BLACK);
        }

        for (Card card : getPlayerBoardCards(this.gameBoard.players)) {
            drawCard(graphics, card);
        }

        if (this.gameBoard.cardDealer.isCardMoving) {
            MovingCard movingCard = this.gameBoard.cardDealer.movingCard;
            this.pixmap = movingCard.showBack ? Assets.cards.get(BACK) : Assets.cards.get(movingCard.card.shortName);
            graphics.drawPixmap(this.pixmap, movingCard.card.position.x, movingCard.card.position.y, movingCard.card.degree);
        }

        if (Assets.turnMovingCard.progress != -1) {
            MovingCard movingCard = Assets.turnMovingCard;
            this.pixmap = Assets.cards.get(movingCard.card.shortName);
            graphics.drawPixmap(this.pixmap, movingCard.card.position.x, movingCard.card.position.y, movingCard.card.degree);
        }

        if (this.states.game == GameState.NONE) {
            graphics.drawPixmap(Assets.cards.get(BACK), this.gameBoard.pack.x, this.gameBoard.pack.y, 0);
        }

        drawButtons(graphics);
    }

    private void drawTrick(Graphics graphics, CardPosition cardPosition) {
        int x, y;
        switch (cardPosition) {
            case BOTTOM:
                x = GameConstants.BOTTOM_TRICK_X;
                y = GameConstants.BOTTOM_TRICK_Y;
                break;
            case LEFT:
                x = GameConstants.LEFT_TRICK_X;
                y = GameConstants.LEFT_TRICK_Y;
                break;
            case TOP:
                x = GameConstants.TOP_TRICK_X;
                y = GameConstants.TOP_TRICK_Y;
                break;
            default:
                x = GameConstants.RIGHT_TRICK_X;
                y = GameConstants.RIGHT_TRICK_Y;
                break;
        }

        graphics.drawPixmap(Assets.cards.get(BACK), x, y, 0);
    }

    private List<Card> getPlayerBoardCards(List<CardHolder> players) {
        for (CardHolder player : players) {
            if (player instanceof User && ((User) player).userType == UserType.PLAYER) {
                return ((User) player).boardCards;
            }
        }
        return Collections.emptyList();
    }

    private void drawButtons(Graphics graphics) {
        if (this.states.turn == TurnState.SHOW_TRICK_BUTTON || this.states.turn == TurnState.SHOW_END_TURN_BUTTON) {
            drawButton(graphics, "Sort", GameConstants.SORT_BUTTON_X, GameConstants.SORT_BUTTON_Y);
            if (this.states.turn == TurnState.SHOW_TRICK_BUTTON) {
                drawButton(graphics, "Trick", GameConstants.END_BUTTON_X, GameConstants.END_BUTTON_Y);
            } else {
                drawButton(graphics, "Done", GameConstants.END_BUTTON_X, GameConstants.END_BUTTON_Y);
            }
        }
        if (this.states.isSortDoneButtonShown) {
            drawButton(graphics, "Done", GameConstants.SORT_BUTTON_X, GameConstants.SORT_BUTTON_Y);
        }
    }

    private void drawButton(Graphics graphics, String text, int buttonX, int buttonY) {
        graphics.drawRect(buttonX, buttonY, GameConstants.BUTTON_WIDTH, GameConstants.BUTTON_HEIGHT, Color.WHITE, Paint.Style.STROKE);

        this.textPaint.getTextBounds(text, 0, text.length(), this.textRect);
        int textX = buttonX + (GameConstants.BUTTON_WIDTH - this.textRect.width()) / 2;
        int textY = buttonY + (GameConstants.BUTTON_HEIGHT - this.textRect.height()) / 2;

        graphics.drawText(text, textX, textY + this.textRect.height(), this.textPaint);
    }

    private void drawCard(Graphics graphics, Card card) {
        this.pixmap = this.showFace ? Assets.cards.get(card.shortName) : Assets.cards.get(BACK);
        graphics.drawPixmap(this.pixmap, card.position.x, card.position.y, card.degree);
        if (card.marked) {
            graphics.drawRect(card.position.x, card.position.y, GameConstants.CARD_WIDTH, GameConstants.CARD_HEIGHT,
                    this.markedColor);
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
