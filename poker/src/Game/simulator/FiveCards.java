package Game.simulator;

import Game.Card;
import Game.utils.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;


public class FiveCards {

	private List<Card> cards;
	private int[] kinds = new int[15];

	public FiveCards() {
		this.cards=new ArrayList<>();
	}

	public FiveCards(List<Card> cards) {
		this.cards = cards;
		Collections.sort(cards);
		cards.forEach(card -> {
			kinds[card.getRank().getRank()] += 1;
		});
	}


	public boolean hasAces() {
		return cards.stream().anyMatch(Card::isAce);
	}
	public boolean isOfAKind(int nOfAKind) {
		return IntStream.range(0, kinds.length).anyMatch(i -> kinds[i] == nOfAKind);
	}
	public int pairCount() {
		int pairCount = 0;
		for (int kind : kinds) {
			if (kind == 2) {
				pairCount++;
			}
		}
		return pairCount;
	}



	public boolean isFlush() {
		return cards.stream().allMatch(card -> card.getSuit() == cards.get(0).getSuit());
	}

	public boolean isStraightFive() {
		boolean straight_1=true;
		for (int i = 0; i < cards.size() - 1; i++) {
			if (cards.get(i + 1).getRank().getRank() != cards.get(i).getRank().getRank() + 1) {
				straight_1= false;
				break;
			}
		}
		if (straight_1) return true;
		return cards.get(0).getRank()== Rank.DEUCE && cards.get(1).getRank()== Rank.THREE && cards.get(2).getRank()== Rank.FOUR && cards.get(3).getRank()== Rank.FIVE && cards.get(4).getRank()== Rank.ACE;
	}

	public boolean addCard(Card c){
		cards.add(c);
		kinds[c.getRank().getRank()] += 1;
		Collections.sort(cards);
		return true;
	}

	public void removeCard(Card c){
		boolean tmp = cards.remove(c);
		if(tmp) {
			kinds[c.getRank().getRank()] -= 1;
		}
	}
	public Card removeCard(int i){
		Card c = cards.remove(i);
		kinds[c.getRank().getRank()] -= 1;
		return c;
	}

	public List<Card> getCards() {
		return this.cards;
	}

	@Override
	public String toString() {
		return cards.stream().map(Card::toString).collect(joining(" "));
	}

	public void clear(){
		cards.clear();
		for(int i=0;i<kinds.length;i++) kinds[i]=0;
	}

	public void discardAndDrawRandomlessly(int nbCards,ArrayList<Card> deck){
		if(nbCards==0 || deck==null ||deck.isEmpty()) return;
		Random random=new Random();
		for (int i=0;i<nbCards;i++){
			deck.add(removeCard(random.nextInt(cards.size())));
		}
		for (int i=0;i<nbCards;i++){
			addCard(deck.remove(0));
		}
	}

	public int getHighestRank(){
		return cards.get(cards.size()-1).getRank().getRank();
	}

}
