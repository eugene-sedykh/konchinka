package com.jjjackson.scopa.logic.turn;

import com.jjjackson.scopa.logic.domain.Card;

import java.util.*;

public class CardCombinator {

    public List<Card> filterCombinableCards(List<Card> cards, int target) {
        Set<Card> combinableCards = new HashSet<>();

        Collections.sort(cards, new CardComparator());
        populateCombinableCards(cards.toArray(new Card[cards.size()]), 0, new Card[cards.size()], 0, target,
                combinableCards);

        return Arrays.asList(combinableCards.toArray(new Card[combinableCards.size()]));
    }

    private void populateCombinableCards(final Card[] data, int fromIndex,
                                      final Card[] stack, final int stacklen,
                                      final int target, Set<Card> combinableCards) {
        if (target == 0) {
            // exact match of our target. Success!
            Collections.addAll(combinableCards, Arrays.copyOf(stack, stacklen));
            return;
        }

        if (fromIndex < data.length && data[fromIndex].value > target) {
            // take advantage of sorted data.
            // we can skip all values that are too large.
            return;//fromIndex++;
        }

        while (fromIndex < data.length && data[fromIndex].value <= target) {
            // stop looping when we run out of data, or when we overflow our target.
            stack[stacklen] = data[fromIndex];
            populateCombinableCards(data, fromIndex + 1, stack, stacklen + 1, target - data[fromIndex].value,
                    combinableCards);
            fromIndex++;
        }
    }

    public List<List<Card>> getCombinations(List<Card> cards, int target) {
        List<List<Card>> combinations = new ArrayList<>();

        Collections.sort(cards, new CardComparator());
        populateCombinableCards(cards.toArray(new Card[cards.size()]), 0, new Card[cards.size()], 0, target,
                combinations);

        return combinations;
    }

    private void populateCombinableCards(final Card[] data, int fromIndex,
                                         final Card[] stack, final int stacklen,
                                         final int target, List<List<Card>> combinations) {
        if (target == 0) {
            // exact match of our target. Success!
            List<Card> combination = new ArrayList<>();
            Collections.addAll(combination, Arrays.copyOf(stack, stacklen));
            combinations.add(combination);
            return;
        }

        if (fromIndex < data.length && data[fromIndex].value > target) {
            // take advantage of sorted data.
            // we can skip all values that are too large.
            return;//fromIndex++;
        }

        while (fromIndex < data.length && data[fromIndex].value <= target) {
            // stop looping when we run out of data, or when we overflow our target.
            stack[stacklen] = data[fromIndex];
            populateCombinableCards(data, fromIndex + 1, stack, stacklen + 1, target - data[fromIndex].value,
                    combinations);
            fromIndex++;
        }
    }

    public int getSum(List<Card> cards) {
        int sum = 0;
        for (Card card : cards) {
            sum += card.value;
        }

        return sum;
    }

    private class CardComparator implements Comparator<Card> {

        @Override
        public int compare(Card lhs, Card rhs) {
            if (lhs.value == rhs.value) {
                return 0;
            }

            return lhs.value > rhs.value ? 1 : -1;
        }
    }
}
