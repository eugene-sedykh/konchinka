package com.jjjackson.scopa.logic.turn;

import com.jjjackson.framework.Input;
import com.jjjackson.scopa.logic.GameConstants;
import com.jjjackson.scopa.logic.States;
import com.jjjackson.scopa.logic.domain.*;

import java.util.*;

public class ComputerTurnMaker extends TurnMaker {

    private List<String> valuableCards = Arrays.asList("c1", "d1", "h1", "s1", "d10", "s2");

    public ComputerTurnMaker(States states) {
        super(states);
    }

    @Override
    public void make(Input input, User currentPlayer, Table table, List<CardHolder> players, float deltaTime) {
        switch (this.states.turn) {
            case WAIT:
                if (this.movingCard.progress == -1) {
                    Card playCard = choosePlayCard(currentPlayer, table, players);
                    initCardMovement(playCard, GameConstants.PLAY_CARD_X, GameConstants.PLAY_CARD_Y);
                } else if (this.movingCard.progress < 1) {
                    this.cardMover.updateCoordinatesAndDegree(this.movingCard, deltaTime);
                } else {
                    finishMovingCard();
                    this.states.turn = TurnState.COMBINE_CARDS;
                }
                break;

        }
    }

    private Card choosePlayCard(User currentPlayer, Table table, List<CardHolder> players) {
        List<Card> playCardHeap = getPlayCardHeap(currentPlayer, players);
        CardCombination bestCombination = calculateAndChooseCombination(playCardHeap, currentPlayer.playCards, table);
        this.playCard = bestCombination.playerCard;
        this.states.turn = TurnState.MOVE_PLAY_CARD;
        if (bestCombination.combination.isEmpty()) {
            this.nextTurnState = TurnState.PLAY_CARD_TO_TABLE;
        } else {
            this.nextTurnState = TurnState.TAKE_COMBINED_CARDS;
            this.combinableCards = bestCombination.combination;
        }
        return null;
    }

    private CardCombination calculateAndChooseCombination(List<Card> cardsHeap, List<Card> playerCards, Table table) {
        Map<Card, List<List<Card>>> cardCombinations = new HashMap<>();
        for (Card playerCard : playerCards) {
            List<List<Card>> combinations = this.cardCombinator.getCombinations(cardsHeap, playerCard.value);
            cardCombinations.put(playerCard, combinations);
        }

        if (cardCombinations.isEmpty()) {
            return getEmptyCombination(playerCards);
        }

        return chooseCombination(getJack(playerCards), cardCombinations, table);
    }

    private Card getJack(List<Card> playerCards) {
        for (Card card : playerCards) {
            if (card.value == GameConstants.JACK_VALUE) {
                return card;
            }
        }
        return null;
    }

    private CardCombination getEmptyCombination(List<Card> playerCards) {
        CardCombination emptyCombination = new CardCombination();
        emptyCombination.playerCard = getLowestCard(playerCards);
        emptyCombination.combination = Collections.EMPTY_LIST;
        return emptyCombination;
    }

    private Card getLowestCard(List<Card> playerCards) {
        Collections.sort(playerCards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                if (valuableCards.contains(lhs.shortName)) {
                    return (rhs.value != GameConstants.JACK_VALUE) ? 1 : -1;
                }

                if (valuableCards.contains(rhs.shortName)) {
                    return (lhs.value != GameConstants.JACK_VALUE) ? -1 : 1;
                }

                if (lhs.value == rhs.value) {
                    return 0;
                }

                return lhs.value > rhs.value ? 1 : -1;
            }
        });

        return playerCards.get(0);
    }

    private CardCombination chooseCombination(Card jack, Map<Card, List<List<Card>>> cardCombinations, Table table) {
        Map<Card, List<List<Card>>> combinationsWithTrick = getCombinationsWithTrick(cardCombinations, table);

        if (!combinationsWithTrick.isEmpty()) {
            return chooseCombinationWithTrick(combinationsWithTrick);
        }

        if (jack != null && isValuableCardPresent(table.playCards)) {
            return buildJackCombination(jack, cardCombinations, table);
        }

        Map<Card, List<List<Card>>> combinationsWithValuableCards = getCombinationsWithValuableCards(cardCombinations);
        if (!combinationsWithValuableCards.isEmpty()) {
            return chooseCombinationWithValuableCards(combinationsWithValuableCards);
        }

        return chooseCombinationWithMaxCards(cardCombinations);
    }

    private CardCombination buildJackCombination(Card jack, Map<Card, List<List<Card>>> cardCombinations, Table table) {
        CardCombination cardCombination = new CardCombination();
        cardCombination.playerCard = jack;

        List<List<Card>> combinations = cardCombinations.get(jack);
        if (combinations == null) {
            cardCombination.combination = table.playCards;
            return cardCombination;
        }



        return null;
    }

    private Map<Card, List<List<Card>>> getCombinationsWithValuableCards(Map<Card, List<List<Card>>> cardCombinations) {
        Map<Card, List<List<Card>>> combinations = new HashMap<>();

        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            List<List<Card>> combinationsWithValuableCards = new ArrayList<>();
            for (List<Card> combination : entry.getValue()) {
                for (Card card : combination) {
                    if (this.valuableCards.contains(card.shortName)) {
                        combinationsWithValuableCards.add(combination);
                        break;
                    }
                }
            }

            if (!combinationsWithValuableCards.isEmpty()) {
                combinations.put(entry.getKey(), combinationsWithValuableCards);
            }
        }

        return combinations;
    }

    private CardCombination chooseCombinationWithValuableCards(Map<Card, List<List<Card>>> cardCombinations) {
        int maxValuableNumber = 0;
        CardCombination cardCombination = new CardCombination();

        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            for (List<Card> combination : entry.getValue()) {
                int valuableNumber = 0;

                for (Card card : combination) {
                    if (this.valuableCards.contains(card.shortName)) {
                        valuableNumber++;
                    }
                }

                if (valuableNumber > maxValuableNumber) {
                    cardCombination.playerCard = entry.getKey();
                    cardCombination.combination = combination;
                }
            }
        }

        return cardCombination;
    }

    private CardCombination chooseCombinationWithMaxCards(Map<Card, List<List<Card>>> cardCombinations) {
        CardCombination cardCombination = new CardCombination();
        cardCombination.combination = new ArrayList<>();

        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            for (List<Card> combination : entry.getValue()) {
                if (combination.size() > cardCombination.combination.size()) {
                    cardCombination.combination = combination;
                    cardCombination.playerCard = entry.getKey();
                }
            }
        }

        return cardCombination;
    }

    private boolean isValuableCardPresent(List<Card> cards) {
        for (Card card : cards) {
            if (this.valuableCards.contains(card.shortName)) {
                return true;
            }
        }
        return false;
    }
}
