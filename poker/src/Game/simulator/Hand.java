package Game.simulator;

import Game.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;


public class Hand {

	private List<Card> cards;
	private int[] kinds = new int[14];
	private int[] suits = new int[4];

	public Hand() {
		this.cards=new ArrayList<>();
	}

	public Hand(List<Card> cards) {
		this.cards = cards;
		Collections.sort(cards);
		cards.forEach(card -> {
			kinds[card.getRank().getRank()] += 1;
			suits[card.getSuit().getRank()] +=1;
		});
	}


	public boolean hasAces() {
		return cards.stream().anyMatch(Card::isAce);
	}


	private boolean isOfAKind(int nOfAKind) {
		return IntStream.range(0, kinds.length).anyMatch(i -> kinds[i] == nOfAKind);
	}

	/**
	 * All cards have the same suit (as the first card)
	 */
	public boolean isFlush() {
		return cards.stream().allMatch(card -> card.suit() == cards.get(0).suit());
	}

	public boolean isStraight() {
		for (int i = 0; i < cards.size() - 1; i++) {
			if (cards.get(i + 1).rank() != cards.get(i).rank() + 1) {
				return false;
			}
		}
		return true;
	}

	public boolean isStraightFlush() {
		return isStraight() && isFlush();
	}



	private int pairCount() {
		int pairCount = 0;
		for (int kind : kinds) {
			if (kind == 2) {
				pairCount++;
			}
		}
		return pairCount;
	}

	public boolean isFourOfAKind() {
		return isOfAKind(4);
	}

	public boolean isThreeOfAKind() {
		return isOfAKind(3);
	}

	public boolean isFullHouse() {
		return isOfAKind(3) && pairCount() == 1;
	}

	public boolean isTwoPair() {
		return pairCount() == 2;
	}

	public boolean isOnePair() {
		return pairCount() == 1;
	}
	
	public void addCard(Card c){
		int pos = Collections.binarySearch(cards, c);
		if (pos < 0) {
			cards.add(-pos-1, c);
			kinds[c.getRank().getRank()] += 1;
			suits[c.getSuit().getRank()] +=1;
		}
	}

	public void removeCard(Card c){
		boolean tmp = cards.remove(c);
		if(tmp) {
			kinds[c.getRank().getRank()] -= 1;
			suits[c.getSuit().getRank()] -= 1;
		}
	}

	public List<Card> getCards() {
		return this.cards;
	}

	@Override
	public String toString() {
		return cards.stream().map(Card::toString).collect(joining(" "));
	}

}
