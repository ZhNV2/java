package ru.spbau.zhidkov;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/** Class implementing game UI */
public class Main extends Application {

    private static final double MIN_BUTTON_SIZE = 50;

    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String EMPTY_CELL = "";

    private static final int SIZE = 4;
    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 300;

    private int turns = 0;
    private int pairsLeft = SIZE * SIZE / 2;

    private ButtonCell currentChosen = null;
    private GridPane gridPane = new GridPane();

    private List<ButtonCell> toFlushText = new ArrayList<>();
    private List<ButtonCell> toFlushEffect = new ArrayList<>();

    /** Starts the game flow */
    public void start(Stage primaryStage) throws Exception {

        fillGrid();

        Scene scene = new Scene(gridPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void process(ButtonCell buttonCell) {
        flushText();
        flushEffect();
        if (!buttonCell.isActive() || currentChosen == buttonCell) {
            return;
        }
        DropShadow shadow = new DropShadow();
        buttonCell.setEffect(shadow);
        if (currentChosen == null) {
            currentChosen = buttonCell;
        } else {
            turns++;
            currentChosen.setText(currentChosen.getValue());
            buttonCell.setText(buttonCell.getValue());
            if (currentChosen.getValue().equals(buttonCell.getValue())) {
                buttonCell.setActiveFalse();
                currentChosen.setActiveFalse();
                pairsLeft--;
                if (pairsLeft == 0) {
                    finish();
                }
            } else {
                toFlushText.add(buttonCell);
                toFlushText.add(currentChosen);
            }
            toFlushEffect.add(buttonCell);
            toFlushEffect.add(currentChosen);
            currentChosen = null;
        }

    }

    private void flushEffect() {
        for (ButtonCell button : toFlushEffect) {
            button.setEffect(null);
        }
        toFlushEffect.clear();
    }

    private void flushText() {
        for (ButtonCell button : toFlushText) {
            button.setText(EMPTY_CELL);
        }
        toFlushText.clear();
    }

    private void fillGrid() {
        final Random random = new Random();
        int cntZeroes = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String value;
                if (i != SIZE - 1 || j != SIZE - 1) {
                    value = random.nextInt() % 2 == 0 ? ZERO : ONE;
                    cntZeroes += value.equals(ZERO) ? 1 : 0;
                } else {
                    value = cntZeroes % 2 == 1 ? ZERO : ONE;
                }
                final ButtonCell buttonCell = new ButtonCell(value);
                buttonCell.setMinSize(MIN_BUTTON_SIZE, MIN_BUTTON_SIZE);
                buttonCell.setOnAction(event -> {
                    try {
                        process(buttonCell);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                gridPane.add(buttonCell, i, j);
            }
        }
    }

    private void finish() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Game over");
        alert.setContentText("You win with " + turns + " turns");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK){
                Platform.exit();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /** Class for button holding a value */
    private static class ButtonCell extends Button {

        final private String value;

        public boolean isActive() {
            return active;
        }

        public void setActiveFalse() {
            this.active = false;
        }

        private boolean active = true;

        public ButtonCell(String value) {
            this.value = value;
        }

        public String getValue() { return  value; }

    }
}
