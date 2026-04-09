package com.catan.presentation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import com.catan.datasource.BoardDataInputs;
import com.catan.domain.Board;
import com.catan.domain.GameWindowController;
import com.catan.domain.NumberCardDeck;
import com.catan.domain.TurnStateMachine;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws  Exception {
        Internationalization i18n = new Internationalization();

        Pane langPane = new Pane();
        Scene langScene = new Scene(langPane, 300, 300);
        stage.setScene(langScene);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("English", "Espanol");
        comboBox.setPromptText("Choose Language");

        Button submitButton = new Button(i18n.getText("submit"));

        VBox selectorBox = new VBox(comboBox, submitButton);
        selectorBox.setAlignment(Pos.CENTER);
        selectorBox.setLayoutX(85);
        selectorBox.setLayoutY(100);

        langPane.getChildren().add(selectorBox);

        submitButton.setOnAction(e -> {
            String selected = comboBox.getValue();
            if (selected != null) {
                if (selected.equals("English")) {
                    i18n.setLocale(new Locale("en", "US"));
                } else if (selected.equals("Espanol")) {
                    i18n.setLocale(new Locale("es", "MX"));
                }
                GameWindow gameWindow = new GameWindow(i18n);
                GameWindowController gameWindowController = new GameWindowController(gameWindow);
                TurnStateMachine turnStateMachine = new TurnStateMachine();
                NumberCardDeck deck = new NumberCardDeck(new Random());
                Board board;
                try {
                    board = new Board(gameWindowController, turnStateMachine, deck);
                    FileInputStream cityCoordsStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
                    FileInputStream cityTerrainsStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
                    FileInputStream cityValuesStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
                    FileInputStream harborsStream = new FileInputStream(Board.HARBORS_FILE_PATH);
                    FileInputStream roadCoordsStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
                    FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
                    FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
                    FileInputStream robberCoordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
                    FileInputStream robberResourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
                    FileInputStream robberNumberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);

                    BoardDataInputs dataInputs = new BoardDataInputs(
                            cityCoordsStream, cityTerrainsStream, cityValuesStream,
                            harborsStream, roadCoordsStream, cityNeighborsStream,
                            roadNeighborsStream, robberCoordsStream, robberResourceStream,
                            robberNumberStream
                    );
                    board.loadBoardData(dataInputs);

                } catch (IOException excep) {

                    throw new RuntimeException("Failed to load essential game data, application cannot start.", excep);
                }
                board.addAllCities(board.cityPoints);
                board.addAllRoadPoints(board.roadPoints);
                board.addAllRobberPoints(board.robberPoints);
                board.addNextTurnButton();
                board.addDiceRollButton();
                board.addBuyDevCardButton();
                board.showInitialTurnState();
                board.showInitialRobberState();
                board.showResources();
                board.showDevCards();
                board.showTradeDialogue();
                board.showDiscardDialog();
                board.startGame();
            }
        });
        stage.show();
    }


}
