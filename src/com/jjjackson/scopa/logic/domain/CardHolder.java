package com.jjjackson.scopa.logic.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class CardHolder {

    public List<Card> playCards = new ArrayList<>();
    public CardPosition cardPosition;
    public boolean isCurrent;
}
