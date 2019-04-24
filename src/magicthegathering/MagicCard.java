package magicthegathering;
import java.util.HashMap;
import java.util.Map;

public class MagicCard {
    private int numCopies;
    private double cmc;
    private String name;
    private HashMap<Character, Integer> manaPips;

    public MagicCard(int numCopies, double cmc, String name) {
        this.numCopies = numCopies;
        this.cmc = cmc;
        this.name = name;
        this.manaPips = new HashMap<Character, Integer>();
        this.manaPips.put('W', 0);
        this.manaPips.put('U', 0);
        this.manaPips.put('B', 0);
        this.manaPips.put('R', 0);
        this.manaPips.put('G', 0);
    }
    public String getCardName() { return this.name; }

    public int getNumCopies() { return this.numCopies; }

    public double getCmc(){
        return cmc;
    }

    public void setColoredManaPips(String coloredManaPips) {
        for(int i = 0; i < coloredManaPips.length(); i++) {
            char index = coloredManaPips.charAt(i);
            // ignore X costs in cards
            if(Character.isLetter(index) && index != 'X')
                addPip(index);
        }
        // multiply by number of copies
        for (Map.Entry<Character, Integer> entry : this.manaPips.entrySet()) {
            if (entry.getValue() != 0) {
                // multiply pips by number of copies of the card
                this.manaPips.put(entry.getKey(), entry.getValue() * this.numCopies);
            }
        }
    }
    private void addPip(char pip) {
        switch (pip) {
            case 'W': int whitePips = this.manaPips.get('W');
            this.manaPips.put('W', whitePips + 1);
            break;
            case 'U': int bluePips = this.manaPips.get('U');
            this.manaPips.put('U', bluePips + 1);
            break;
            case'B': int blackPips = this.manaPips.get('B');
            this.manaPips.put('B', blackPips + 1);
            break;
            case'R': int redPips = this.manaPips.get('R');
            this.manaPips.put('R', redPips + 1);
            break;
            case'G': int greenPips = this.manaPips.get('G');
            this.manaPips.put('G', greenPips + 1);
            break;
        }
    }
    public HashMap<Character, Integer> getColoredPips() {
        return this.manaPips;
    }
}
