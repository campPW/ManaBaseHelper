package magicthegathering;
import magicthegathering.MagicCard;

import java.util.ArrayList;

// TODO create attribute for deck type (aggro, control, etc.)
public class MagicDeck {
    private ArrayList<MagicCard> cards = new ArrayList<MagicCard>();
    private int deckSize;
    private double averageCMC;

    public MagicDeck(ArrayList<MagicCard> magicCards) {
        this.cards = magicCards;
        this.setDeckSize();
        this.setAverageCMC();
    }
    public ArrayList<MagicCard> getCards() {
        return this.cards;
    }
    public double getAverageCMC(){
        return this.averageCMC;
    }
    public int getDeckSize(){
        return this.deckSize;
    }
    private void setDeckSize() {
        int size = 0;
        for(MagicCard mc : this.cards){
            size += mc.getNumCopies();
        }
        this.deckSize = size;
    }
    private void setAverageCMC() {
        double totalNumPips = 0, average = 0;
        for(MagicCard mc : this.cards){
            totalNumPips += mc.getNumCopies() * mc.getCmc();
        }
        average = totalNumPips / this.deckSize;
        // TODO figure out why sometimes getting an out of bound exception
        String avgStr = Double.toString(average).substring(0,3);
        this.averageCMC = Double.parseDouble(avgStr);
    }

}
