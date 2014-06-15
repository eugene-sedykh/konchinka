package com.jjjackson.scopa.logic;

import com.jjjackson.scopa.logic.domain.Card;
import com.jjjackson.scopa.logic.domain.MovingCard;

import java.util.List;

public class CardMover {

    public void updateCoordinatesAndDegree(MovingCard movingCard, float deltaTime) {
        movingCard.progress = movingCard.progress + movingCard.speed * deltaTime;

        int nextX = (int) (movingCard.startX + (movingCard.endX - movingCard.startX) * movingCard.progress);
        if (movingCard.endX - movingCard.startX > 0 && nextX <= movingCard.endX) {
            movingCard.card.position.x = nextX;
        } else if (movingCard.endX - movingCard.startX < 0 && nextX >= movingCard.endX) {
            movingCard.card.position.x = nextX;
        }

        int nextY = (int) (movingCard.startY + (movingCard.endY - movingCard.startY) * movingCard.progress);
        if (movingCard.endY - movingCard.startY > 0 && nextY <= movingCard.endY) {
            movingCard.card.position.y = nextY;
        } else if (movingCard.endY - movingCard.startY < 0 && nextY >= movingCard.endY) {
            movingCard.card.position.y = nextY;
        }

        float nextDegree = (float) (movingCard.startDegree + (movingCard.endDegree - movingCard.startDegree) * movingCard.progress);
        if (movingCard.endDegree - movingCard.startDegree > 0 && nextDegree <= movingCard.endDegree) {
            movingCard.card.degree = nextDegree;
        } else if (movingCard.endDegree - movingCard.startDegree < 0 && nextDegree >= movingCard.endDegree) {
            movingCard.card.degree = nextDegree;
        }
//        Log.i("1", "degree: " + movingCard.card.degree);
    }

    public double calculateDistance(MovingCard movingCard) {
        int dx = movingCard.startX - movingCard.endX;
        int dy = movingCard.startY - movingCard.endY;
        return Math.abs(Math.sqrt(dx * dx + dy * dy));
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder) {
        changeCenterCardsPosition(cards, addPlaceholder, false);
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder, boolean shiftVertically) {
        int size = cards.size();
        int rows = size / 4;
        int row = 0;
        for (int i = 0; i < size; i++) {
            int indexInRow = i % 4;
            if (indexInRow == 0) {
                row++;
            }
            Card card = cards.get(i);

            card.position.x = 240 - (GameConstants.CARD_WIDTH + 4) * getRowSize(addPlaceholder, size, row, rows) / 2 +
                    indexInRow * (GameConstants.CARD_WIDTH + 4);
            card.position.y = 225 + ((row - 1) * (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP));
            if (shiftVertically) {
                card.position.y += (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP) / 2;
            }
        }
    }

    private int getRowSize(boolean addPlaceholder, int total, int row, int rows) {
        int totalInRow = total % 4;
        if (row < rows || totalInRow == 0) {
            return 4;
        }

        return totalInRow + (addPlaceholder ? 1 : 0);
    }
}