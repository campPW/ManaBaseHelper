package magicthegathering;
import java.util.ArrayList;

public class MagicDeck {
    private ArrayList<MagicCard> cards = new ArrayList<MagicCard>();
    private int deckSize;
    private double averageCMC;
    private String deckFormat;
    public MagicDeck(ArrayList<MagicCard> magicCards) {
        this.cards = magicCards;
        this.setDeckSize();
        this.setAverageCMC();
        this.setDeckFormat(deckSize);
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
        String avgStr = Double.toString(average).substring(0,3);
        this.averageCMC = Double.parseDouble(avgStr);
    }
    private void setDeckFormat(int deckSize){
        if(deckSize >= 50)
            this.deckFormat = "Commander";
        else if(deckSize >= 30 && deckSize <= 45)
            this.deckFormat = "Constructed";
        else
            this.deckFormat = "Limited";
        }
}

