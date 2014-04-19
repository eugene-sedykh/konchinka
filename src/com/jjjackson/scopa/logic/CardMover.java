package com.jjjackson.scopa.logic;

import android.util.Log;
import com.jjjackson.scopa.logic.domain.MovingCard;

public class CardMover {

    public void updateCoordinatesAndDegree(MovingCard movingCard, float deltaTime) {
        movingCard.progress = movingCard.progress + movingCard.speed * deltaTime;
        movingCard.card.position.x = (int) (movingCard.startX + (movingCard.endX - movingCard.startX) * movingCard.progress);
        movingCard.card.position.y = (int) (movingCard.startY + (movingCard.endY - movingCard.startY) * movingCard.progress);
        movingCard.card.degree = (float) (movingCard.startDegree + (movingCard.endDegree - movingCard.startDegree) * movingCard.progress);
        Log.i("1", "degree: " + movingCard.card.degree);
    }

    public double calculateDistance(MovingCard movingCard) {
        int dx = movingCard.startX - movingCard.endX;
        int dy = movingCard.startY - movingCard.endY;
        return Math.abs(Math.sqrt(dx * dx + dy * dy));
    }
}