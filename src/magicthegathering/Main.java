package magicthegathering;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.json.*;

// TODO: Handle Split cards
// TODO: Handle cards with hybrid mana
public class Main extends Application {
    private final String scryfallAPI = "https://api.scryfall.com/cards/named?fuzzy=";
    private Stage stage;
    private Button openFileBtn = new Button("Open Decklist");
    private String fileName;
    private  ArrayList<MagicCard> cardArrayList = new ArrayList<MagicCard>();
    private final HashMap<Character,Integer> landsNeeded = new HashMap<>();
    private MagicDeck deck;
    private GridPane gridPane = new GridPane();
    private Label avgCMCLbl = new Label("Average CMC: ");
    private Label whiteLbl = new Label("White Mana: ");
    private Label blueLbl = new Label("Blue Mana: ");
    private Label blackLbl = new Label("Black Mana: ");
    private Label redLbl = new Label("Red Mana: ");
    private Label greenLbl = new Label("Green Mana: ");
    private Label recLbl = new Label("Recommended Amount");
    private int useCount = 0;
    private Label statusLbl = new Label("Ready");
    private ProgressBar progressBar = new ProgressBar();
    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Mana Base Helper");
        gridPane.setVgap(25);
        gridPane.add(recLbl, 0, 1);
        gridPane.add(openFileBtn, 0, 8);
        gridPane.add(statusLbl, 3,8);
        gridPane.add(progressBar, 2, 8);

        gridPane.add(whiteLbl, 0, 2);
        gridPane.add(blueLbl, 0, 3);
        gridPane.add(blackLbl, 0, 4);
        gridPane.add(redLbl, 0, 5);
        gridPane.add(greenLbl,0,6);
        gridPane.add(avgCMCLbl, 0, 7);

        whiteLbl.setPadding(new Insets(10,10,10,10));
        blueLbl.setPadding(new Insets(10,10,10,10));
        blackLbl.setPadding(new Insets(10,10,10,10));
        redLbl.setPadding(new Insets(10,10,10,10));
        greenLbl.setPadding(new Insets(10,10,10,10));
        avgCMCLbl.setPadding(new Insets(10,10,10,10));
        recLbl.setPadding(new Insets(10,10,10,10));
        statusLbl.setPadding(new Insets(10,10,10,10));
        openFileBtn.setPadding(new Insets(10,10,10,10));

        recLbl.setUnderline(true);
        recLbl.setFont(Font.font("Courier New", 20));
        avgCMCLbl.setFont(Font.font("Courier New", 15));
        progressBar.setVisible(false);
        openFileBtn.setTooltip(new Tooltip("Open decklist to receive mana suggestion"));

        openFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(useCount == 0){
                    process();
                }
                else {
                    reset();
                }
            }
        });
        Scene scene = new Scene(gridPane, 440, 525);
        stage.setScene(scene);
        stage.show();
    }
    private void process() {
        this.openFile();
        openFileBtn.setDisable(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                useCount++;
                readDeckFile();
                deck = new MagicDeck(cardArrayList);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        avgCMCLbl.setText("Average CMC: " + deck.getAverageCMC());
                        statusLbl.setText("done");
                        openFileBtn.setDisable(false);
                        getColors();
                    }
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        task.setOnScheduled(scheduleEvent -> progressBar.setVisible(true));
        task.setOnRunning(runEvent -> statusLbl.setText("reading..."));
        task.setOnSucceeded(finishEvent -> progressBar.setProgress(1.0));
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
        fileChooser.getExtensionFilters().addAll(filter);
        File file = fileChooser.showOpenDialog(stage);
        this.fileName = file.getPath();
    }
    private void readDeckFile() {
        BufferedReader br = null;
        FileReader fr = null;

        try{
            fr = new FileReader(this.fileName);
            br = new BufferedReader(fr);
            String line;

            while((line = br.readLine()) != null && line.isEmpty() == false){
                this.parseNameAndAmount(line);
            }
        }
        catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
        finally {
            try {
                fr.close();
                br.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void createCard(int amountOfCard, String nameOfCard) {
        // connect to Scryfall API and get card data in JSON format
        try {
            URL obj = new URL(scryfallAPI + nameOfCard);
            HttpsURLConnection connection = (HttpsURLConnection)obj.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder jsonCardInfo = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = in.readLine()) != null){
                jsonCardInfo.append(line);
            }
            // create json object to parse out cmc & colored mana pips
            JSONObject jsonObject = new JSONObject(jsonCardInfo.toString());
            double cmc = (double)jsonObject.get("cmc");
            // check if card is a land and return if true
            if(cmc == 0)
                return;
            String manaPips = (String)jsonObject.get("mana_cost");
            // create card object and add to card list to paint GUI
            MagicCard mc = new MagicCard(amountOfCard, cmc, nameOfCard);

            System.out.println(mc.getCardName());
            mc.setColoredManaPips(manaPips);
            this.cardArrayList.add(mc);
        }
        catch(MalformedURLException me){
            me.printStackTrace();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private void parseNameAndAmount(String line) {
        int numberOfEachCard = 0;
        String name = "";
        // parsing out amount of each unique card and that card's name
        for(int i = 0; i < line.length(); i++){
            char ch = line.charAt(i);
            if(Character.isDigit(ch))
                numberOfEachCard = Character.getNumericValue(ch);
            else if (ch != ' ')
                name += ch;
        }
        this.createCard(numberOfEachCard, name);
    }
    private void getColors() {
        // iterate through each card and pull colored pips out of card object
        for(MagicCard magicCard : this.deck.getCards()) {
            HashMap<Character, Integer> pipsHashMap = magicCard.getColoredPips();
            // iterate through map of colored pips in card
            for (Map.Entry<Character, Integer> entry : pipsHashMap.entrySet()) {
                if (entry.getValue() != 0) {
                    Character key = entry.getKey();
                    // add pips to new map
                    if(this.landsNeeded.get(key) == null){
                        this.landsNeeded.put(key, entry.getValue());
                    }
                    else{
                        int valAtKeyInColors = this.landsNeeded.get(key);
                        this.landsNeeded.replace(key,valAtKeyInColors + entry.getValue());
                    }
                }
            }
        }
        this.setManaLabels(this.landsNeeded);
    }
    private void setManaLabels(HashMap <Character, Integer> colors){
        for(Map.Entry<Character, Integer> lands : colors.entrySet()) {
            if (lands.getValue() != 0) {
                char key = lands.getKey();

                // round up if number is odd
                if (lands.getValue() % 2 != 0) {
                    colors.replace(key, lands.getValue() + 1);
                }
                // get number of lands needed for deck by dividing by 2.1
                int numLands = (int) (lands.getValue() / 2.1);
                this.landsNeeded.replace(key, numLands);

                switch (key) {
                    case 'W':
                        whiteLbl.setText(whiteLbl.getText() + "\t" + numLands);
                        break;
                    case 'U':
                        blueLbl.setText(blueLbl.getText() + "\t" + +numLands);
                        break;
                    case 'B':
                        blackLbl.setText(blackLbl.getText() + "\t" + +numLands);
                        break;
                    case 'R':
                        redLbl.setText(redLbl.getText() + "\t" + +numLands);
                        break;
                    case 'G':
                        greenLbl.setText(greenLbl.getText() + "\t" + +numLands);
                        break;
                    default:
                        System.out.println("Color does not exist.");
                }
            }
        }
    }
    private void reset() {
        this.whiteLbl.setText("White Mana: ");
        this.blueLbl.setText("Blue Mana: ");
        this.blackLbl.setText("Black Mana: ");
        this.redLbl.setText("Red Mana: ");
        this.greenLbl.setText("Green Mana: ");
        this.avgCMCLbl.setText("Average CMC: ");
        this.statusLbl.setText("Ready");
        this.cardArrayList.clear();
        this.landsNeeded.clear();
        this.progressBar.setVisible(false);
        this.progressBar.setProgress(-1);
        this.process();
    }
    static void main(String[] args) {
        launch(args);
    }
}
