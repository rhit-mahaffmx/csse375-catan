package com.catan.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.catan.domain.Board;
import com.catan.domain.DevCards;
import com.catan.domain.DevelopmentCard;
import com.catan.domain.Player;
import com.catan.domain.ResourceType;
import com.catan.domain.TradeInfo;
import com.catan.domain.Turn;
import com.catan.domain.TurnStateData;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameWindow {
    VBox vbox;
    ImageView robberImageView = new ImageView();

    public final static int BUTTON_RADIUS = 10;

    // private final Rectangle topTurnBar = new Rectangle();
    private final HBox topTurnBar = new HBox(10); // The '10' adds a 10px gap between items inside the box
    private final Circle turnColorIndicator = new Circle(8); // An 8px radius circle
    private final Text turnText = new Text();

    private final Pane pane = new Pane();
    private final Stage primaryStage = new Stage();
    private final int settlementImageSize = 50;
    private final int settlementImageOffset = 15;
    private ImageView currentDiceImageView;
    private Internationalization i18n;
    private final ArrayList<Text> lastText = new ArrayList<>();
    private final ArrayList<ImageView> lastImages = new ArrayList<>();
    private final ArrayList<Button> lastButtons = new ArrayList<>();
    private final HashMap<String, String> realToDisplayText = new HashMap<>();

    public GameWindow(Internationalization i18n) {
        BackgroundSize backgroundPos = new BackgroundSize(800, 700, false, false, false, false);
        Image background = new Image("/imgs/general/Catan.jpg");
        BackgroundPosition imagePos = new BackgroundPosition(null,2 , false, null, 50, false);
        BackgroundImage backgroundImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, imagePos, backgroundPos);
        pane.setBackground(new Background(backgroundImage));

        Scene scene = new Scene(pane, 1300, 800);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        vbox = new VBox();

        robberImageView.setImage(new Image("/imgs/general/robber.jpg"));
        pane.getChildren().add(robberImageView);
        this.i18n = i18n;

        realToDisplayText.put(i18n.getText("red"), "RED");
        realToDisplayText.put(i18n.getText("blue"), "BLUE");
        realToDisplayText.put(i18n.getText("orange"), "ORANGE");
        realToDisplayText.put(i18n.getText("white"), "WHITE");
        realToDisplayText.put(i18n.getText("bank"), "BANK");

        topTurnBar.setAlignment(Pos.CENTER);
        topTurnBar.setPrefHeight(30);
        topTurnBar.setLayoutX(0);
        topTurnBar.setLayoutY(0);
        topTurnBar.prefWidthProperty().bind(pane.widthProperty()); 
        
        // Give the bar a sleek, neutral background with a slight bottom border
        topTurnBar.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #cccccc; -fx-border-width: 0 0 2 0;");

        // Make the text bold
        turnText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Add the circle and text into the HBox, then add the HBox to the screen
        topTurnBar.getChildren().addAll(turnColorIndicator, turnText);
        pane.getChildren().add(topTurnBar);
    }

    public void run() {
        primaryStage.show();
    }

    public void addCityButton(Board board, int x, int y) {
        int buttonDiameter = BUTTON_RADIUS * 2;

        Button button = new Button();

        button.setShape(new Circle(BUTTON_RADIUS));
        button.setStyle("-fx-opacity: 0;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-opacity: 1;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-opacity: 0;"));

        button.setMinSize(buttonDiameter, buttonDiameter);
        button.setMaxSize(buttonDiameter, buttonDiameter);

        button.setLayoutX(x);
        button.setLayoutY(y);

        button.setOnAction(e -> {
            board.onCityPointClick(x, y);
        });

        pane.getChildren().add(button);
    }

    public void addRoadButton(Board board, int x, int y) {
        int buttonDiameter = BUTTON_RADIUS * 2;

        Button button = new Button();

        button.setShape(new Circle(BUTTON_RADIUS));
        button.setStyle("-fx-opacity: 0;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-opacity: 1;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-opacity: 0;"));

        button.setMinSize(buttonDiameter, buttonDiameter);
        button.setMaxSize(buttonDiameter, buttonDiameter);

        button.setLayoutX(x);
        button.setLayoutY(y);

        button.setOnAction(e -> {
            board.onRoadPointClick(x, y);
        });

        pane.getChildren().add(button);
    }

    public void showSettlement(Turn turn, int x, int y){
        ImageView image = new ImageView();

        if(turn == Turn.RED){
            image.setImage(new Image("/imgs/general/redSettlement.png"));
        }
        else if(turn == Turn.BLUE){
            image.setImage(new Image("/imgs/general/blueSettlement.png"));
        }
        else if(turn == Turn.ORANGE){
            image.setImage(new Image("/imgs/general/orangeSettlement.png"));
        }
        else if(turn == Turn.WHITE){
            image.setImage(new Image("/imgs/general/whiteSettlement.png"));
        }

        image.setFitHeight(settlementImageSize);
        image.setFitWidth(settlementImageSize);

        image.setX(x - settlementImageOffset);
        image.setY(y - settlementImageOffset);

        pane.getChildren().add(image);
        pane.getChildren().get(pane.getChildren().indexOf(image)).toBack();
    }

    public void showCity(Turn turn, int x, int y) {
        ImageView image = new ImageView();

        if(null != turn)
        switch (turn) {
            case RED:
                image.setImage(new Image("/imgs/general/redCity.png"));
                break;
            case BLUE:
                image.setImage(new Image("/imgs/general/blueCity.png"));
                break;
            case ORANGE:
                image.setImage(new Image("/imgs/general/orangeCity.png"));
                break;
            case WHITE:
                image.setImage(new Image("/imgs/general/whiteCity.png"));
                break;
            default:
                break;
        }

        image.setFitHeight(settlementImageSize);
        image.setFitWidth(settlementImageSize);

        image.setX(x - settlementImageOffset);
        image.setY(y - settlementImageOffset);

        pane.getChildren().add(image);
    }

    public void showRoad(Turn turn, int x, int y) {
        ImageView image = new ImageView();

        if(null == turn){
            image.setImage(new Image("/imgs/general/whiteRoad.png"));
        }
        else switch (turn) {
            case RED:
                image.setImage(new Image("/imgs/general/redRoad.png"));
                break;
            case BLUE:
                image.setImage(new Image("/imgs/general/blueRoad.png"));
                break;
            case ORANGE:
                image.setImage(new Image("/imgs/general/orangeRoad.png"));
                break;
            default:
                image.setImage(new Image("/imgs/general/whiteRoad.png"));
                break;
        }

        image.setFitHeight(30);
        image.setFitWidth(30);

        image.setX(x);
        image.setY(y);

        pane.getChildren().add(image);
    }

    public static PlayerInfo getPlayerInfo(Turn turn) {
        String playerName = "";
        Color playerColor = Color.TRANSPARENT;

        if (turn == Turn.RED) {
            playerName = "Player 1 (Red)";
            playerColor = Color.RED;
        } else if (turn == Turn.BLUE) {
            playerName = "Player 2 (Blue)";
            playerColor = Color.BLUE;
        } else if (turn == Turn.ORANGE) {
            playerName = "Player 3 (Orange)";
            playerColor = Color.ORANGE;
        } else if (turn == Turn.WHITE) {
            playerName = "Player 4 (White)";
            playerColor = Color.WHITE;
        }

        return new PlayerInfo(playerName, playerColor);
    }

    public void showInitialTurnState(TurnStateData turnData) {
        pane.getChildren().remove(vbox);

        vbox = new VBox();

        for(String translated : realToDisplayText.keySet()){
            if(realToDisplayText.get(translated) == turnData.turn.toString()){
                vbox.getChildren().add(new Text(translated));
            }
        }

        vbox.getChildren().add(showSettlementsBox(turnData.settlements));
        vbox.getChildren().add(showRoadsBox(turnData.roads));
        vbox.getChildren().add(new Text(i18n.getText("victoryPoints") + turnData.victoryPoints));

        pane.getChildren().add(vbox);

        PlayerInfo playerInfo = getPlayerInfo(turnData.turn);

        // Update the text variable
        turnText.setText("Current Turn: " + playerInfo.name());
        
        // Update the circle variable
        turnColorIndicator.setFill(playerInfo.color());
        turnColorIndicator.setStroke(Color.BLACK); 

        // Bring the HBox to the front
        topTurnBar.toFront();

    }

    private HBox showSettlementsBox(int settlements) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        Image image = new Image("/imgs/general/house.jpg");

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);

        hbox.getChildren().add(imageView);
        hbox.getChildren().add(new Text(Integer.toString(settlements)));

        return hbox;
    }

    private HBox showRoadsBox(int roads) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        Image image = new Image("/imgs/general/road.jpg");

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);

        hbox.getChildren().add(imageView);
        hbox.getChildren().add(new Text(Integer.toString(roads)));

        return hbox;
    }

    public void addNextTurnButton(Board board) {
        Button button = new Button();
        button.setShape(new Rectangle(20, 10));

        button.setOnAction(e -> {
            board.onNextTurnClick();
        });

        button.setText(i18n.getText("nextTurn"));

        button.setLayoutX(pane.getWidth() - button.getShape().getBoundsInLocal().getWidth() * 5.5);
        button.setLayoutY(pane.getHeight() - button.getShape().getBoundsInLocal().getHeight() * 4);

        pane.getChildren().add(button);
    }

    public void addDiceRollButton(Board board) {
        Button button = new Button();
        button.setShape(new Rectangle(20, 10));

        button.setOnAction(e -> {
            board.onRollDiceClick();
        });

        button.setText(i18n.getText("rollDice"));

        button.setLayoutX(pane.getWidth() - button.getShape().getBoundsInLocal().getWidth() * 5.25);
        button.setLayoutY(pane.getHeight() - button.getShape().getBoundsInLocal().getHeight() * 7);

        pane.getChildren().add(button);
    }

    public void showDiceRoll(int diceRoll) {
        if (currentDiceImageView != null) {
            pane.getChildren().remove(currentDiceImageView);
        }

        String resourcePath = "/imgs/dice/dice_" + diceRoll + ".png";

        Image diceImage = new Image(getClass().getResource(resourcePath).toExternalForm());

        ImageView diceImageView = new ImageView(diceImage);
        diceImageView.setPreserveRatio(true);
        diceImageView.setFitWidth(200);
        diceImageView.setFitHeight(200);
        diceImageView.setLayoutX(600);
        diceImageView.setLayoutY(690);

        pane.getChildren().add(diceImageView);

        currentDiceImageView = diceImageView;
    }


    public void showInitialRobberState(int x, int y) {
        robberImageView.setFitHeight(settlementImageSize);
        robberImageView.setFitWidth(settlementImageSize);

        robberImageView.setX(x - settlementImageOffset);
        robberImageView.setY(y - settlementImageOffset);
    }

    public void addRobberButton(Board board, int x, int y) {
        int buttonDiameter = BUTTON_RADIUS * 2;

        Button button = new Button();
        button.setShape(new Circle(BUTTON_RADIUS));

        button.setStyle("-fx-opacity: 0;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-opacity: 1;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-opacity: 0;"));

        button.setMinSize(buttonDiameter, buttonDiameter);
        button.setMaxSize(buttonDiameter, buttonDiameter);

        button.setLayoutX(x);
        button.setLayoutY(y);

        button.setOnAction(e -> {
            board.onRobberPointClick(x, y);
        });

        pane.getChildren().add(button);
    }

    public void addDevCardButton(Board board) {
        Button button = new Button();
        button.setShape(new Rectangle(20, 10));
        button.setOnAction(e -> board.onBuyDevCardClick());
        button.setText(i18n.getText("buyDevCard"));

        button.setLayoutX(pane.getWidth() - button.getShape().getBoundsInLocal().getWidth() * 9);
        button.setLayoutY(pane.getHeight() - button.getShape().getBoundsInLocal().getHeight() * 10);

        pane.getChildren().add(button);
    }

    public void showDevCard(Board board, ArrayList<DevelopmentCard> devCards) {
        int imageOffset = 1;
        int textOffset = 1;

        HashMap<DevCards, Integer> counts = countCards(devCards);

        clearDevText();
        clearDevImages();
        clearDevButtons();

        for(DevCards card : counts.keySet()) {
            if(counts.get(card) == 1){
                drawNewDevCard(card, imageOffset, board);

                imageOffset++;
                textOffset++;
            }
            else if(counts.get(card) >= 2){
                drawNewDevCard(card, imageOffset, board);

                imageOffset++;

                Text cardLabel = new Text("X " + counts.get(card));
                lastText.add(cardLabel);
                cardLabel.setStyle("-fx-font-size: 20; -fx-fill: black;");
                cardLabel.setX(pane.getWidth() - 100 * textOffset);
                cardLabel.setY(175);

                pane.getChildren().add(cardLabel);

                textOffset++;
            }
        }

    }

    public void clearDevText(){
        for(Text text : lastText){
            pane.getChildren().remove(text);
        }
        lastText.clear();
    }

    public void clearDevImages(){
        for(ImageView image : lastImages){
            pane.getChildren().remove(image);
        }
        lastImages.clear();
    }

    public void clearDevButtons(){
        for(Button button : lastButtons){
            pane.getChildren().remove(button);
        }
        lastButtons.clear();
    }

    private HashMap<DevCards, Integer> countCards(ArrayList<DevelopmentCard> cards) {
        HashMap<DevCards, Integer> count = new HashMap<>();

        for(DevelopmentCard card : cards) {
            if(count.get(card.getType()) == null) {
                count.put(card.getType(), 1);
            }
            else{
                count.put(card.getType(), count.get(card.getType()) + 1);
            }
        }

        return count;
    }

    private void drawNewDevCard(DevCards type, int offset, Board board){
        ImageView image = new ImageView();

        if(type == DevCards.KNIGHT) {
            image.setImage(new Image(i18n.getText("knightPath")));
        }
        else if(type == DevCards.VICTORY_POINT) {
            image.setImage(new Image(i18n.getText("victoryPointPath")));
        }
        else if(type == DevCards.MONOPOLY){
            image.setImage(new Image(i18n.getText("monopolyPath")));
        }
        else if(type == DevCards.ROAD_BUILDING){
            image.setImage(new Image(i18n.getText("roadBuildingPath")));
        }
        else{
            image.setImage(new Image(i18n.getText("yOpPath")));
        }

        image.setFitHeight(150);
        image.setFitWidth(100);

        image.setX(pane.getWidth() - image.getFitWidth() * offset);
        image.setY(0);

        int buttonDiameter = BUTTON_RADIUS * 4;

        Button button = new Button();
        button.setShape(new Circle(BUTTON_RADIUS));

        button.setMinSize(buttonDiameter, buttonDiameter);
        button.setMaxSize(buttonDiameter, buttonDiameter);

        button.setLayoutX(pane.getWidth() + image.getFitWidth()/2 - image.getFitWidth() * offset);
        button.setLayoutY(image.getFitWidth()/2);

        button.setOnAction(e -> {
            if(type == DevCards.YEAR_OF_PLENTY){
                board.onYoPClick();
            } else if(type == DevCards.MONOPOLY){
                board.onMonopolyClick();
            }
            else if(type == DevCards.ROAD_BUILDING){
                board.onRoadBuildingClick();
            }
            else if(type == DevCards.KNIGHT){
                board.onKnightCardClick();
            }
        });

        lastButtons.add(button);
        lastImages.add(image);

        pane.getChildren().add(image);
        pane.getChildren().add(button);
    }

    public void showInvalidInputAndPass(String message) {
        System.out.println(message);
    }

    public void showResourceCards(HashMap<ResourceType, Integer> resourceMap) {
        pane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("resourceDisplay"));

        HBox resourceBox = new HBox(10);
        resourceBox.setId("resourceDisplay");
        resourceBox.setLayoutX(850);
        resourceBox.setLayoutY(300);

        for (ResourceType type : ResourceType.values()) {
            if (type == ResourceType.NULL) {
                continue;
            }

            int count = resourceMap.getOrDefault(type, 0);

            String imagePath = "/imgs/resources/" + type.toString().toLowerCase() + ".png";

            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(70);
            imageView.setFitHeight(70);

            Text countText = new Text("X " + count);
            countText.setStyle("-fx-font-size: 20; -fx-fill: black;");

            VBox cardBox = new VBox(5, imageView, countText);
            cardBox.setAlignment(Pos.CENTER);

            resourceBox.getChildren().add(cardBox);
        }

        pane.getChildren().add(resourceBox);
    }

    public void addTradeDialogue(Board board){
        ComboBox<String> player2Dropdown = new ComboBox<>();

        player2Dropdown.getItems().addAll(
                i18n.getText("red"),
                i18n.getText("blue"),
                i18n.getText("orange"),
                i18n.getText("white"),
                i18n.getText("bank"));
        player2Dropdown.setValue(i18n.getText("red"));

        TextField player1Wood = new TextField();
        player1Wood.setPromptText(i18n.getText("woodToTrade"));

        TextField player1Brick = new TextField();
        player1Brick.setPromptText(i18n.getText("brickToTrade"));

        TextField player1Sheep = new TextField();
        player1Sheep.setPromptText(i18n.getText("sheepToTrade"));

        TextField player1Wheat = new TextField();
        player1Wheat.setPromptText(i18n.getText("wheatToTrade"));

        TextField player1Ore = new TextField();
        player1Ore.setPromptText(i18n.getText("oreToTrade"));

        TextField player2Wood = new TextField();
        player2Wood.setPromptText(i18n.getText("woodToReceive"));

        TextField player2Brick = new TextField();
        player2Brick.setPromptText(i18n.getText("brickToReceive"));

        TextField player2Sheep = new TextField();
        player2Sheep.setPromptText(i18n.getText("sheepToReceive"));

        TextField player2Wheat = new TextField();
        player2Wheat.setPromptText(i18n.getText("wheatToReceive"));

        TextField player2Ore = new TextField();
        player2Ore.setPromptText(i18n.getText("oreToReceive"));

        Button submitButton = new Button(i18n.getText("submit"));
        submitButton.setOnAction(e -> {
            TradeInfo offer = new TradeInfo();
            TradeInfo receive = new TradeInfo();

            receive.setPlayer(Turn.valueOf(realToDisplayText.get(player2Dropdown.getValue())));

            offer.setResources(ResourceType.WOOD, defaultZeroOnEmpty(player1Wood));
            offer.setResources(ResourceType.BRICK, defaultZeroOnEmpty(player1Brick));
            offer.setResources(ResourceType.SHEEP, defaultZeroOnEmpty(player1Sheep));
            offer.setResources(ResourceType.WHEAT, defaultZeroOnEmpty(player1Wheat));
            offer.setResources(ResourceType.ORE, defaultZeroOnEmpty(player1Ore));

            receive.setResources(ResourceType.WOOD, defaultZeroOnEmpty(player2Wood));
            receive.setResources(ResourceType.BRICK, defaultZeroOnEmpty(player2Brick));
            receive.setResources(ResourceType.SHEEP, defaultZeroOnEmpty(player2Sheep));
            receive.setResources(ResourceType.WHEAT, defaultZeroOnEmpty(player2Wheat));
            receive.setResources(ResourceType.ORE, defaultZeroOnEmpty(player2Ore));

            if(receive.getPlayer().equals(Turn.BANK)){
                board.onBankSubmitClick(offer, receive);
            }
            else{
                board.onTradeSubmitClick(offer, receive);
            }
        });

        GridPane grid = new GridPane();
        grid.setLayoutX(800);
        grid.setLayoutY(600);

        grid.add(new Text(i18n.getText("tradeWith")), 0, 0);
        grid.add(player2Dropdown, 1, 0);
        grid.add(new Text(i18n.getText("youOffer")), 0, 1);
        grid.add(new Text(i18n.getText("youWant")), 1, 1);

        grid.add(player1Wood, 0, 2);
        grid.add(player2Wood, 1, 2);
        grid.add(player1Brick, 0, 3);
        grid.add(player2Brick, 1, 3);
        grid.add(player1Sheep, 0, 4);
        grid.add(player2Sheep, 1, 4);
        grid.add(player1Wheat, 0, 5);
        grid.add(player2Wheat, 1, 5);
        grid.add(player1Ore, 0, 6);
        grid.add(player2Ore, 1, 6);

        grid.add(submitButton, 1, 7);

        pane.getChildren().add(grid);
    }

    private int defaultZeroOnEmpty(TextField text){
        if(text.getText().isEmpty()){
            return 0;
        }
        else{
            return Integer.parseInt(text.getText());
        }
    }


    public void gameOver(Player winner){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(i18n.getText("gameOver"));
        alert.setHeaderText(null);
        alert.setContentText(winner.color.toString() + i18n.getText("wins"));
        alert.show();
    }

    public void addDiscardDialogue(Board board) {
        HashMap<Turn, HashMap<ResourceType,Integer>> hands = board.getAllPlayersResourceMaps();

        ArrayList<Turn> toDiscard = new ArrayList<>();
        HashMap<Turn,Integer> required = new HashMap<>();

        for (Turn turn : hands.keySet()) {
            int total = 0;
            for (int val : hands.get(turn).values()) {
                total += val;
            }
            if (total > 7) {
                toDiscard.add(turn);
                required.put(turn, total / 2);
            }
        }
        if (toDiscard.isEmpty()) {
            return;
        }

        GridPane dialog = new GridPane();
        dialog.setId("discardDialog");
        dialog.setHgap(20);
        dialog.setVgap(20);
        dialog.setPadding(new Insets(10,5,0,5));
        dialog.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(5), Insets.EMPTY)));

        for (int i = 0; i < toDiscard.size(); i++) {
            Turn turn = toDiscard.get(i);

            HashMap<ResourceType,Integer> hand = hands.get(turn);

            int need = required.get(turn);

            VBox playerBox = new VBox(5);
            playerBox.setPadding(new Insets(5));
            playerBox.setStyle("-fx-border-color: gray; -fx-border-radius: 3;");
            playerBox.getChildren().add(new Text(turn + i18n.getText("mustDiscard") + need));

            HBox handRow = new HBox(4);
            handRow.getChildren().add(new Text(i18n.getText("has")));

            for (ResourceType type : ResourceType.values()) {
                if (type == ResourceType.NULL){
                    continue;
                }

                handRow.getChildren().add(new Text(type + "x" + hand.get(type)));
            }

            playerBox.getChildren().add(handRow);

            for (ResourceType type : ResourceType.values()) {
                if (type == ResourceType.NULL){
                    continue;
                }

                HBox row = new HBox(4);
                row.getChildren().add(new Text(i18n.getText("discard") + type + ":"));

                TextField tf = new TextField();
                tf.setPrefWidth(40);
                tf.setPromptText("0");
                tf.setId(turn + "_" + type);

                row.getChildren().add(tf);

                playerBox.getChildren().add(row);
            }

            int col = i % 2;
            int row = i / 2;
            dialog.add(playerBox, col, row);
        }
        Button submit = new Button(i18n.getText("submit"));

        submit.setOnAction(e -> {
            HashMap<Turn, HashMap<ResourceType,Integer>> allDiscards = new HashMap<>();
            for (Turn turn : toDiscard) {
                HashMap<ResourceType,Integer> dm = new HashMap<>();
                for (ResourceType type : ResourceType.values()) {
                    if (type == ResourceType.NULL) continue;
                    TextField tf = (TextField) dialog.lookup("#" + turn + "_" + type);
                    int placeHolder = defaultZeroOnEmpty(tf);
                    dm.put(type, placeHolder);
                }
                allDiscards.put(turn, dm);
            }
            board.onSubmitDiscard(allDiscards);
            pane.getChildren().remove(dialog);
        });

        dialog.add(submit, 0, 2, 2, 1);
        dialog.layoutXProperty().bind(pane.widthProperty().subtract(dialog.widthProperty()).subtract(20));
        dialog.setLayoutY(20);

        pane.getChildren().add(dialog);
    }

    public void removeDiscardDialog() {
        pane.getChildren().removeIf(node ->
                "discardPrompt".equals(node.getId())
        );
    }

    public void showStealDialog(Board board, Set<Player> players) {
        ArrayList<String> playersToRobFrom = new ArrayList<>();

        for (Player player : players) {
            playersToRobFrom.add(player.color.toString());
        }

        ComboBox<String> player2Dropdown = new ComboBox<>();
        player2Dropdown.getItems().addAll(playersToRobFrom);
        player2Dropdown.setValue(playersToRobFrom.getFirst());

        Button submitButton = new Button(i18n.getText("steal"));
        submitButton.setOnAction(e -> {
            board.robCardFromPlayer(Turn.valueOf(player2Dropdown.getValue()), new Random());
        });

        GridPane grid = new GridPane();
        grid.setId("RobPlayer");
        grid.setLayoutX(800);
        grid.setLayoutY(400);

        grid.add(new Text(i18n.getText("tradeWith")), 0, 0);
        grid.add(player2Dropdown, 1, 0);
        grid.add(submitButton, 2, 0);

        pane.getChildren().add(grid);
    }

    public void removeStealDialog() {
        pane.getChildren().removeIf(node ->
                "RobPlayer".equals(node.getId())
        );
    }

    public void openYoPDialogue(Board board, Turn player) {
        List<String> resources = List.of("WOOD", "BRICK", "SHEEP", "WHEAT", "ORE");

        VBox root = new VBox(10);

        List<CheckBox> checkBoxes = new ArrayList<>();

        for (String resource : resources) {
            CheckBox checkBox = new CheckBox(resource);
            checkBoxes.add(checkBox);
            root.getChildren().add(checkBox);
        }

        Button submitButton = new Button(i18n.getText("submit"));
        submitButton.setOnAction(e -> {
            List<String> selectedResources = new ArrayList<>();
            int numSelected = 0;

            for (CheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    numSelected++;
                    selectedResources.add(cb.getText());
                }
            }

            if(numSelected == 1){
                TradeInfo offer = new TradeInfo();
                TradeInfo receive = new TradeInfo();

                offer.setPlayer(player);

                receive.setPlayer(Turn.BANK);
                receive.setResources(ResourceType.valueOf(selectedResources.getFirst()), 2);

                board.onTradeSubmitClick(offer, receive);

                pane.getChildren().remove(root);
                return;
            }
            else if(numSelected == 2){
                TradeInfo offer = new TradeInfo();
                TradeInfo receive = new TradeInfo();

                offer.setPlayer(player);
                receive.setPlayer(Turn.BANK);

                receive.setResources(ResourceType.valueOf(selectedResources.getFirst()), 1);
                receive.setResources(ResourceType.valueOf(selectedResources.get(1)), 1);

                board.onTradeSubmitClick(offer, receive);

                pane.getChildren().remove(root);
                return;
            }
        });

        root.getChildren().add(submitButton);

        root.setLayoutX(1100);
        root.setLayoutY(500);

        pane.getChildren().add(root);
    }

    public void openMonopolyDialogue(Board board, Turn player) {
        VBox root = new VBox(10);

        ToggleGroup group = new ToggleGroup();
        for (ResourceType type : List.of(
                ResourceType.WOOD,
                ResourceType.BRICK,
                ResourceType.SHEEP,
                ResourceType.WHEAT,
                ResourceType.ORE)) {
            RadioButton rb = new RadioButton(type.toString());
            rb.setToggleGroup(group);
            root.getChildren().add(rb);
        }

        Button submit = new Button("Take All");
        submit.setOnAction(e -> {
            RadioButton sel = (RadioButton) group.getSelectedToggle();
            if (sel != null) {
                board.executeMonopoly(player, ResourceType.valueOf(sel.getText()));
                pane.getChildren().remove(root);
            }
        });

        root.getChildren().add(submit);
        root.setLayoutX(1100);
        root.setLayoutY(550);

        pane.getChildren().add(root);
    }

}
