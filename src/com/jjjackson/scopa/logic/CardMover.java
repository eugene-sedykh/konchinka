package com.jjjackson.scopa.logic;

import com.jjjackson.scopa.logic.domain.Card;
import com.jjjackson.scopa.logic.domain.MovingCard;

import java.util.List;

public class CardMover {

    public void updateCoordinatesAndDegree(MovingCard movingCard, float deltaTime) {
        movingCard.progress = movingCard.progress + movingCard.speed * deltaTime;
        movingCard.card.position.x = (int) (movingCard.startX + (movingCard.endX - movingCard.startX) * movingCard.progress);
        movingCard.card.position.y = (int) (movingCard.startY + (movingCard.endY - movingCard.startY) * movingCard.progress);
        movingCard.card.degree = (float) (movingCard.startDegree + (movingCard.endDegree - movingCard.startDegree) * movingCard.progress);
//        Log.i("1", "degree: " + movingCard.card.degree);
    }

    public double calculateDistance(MovingCard movingCard) {
        int dx = movingCard.startX - movingCard.endX;
        int dy = movingCard.startY - movingCard.endY;
        return Math.abs(Math.sqrt(dx * dx + dy * dy));
    }

    public void recalculateCenterCardsPosition(List<Card> cards, boolean addPlaceholder) {
        int size = cards.size();
        for (int i = 0; i < size; i++) {
            Card card = cards.get(i);
            card.position.x = 240 - (GameConstants.CARD_WIDTH + 4) * (size + (addPlaceholder ? 1 : 0)) / 2 +
                    i * (GameConstants.CARD_WIDTH + 4);
        }
    }
}