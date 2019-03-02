package magicthegathering;
import java.util.HashMap;
import java.util.Map;

public class MagicCard {

    private int numCopies;
    private double cmc;
    private String name;
    private HashMap<Character, Integer> coloredManaPips;

    public MagicCard(int numCopies, double cmc, String name) {

        this.numCopies = numCopies;
        this.cmc = cmc;
        this.name = name;
        this.coloredManaPips = new HashMap<Character, Integer>();
        this.coloredManaPips.put('W', 0);
        this.coloredManaPips.put('U', 0);
        this.coloredManaPips.put('B', 0);
        this.coloredManaPips.put('R', 0);
        this.coloredManaPips.put('G', 0);
    }

    public String getCardName() {

        return this.name;
    }

    public int getNumCopies() {

        return this.numCopies;
    }

    public double getCmc(){
        return cmc;
    }

    public void setColoredManaPips(String coloredManaPips) {

        for(int i = 0; i < coloredManaPips.length(); i++) {
            char index = coloredManaPips.charAt(i);
            // ignore X costs in cards
            if(Character.isLetter(index) == true && index != 'X')
                addPip(index);
        }
        // multiply by number of copies
        for (Map.Entry<Character, Integer> entry : this.coloredManaPips.entrySet()) {
            if (entry.getValue() != 0) {
                // multiply pips by number of copies of the card
                this.coloredManaPips.put(entry.getKey(), entry.getValue() * this.numCopies);
            }
        }
    }
    private void addPip(char pip) {

        switch (pip) {
            case 'W': int whitePips = this.coloredManaPips.get('W');
            this.coloredManaPips.put('W', whitePips + 1);
            break;
            case 'U': int bluePips = this.coloredManaPips.get('U');
            this.coloredManaPips.put('U', bluePips + 1);
            break;
            case'B': int blackPips = this.coloredManaPips.get('B');
            this.coloredManaPips.put('B', blackPips + 1);
            break;
            case'R': int redPips = this.coloredManaPips.get('R');
            this.coloredManaPips.put('R', redPips + 1);
            break;
            case'G': int greenPips = this.coloredManaPips.get('G');
            this.coloredManaPips.put('G', greenPips + 1);
            break;
            default:
                System.out.println("Error: not a colored mana pip.");
                break;
        }
    }

    public HashMap<Character, Integer> getColoredPips() {
        return this.coloredManaPips;
    }
}
