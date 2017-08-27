/*
 * Plexus
 * By Santiago Benoit
 * Copyright (c) 2017 Rectangular Chicken. All rights reserved.
*/

package com.rectangularchicken.plexus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * The graphical user interface.
 * @author Santiago Benoit
 */
public class GUI extends Application {
    
    public Parent title() {
        Label ple = new Label();
        ple.setGraphic(new ImageView("/resources/image/ple.png"));
        Label x = new Label();
        x.setGraphic(new ImageView("/resources/image/x.png"));
        Label us = new Label();
        us.setGraphic(new ImageView("/resources/image/us.png"));
        HBox title = new HBox();
        title.setPadding(new Insets(10));
        title.setSpacing(-20);
        title.setAlignment(Pos.CENTER);
        title.getChildren().addAll(ple, x, us);
        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(setupScene);
            stage.centerOnScreen();
        });
        Button loadGame = new Button("Load Game");
        loadGame.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            FileChooser loader = new FileChooser();
            loader.setTitle("Load Save File");
            loader.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.sav"));
            if (SAVES.exists()) {
                loader.setInitialDirectory(SAVES);
            }
            File save = loader.showOpenDialog(stage);
            if (save != null) {
                try {
                    deserialize(save);
                    initMap();
                    refreshMap();
                    stage.setScene(gameScene);
                    Platform.runLater(() -> {
                        stage.setFullScreen(fullscreen);
                    });
                    stage.centerOnScreen();
                    gameInProgress = true;
                } catch (Exception ex) {
                    error.showAndWait();
                }
            }
        });
        Button howToPlay = new Button("How to Play");
        howToPlay.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(instructionScene);
            stage.centerOnScreen();
        });
        Button settings = new Button("Settings");
        settings.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(settingScene);
            stage.centerOnScreen();
        });
        HBox buttons = new HBox();
        buttons.setPadding(new Insets(0, 10, 20, 10));
        buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(newGame, loadGame, howToPlay, settings);
        Label info = new Label("Copyright \u00a9 2017 Rectangular Chicken. All rights reserved.");
        info.setAlignment(Pos.BOTTOM_CENTER);
        StackPane bottomPane = new StackPane();
        bottomPane.setAlignment(Pos.BOTTOM_CENTER);
        bottomPane.getChildren().add(info);
        BorderPane border = new BorderPane();
        border.setTop(title);
        border.setCenter(buttons);
        border.setBottom(bottomPane);
        Group group = new Group();
        group.getChildren().add(border);
        return group;
    }
    
    public Parent setup() {
        Label title = new Label("Setup Game");
        title.setId("title");
        title.setPadding(new Insets(10, 10, 10, 10));
        Label playersLabel = new Label("Players:");
        Spinner playersSpinner = new Spinner();
        playersSpinner.setEditable(true);
        playersSpinner.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        playersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 4, 2));
        playersSpinner.focusedProperty().addListener(l -> {
            playersSpinner.increment(0);
        });
        HBox playersBox = new HBox();
        playersBox.setSpacing(10);
        playersBox.getChildren().addAll(playersLabel, playersSpinner);
        Label nodesLabel = new Label("Nodes:");
        Spinner nodesSpinner = new Spinner();
        nodesSpinner.setEditable(true);
        nodesSpinner.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        nodesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 999, 20));
        nodesSpinner.focusedProperty().addListener(l -> {
            nodesSpinner.increment(0);
        });
        HBox nodesBox = new HBox();
        nodesBox.setSpacing(10);
        nodesBox.getChildren().addAll(nodesLabel, nodesSpinner);
        Label densityLabel = new Label("Density:");
        Spinner densitySpinner = new Spinner();
        densitySpinner.setEditable(true);
        densitySpinner.getEditor().setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        densitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1, 0.5, 0.1));
        densitySpinner.focusedProperty().addListener(l -> {
            densitySpinner.increment(0);
        });
        HBox densityBox = new HBox();
        densityBox.setSpacing(10);
        densityBox.getChildren().addAll(densityLabel, densitySpinner);
        ToggleButton singleplayer = new ToggleButton("Singleplayer");
        singleplayer.setSelected(true);
        singleplayer.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            singleplayer.setSelected(true);
        });
        ToggleButton multiplayer = new ToggleButton("Multiplayer");
        multiplayer.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            multiplayer.setSelected(true);
        });
        ToggleGroup playerToggle = new ToggleGroup();
        playerToggle.getToggles().addAll(singleplayer, multiplayer);
        HBox playerMode = new HBox();
        playerMode.setSpacing(10);
        playerMode.getChildren().addAll(singleplayer, multiplayer);
        VBox settings = new VBox();
        settings.setPadding(new Insets(10));
        settings.setSpacing(20);
        settings.setAlignment(Pos.CENTER);
        settings.getChildren().addAll(playerMode, playersBox, nodesBox, densityBox);
        Button ok = new Button("Play");
        ok.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            if (singleplayer.isSelected()) {
                mode = "single";
            } else {
                mode = "multi";
            }
            initPlexus((int) nodesSpinner.getValue(), Math.sqrt((double) densitySpinner.getValue()));
            players = (int) playersSpinner.getValue();
            initMap();
            nextTurn();
            stage.setScene(gameScene);
            Platform.runLater(() -> {
                stage.setFullScreen(fullscreen);
            });
            stage.centerOnScreen();
            gameInProgress = true;
        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(titleScene);
            stage.centerOnScreen();
        });
        HBox buttons = new HBox();
        buttons.setPadding(new Insets(10));
        buttons.setSpacing(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(ok, cancel);
        BorderPane border = new BorderPane();
        border.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        border.setCenter(settings);
        border.setBottom(buttons);
        Group group = new Group();
        group.getChildren().add(border);
        return group;
    }
    
    public Parent instructions() {
        Label title = new Label("How to Play");
        title.setId("title");
        title.setPadding(new Insets(10));
        Text text = new Text("Plexus is a strategy game for 2-4 players. The game is played on a randomly generated network of interconnected nodes. The objective of the game is to acquire the greatest number of connections between nodes as possible before the game ends. The game ends when the next player can no longer make any moves (even if other players still can).\n\nAt the start of the game, each player chooses their starting position, starting from the first player. Starting positions cannot be directly connected to each other. After all players have chosen their starting positions, the game proceeds in reverse turn order, starting from the last player. This is done to balance the advantage between choosing the starting position first and going first.\n\nOn your turn, you may claim an unclaimed node that is directly connected to one of your owned nodes. Each connection between your owned nodes will gain you one point. At the end of the game, if there is no tie for first place, whoever has the most points wins!\n\nVersion " + VERSION + "\nCreated by Santiago Benoit");
        text.setId("body");
        text.setWrappingWidth(614);
        text.setTextAlignment(TextAlignment.JUSTIFY);
        VBox textBox = new VBox();
        textBox.setSpacing(10);
        textBox.setPadding(new Insets(10));
        textBox.getChildren().addAll(text);
        ScrollPane sp = new ScrollPane();
        sp.setPrefSize(640, 480);
        sp.setContent(textBox);
        Button back = new Button("Back");
        back.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(titleScene);
            stage.centerOnScreen();
        });
        HBox button = new HBox();
        button.setPadding(new Insets(10));
        button.setAlignment(Pos.CENTER);
        button.getChildren().add(back);
        BorderPane border = new BorderPane();
        border.setTop(title);
        border.setCenter(sp);
        BorderPane.setAlignment(title, Pos.CENTER);
        border.setBottom(button);
        Group group = new Group();
        group.getChildren().add(border);
        return group;
    }
    
    public Parent game() {
        turnText = new Label();
        turnText.setId("title");
        turnText.setPadding(new Insets(10));
        Label text1 = new Label("Player 1:");
        text1.setId("header");
        text1.setStyle("-fx-text-fill: red");
        number1 = new Label();
        number1.setId("header");
        number1.setStyle("-fx-text-fill: red");
        box1 = new VBox();
        box1.setPadding(new Insets(10));
        box1.setAlignment(Pos.TOP_CENTER);
        box1.getChildren().addAll(text1, number1);
        Label text2 = new Label("Player 2:");
        text2.setId("header");
        text2.setStyle("-fx-text-fill: blue");
        number2 = new Label();
        number2.setId("header");
        number2.setStyle("-fx-text-fill: blue");
        box2 = new VBox();
        box2.setPadding(new Insets(10));
        box2.setAlignment(Pos.TOP_CENTER);
        box2.getChildren().addAll(text2, number2);
        Label text3 = new Label("Player 3:");
        text3.setId("header");
        text3.setStyle("-fx-text-fill: yellow");
        number3 = new Label();
        number3.setId("header");
        number3.setStyle("-fx-text-fill: yellow");
        box3 = new VBox();
        box3.setPadding(new Insets(10));
        box3.setAlignment(Pos.TOP_CENTER);
        box3.getChildren().addAll(text3, number3);
        Label text4 = new Label("Player 4:");
        text4.setId("header");
        text4.setStyle("-fx-text-fill: green");
        number4 = new Label();
        number4.setId("header");
        number4.setStyle("-fx-text-fill: green");
        box4 = new VBox();
        box4.setPadding(new Insets(10));
        box4.setAlignment(Pos.TOP_CENTER);
        box4.getChildren().addAll(text4, number4);
        VBox leftBox = new VBox();
        leftBox.setPadding(new Insets(10));
        leftBox.setAlignment(Pos.TOP_CENTER);
        leftBox.getChildren().addAll(box1, box3);
        VBox rightBox = new VBox();
        rightBox.setPadding(new Insets(10));
        rightBox.setAlignment(Pos.TOP_CENTER);
        rightBox.getChildren().addAll(box2, box4);
        map = new Group();
        Group wrapper = new Group();
        wrapper.getChildren().add(map);
        scroll = new ScrollPane();
        scroll.setPrefSize(640, 480);
        scroll.setPannable(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setContent(wrapper);
        scroll.addEventFilter(ScrollEvent.ANY, e -> {
            double factor = Math.pow((e.getDeltaY() > 0) ? 1.1 : 1 / 1.1, zoomExp);
            if (zoom < 10 && factor > 1 || zoom > -10 && factor < 1) {
                zoom += (e.getDeltaY() > 0) ? 1 : -1;
                Bounds wrapperBounds = wrapper.getLayoutBounds();
                Bounds scrollBounds = scroll.getLayoutBounds();
                double x = scroll.getHvalue() * (wrapperBounds.getWidth() - scrollBounds.getWidth());
                double y = scroll.getVvalue() * (wrapperBounds.getHeight() - scrollBounds.getHeight());
                Point2D position = new Point2D(e.getX(), e.getY());
                Point2D adjustment = wrapper.getLocalToParentTransform().deltaTransform(position.multiply(factor - 1));
                map.setScaleX(factor * map.getScaleX());
                map.setScaleY(factor * map.getScaleY());
                scroll.layout();
                wrapperBounds = wrapper.getLayoutBounds();
                scroll.setHvalue((x + adjustment.getX()) / (wrapperBounds.getWidth() - scrollBounds.getWidth()));
                scroll.setVvalue((y + adjustment.getY()) / (wrapperBounds.getHeight() - scrollBounds.getHeight()));
            }
            e.consume();
        });
        root = new StackPane();
        root.getChildren().addAll(scroll);
        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(setupScene);
            stage.centerOnScreen();
            gameInProgress = false;
        });
        Button saveGame = new Button("Save Game");
        saveGame.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            FileChooser saver = new FileChooser();
            saver.setTitle("Save Game");
            saver.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.sav"));
            if (SAVES.exists()) {
                saver.setInitialDirectory(SAVES);
            }
            File save = saver.showSaveDialog(stage);
            if (save != null) {
                try {
                    serialize(save);
                } catch (Exception ex) {
                    error.showAndWait();
                }
            }
        });
        Button loadGame = new Button("Load Game");
        loadGame.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            FileChooser loader = new FileChooser();
            loader.setTitle("Load Save File");
            loader.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save File", "*.sav"));
            if (SAVES.exists()) {
                loader.setInitialDirectory(SAVES);
            }
            File save = loader.showOpenDialog(stage);
            if (save != null) {
                try {
                    deserialize(save);
                    initMap();
                    refreshMap();
                } catch (Exception ex) {
                    error.showAndWait();
                }
            }
        });
        Button settings = new Button("Settings");
        settings.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(settingScene);
            stage.centerOnScreen();
        });
        Button exit = new Button("Exit");
        exit.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            stage.setScene(titleScene);
            stage.centerOnScreen();
            gameInProgress = false;
        });
        HBox buttons = new HBox();
        buttons.setSpacing(20);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(newGame, saveGame, loadGame, settings, exit);
        BorderPane gameBorder = new BorderPane();
        gameBorder.setTop(turnText);
        gameBorder.setLeft(leftBox);
        gameBorder.setRight(rightBox);
        BorderPane.setAlignment(turnText, Pos.CENTER);
        gameBorder.setCenter(root);
        gameBorder.setBottom(buttons);
        return gameBorder;
    }
    
    public Parent settings() {
        Label title = new Label("Settings");
        title.setId("title");
        title.setPadding(new Insets(10));
        Label zoomLabel = new Label("Zoom Exponent:");
        Spinner zoomSpinner = new Spinner();
        zoomSpinner.setEditable(true);
        zoomSpinner.getEditor().setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
        zoomSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 2, 1, 0.1));
        HBox zoomBox = new HBox();
        zoomBox.setSpacing(10);
        zoomBox.getChildren().addAll(zoomLabel, zoomSpinner);
        Label widthLabel = new Label("Viewport Width:");
        Spinner widthSpinner = new Spinner();
        widthSpinner.setEditable(true);
        widthSpinner.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, (int) Screen.getPrimary().getVisualBounds().getWidth(), 640, 10));
        widthSpinner.setDisable(true);
        HBox widthBox = new HBox();
        widthBox.setSpacing(10);
        widthBox.getChildren().addAll(widthLabel, widthSpinner);
        Label heightLabel = new Label("Viewport Height:");
        Spinner heightSpinner = new Spinner();
        heightSpinner.setEditable(true);
        heightSpinner.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, (int) Screen.getPrimary().getVisualBounds().getHeight(), 480, 10));
        heightSpinner.setDisable(true);
        HBox heightBox = new HBox();
        heightBox.setSpacing(10);
        heightBox.getChildren().addAll(heightLabel, heightSpinner);
        Label audioLabel = new Label("Enable Audio:");
        CheckBox audioCheck = new CheckBox();
        audioCheck.setSelected(true);
        HBox audioBox = new HBox();
        audioBox.setSpacing(10);
        audioBox.getChildren().addAll(audioLabel, audioCheck);
        Label fullscreenLabel = new Label("Fullscreen Mode:");
        CheckBox fullscreenCheck = new CheckBox();
        fullscreenCheck.setOnAction(e -> {
            if (fullscreenCheck.isSelected()) {
                widthSpinner.setDisable(true);
                heightSpinner.setDisable(true);
            } else {
                widthSpinner.setDisable(false);
                heightSpinner.setDisable(false);
            }
        });
        fullscreenCheck.setSelected(true);
        HBox fullscreenBox = new HBox();
        fullscreenBox.setSpacing(10);
        fullscreenBox.getChildren().addAll(fullscreenLabel, fullscreenCheck);
        VBox settings = new VBox();
        settings.setSpacing(20);
        settings.setPadding(new Insets(10));
        settings.setAlignment(Pos.CENTER);
        settings.getChildren().addAll(audioBox, fullscreenBox, widthBox, heightBox, zoomBox);
        Button ok = new Button("OK");
        ok.setOnAction(e -> {
            audio = audioCheck.isSelected();
            playSound("/resources/sound/click.wav");
            fullscreen = fullscreenCheck.isSelected();
            widthSpinner.increment(0);
            heightSpinner.increment(0);
            zoomSpinner.increment(0);
            if (!fullscreen) {
                scroll.setPrefWidth((int) widthSpinner.getValue());
                scroll.setPrefHeight((int) heightSpinner.getValue());
            }
            zoomExp = (double) zoomSpinner.getValue();
            if (gameInProgress) {
                stage.setScene(gameScene);
                Platform.runLater(() -> {
                    stage.setFullScreen(fullscreen);
                });
            } else {
                stage.setScene(titleScene);
            }
            stage.centerOnScreen();
        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction(e -> {
            playSound("/resources/sound/click.wav");
            if (gameInProgress) {
                stage.setScene(gameScene);
                Platform.runLater(() -> {
                    stage.setFullScreen(fullscreen);
                });
            } else {
                stage.setScene(titleScene);
            }
            stage.centerOnScreen();
        });
        HBox buttons = new HBox();
        buttons.setSpacing(20);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(ok, cancel);
        BorderPane border = new BorderPane();
        border.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        border.setCenter(settings);
        border.setBottom(buttons);
        Group group = new Group();
        group.getChildren().add(border);
        return group;
    }
    
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        titleScene = new Scene(title());
        titleScene.setFill(Color.GRAY);
        titleScene.getStylesheets().add("/resources/theme.css");
        setupScene = new Scene(setup());
        setupScene.setFill(Color.GRAY);
        setupScene.getStylesheets().add("/resources/theme.css");
        instructionScene = new Scene(instructions());
        instructionScene.setFill(Color.GRAY);
        instructionScene.getStylesheets().add("/resources/theme.css");
        gameScene = new Scene(game());
        gameScene.setFill(Color.GRAY);
        gameScene.getStylesheets().add("/resources/theme.css");
        settingScene = new Scene(settings());
        settingScene.setFill(Color.GRAY);
        settingScene.getStylesheets().add("/resources/theme.css");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/resources/image/icon.png"));
        stage.setTitle("Plexus");
        stage.setScene(titleScene);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
        stage.centerOnScreen();
        error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(null);
        error.setContentText("An error occurred.");
        error.initOwner(stage);
        gameInProgress = false;
        audio = true;
        fullscreen = true;
        zoomExp = 1.0;
        zoom = 0;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void serialize(File file) throws Exception {
        try (FileOutputStream fOut = new FileOutputStream(file); ObjectOutputStream oOut = new ObjectOutputStream(fOut)){
            oOut.writeObject(plexus);
            oOut.writeObject(mode);
            oOut.writeObject(players);
            oOut.writeObject(turn);
            oOut.writeObject(start);
        }
    }
    
    public static void deserialize(File file) throws Exception {
        try (FileInputStream fIn = new FileInputStream(file); ObjectInputStream oIn = new ObjectInputStream(fIn)) {
            plexus = (ArrayList<Node>) oIn.readObject();
            mode = (String) oIn.readObject();
            players = (int) oIn.readObject();
            turn = (int) oIn.readObject();
            start = (boolean) oIn.readObject();
            score1 = 0;
            score2 = 0;
            score3 = 0;
            score4 = 0;
        }
    }
    
    public static void initPlexus(int nodes, double density) {
        plexus = new ArrayList<>();
        for (int i = 0; i < nodes; i++) {
            Node node = null;
            while (node == null || tooClose(node, 30 / density)) {
                node = new Node(randDouble(0, 100 * Math.sqrt(nodes) / density), randDouble(0, 75 * Math.sqrt(nodes) / density));
            }
            plexus.add(node);
        }
        wire(plexus.get(0));
        plexus.stream().forEach(n1 -> {
            plexus.stream().forEach(n2 -> {
                if (n1 != n2) {
                    if (Node.distance(n1, n2) <= 100) {
                        Node.connect(n1, n2);
                    } else if (Node.distance(n1, n2) <= 200 && randInt(1, (int) (2 / density)) == 1) {
                        Node.connect(n1, n2);
                    } else if (Node.distance(n1, n2) <= 300 && randInt(1, (int) (10 / density)) == 1) {
                        Node.connect(n1, n2);
                    }
                }
            });
        });
        turn = 0;
        score1 = 0;
        score2 = 0;
        score3 = 0;
        score4 = 0;
        start = true;
    }
    
    public static void initMap() {
        circles = new ArrayList<>();
        lines = new ArrayList<>();
        plexus.stream().forEach(n -> {
            Circle circ = new Circle(10, Color.GRAY);
            circ.setLayoutX(n.getX());
            circ.setLayoutY(n.getY());
            circ.setEffect(new DropShadow(10, Color.WHITE));
            circ.setOnMouseClicked(e -> {
                if (start) {
                    if (n.getOwner() == 0 && !n.getConnections().stream().anyMatch(c -> (c.getOwner() != 0))) {
                        if (turn == 1 || mode.equals("multi")) {
                            playSound("/resources/sound/pop.wav");
                        }
                        n.setOwner(turn);
                        if (turn == players) {
                            turn++;
                            start = false;
                        }
                        refreshMap();
                        nextTurn();
                    }
                } else if (n.getOwner() == 0 && n.getConnections().stream().anyMatch(c -> (c.getOwner() == turn))) {
                    if (turn == 1 || mode.equals("multi")) {
                        playSound("/resources/sound/pop.wav");
                    }
                    n.setOwner(turn);
                    refreshMap();
                    nextTurn();
                }
            });
            circles.add(circ);
            n.getConnections().stream().forEach(c -> {
                Line line = new Line(n.getX(), n.getY(), c.getX(), c.getY());
                line.setStroke(Color.GRAY);
                line.setEffect(new DropShadow(5, Color.WHITE));
                if (!lines.stream().anyMatch(l -> (l.getStartX() == line.getEndX() && l.getStartY() == line.getEndY() && line.getStartX() == l.getEndX() && line.getStartY() == l.getEndY()))) {
                    lines.add(line);
                }
            });
        });
        map.getChildren().clear();
        lines.stream().forEach(l -> {
            map.getChildren().add(l);
        });
        circles.stream().forEach(c -> {
            map.getChildren().add(c);
        });
        switch (players) {
            case 1:
                box1.setVisible(true);
                box2.setVisible(false);
                box3.setVisible(false);
                box4.setVisible(false);
                break;
            case 2:
                box1.setVisible(true);
                box2.setVisible(true);
                box3.setVisible(false);
                box4.setVisible(false);
                break;
            case 3:
                box1.setVisible(true);
                box2.setVisible(true);
                box3.setVisible(true);
                box4.setVisible(false);
                break;
            case 4:
                box1.setVisible(true);
                box2.setVisible(true);
                box3.setVisible(true);
                box4.setVisible(true);
                break;
            default:
                break;
        }
        turnText.setText("Player " + turn + "'s Turn");
        switch (turn) {
            case 1:
                turnText.setStyle("-fx-text-fill: red");
                break;
            case 2:
                turnText.setStyle("-fx-text-fill: blue");
                break;
            case 3:
                turnText.setStyle("-fx-text-fill: yellow");
                break;
            case 4:
                turnText.setStyle("-fx-text-fill: green");
            default:
                break;
        }
        number1.setText(Integer.toString(score1));
        number2.setText(Integer.toString(score2));
        number3.setText(Integer.toString(score3));
        number4.setText(Integer.toString(score4));
        scroll.setStyle("");
    }
    
    public static void refreshMap() {
        circles.stream().forEach(c -> {
            switch (nodeAt(c.getLayoutX(), c.getLayoutY()).getOwner()) {
                case 0:
                    c.setFill(Color.GRAY);
                    c.setEffect(new DropShadow(10, Color.WHITE));
                    break;
                case 1:
                    c.setFill(Color.RED);
                    c.setEffect(new DropShadow(10, Color.RED));
                    break;
                case 2:
                    c.setFill(Color.BLUE);
                    c.setEffect(new DropShadow(10, Color.BLUE));
                    break;
                case 3:
                    c.setFill(Color.YELLOW);
                    c.setEffect(new DropShadow(10, Color.YELLOW));
                    break;
                case 4:
                    c.setFill(Color.GREEN);
                    c.setEffect(new DropShadow(10, Color.GREEN));
                default:
                    break;
            }
        });
        lines.stream().forEach(l -> {
            Node n1 = nodeAt(l.getStartX(), l.getStartY());
            Node n2 = nodeAt(l.getEndX(), l.getEndY());
            if (n1.getOwner() == n2.getOwner()) {
                switch (n1.getOwner()) {
                    case 0:
                        l.setStroke(Color.GRAY);
                        l.setEffect(new DropShadow(5, Color.WHITE));
                        break;
                    case 1:
                        if (!l.getStroke().equals(Color.RED)) {
                            score1++;
                        }
                        l.setStroke(Color.RED);
                        l.setEffect(new DropShadow(5, Color.RED));
                        break;
                    case 2:
                        if (!l.getStroke().equals(Color.BLUE)) {
                            score2++;
                        }
                        l.setStroke(Color.BLUE);
                        l.setEffect(new DropShadow(5, Color.BLUE));
                        break;
                    case 3:
                        if (!l.getStroke().equals(Color.YELLOW)) {
                            score3++;
                        }
                        l.setStroke(Color.YELLOW);
                        l.setEffect(new DropShadow(5, Color.YELLOW));
                        break;
                    case 4:
                        if (!l.getStroke().equals(Color.GREEN)) {
                            score4++;
                        }
                        l.setStroke(Color.GREEN);
                        l.setEffect(new DropShadow(5, Color.GREEN));
                        break;
                    default:
                        break;
                }
            }
        });
        number1.setText(Integer.toString(score1));
        number2.setText(Integer.toString(score2));
        number3.setText(Integer.toString(score3));
        number4.setText(Integer.toString(score4));
    }
    
    public static void nextTurn() {
        if (start) {
            turn++;
        } else {
            turn--;
        }
        if (turn < 1) {
            turn = players;
        }
        if (gameover()) {
            int winner = 0;
            if (score1 > score2 && score1 > score3 && score1 > score4) {
                winner = 1;
            } else if (score2 > score1 && score2 > score3 && score2 > score4) {
                winner = 2;
            } else if (score3 > score1 && score3 > score2 && score3 > score4) {
                winner = 3;
            } else if (score4 > score1 && score4 > score2 && score4 > score3) {
                winner = 4;
            }
            if (winner != 0) {
                turnText.setText("Player " + winner + " Wins!");
            }
            switch (winner) {
                case 1:
                    turnText.setStyle("-fx-text-fill: red");
                    scroll.setStyle("-fx-background: lightcoral");
                    break;
                case 2:
                    turnText.setStyle("-fx-text-fill: blue");
                    scroll.setStyle("-fx-background: lightblue");
                    break;
                case 3:
                    turnText.setStyle("-fx-text-fill: yellow");
                    scroll.setStyle("-fx-background: lightyellow");
                    break;
                case 4:
                    turnText.setStyle("-fx-text-fill: green");
                    scroll.setStyle("-fx-background: lightgreen");
                    break;
                default:
                    turnText.setText("Tie Game");
                    turnText.setStyle("");
                    break;
            }
            if (mode.equals("single")) {
                switch (winner) {
                    case 0:
                        playSound("/resources/sound/tie.wav");
                        break;
                    case 1:
                        playSound("/resources/sound/win.wav");
                        turnText.setText("You Win!");
                        break;
                    default:
                        playSound("/resources/sound/lose.wav");
                        turnText.setText("You Lose!");
                        break;
                }
            } else {
                if (winner == 0) {
                    playSound("/resources/sound/tie.wav");
                } else {
                    playSound("/resources/sound/win.wav");
                }
            }
        } else {
            turnText.setText("Player " + turn + "'s Turn");
            switch (turn) {
                case 1:
                    turnText.setStyle("-fx-text-fill: red");
                    break;
                case 2:
                    turnText.setStyle("-fx-text-fill: blue");
                    break;
                case 3:
                    turnText.setStyle("-fx-text-fill: yellow");
                    break;
                case 4:
                    turnText.setStyle("-fx-text-fill: green");
                    break;
                default:
                    break;
            }
            if (mode.equals("single")) {
                turnText.setText("You Have " + score1 + " Point" + ((score1 == 1) ? "" : "s"));
                if (turn != 1) {
                    Node best = bestNode();
                    Circle c = circleAt(best.getX(), best.getY());
                    c.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
                }
            }
        }
    }
    
    public static void playSound(String path) {
        if (audio) {
            new AudioClip(GUI.class.getResource(path).toString()).play();
        }
    }
    
    private static void wire(Node node) {
        Node n = closestUnconnectedNode(node);
        if (n != null) {
            Node.connect(node, n);
            wire(n);
        }
    }
    
    private static Node bestNode() {
        ArrayList<Node> best = new ArrayList<>();
        int connections = 0;
        for (Node n : plexus) {
            if (start) {
                if (n.getOwner() == 0 && !n.getConnections().stream().anyMatch(c -> (c.getOwner() != 0)) && n.getConnections().size() >= connections) {
                    if (n.getConnections().size() > connections) {
                        best.clear();
                        connections = n.getConnections().size();
                    }
                    best.add(n);
                }
            } else if (n.getOwner() == 0 && n.getConnections().stream().anyMatch(c -> (c.getOwner() == turn)) && n.getConnections().size() >= connections) {
                if (n.getConnections().size() > connections) {
                    best.clear();
                    connections = n.getConnections().size();
                }
                best.add(n);
            }
        }
        if (best.size() > 0) {
            return best.get(randInt(0, best.size() - 1));
        } else {
            return null;
        }
    }
    
    private static Node nodeAt(double x, double y) {
        for (Node n : plexus) {
            if (x == n.getX() && y == n.getY()) {
                return n;
            }
        }
        return null;
    }
    
    private static Circle circleAt(double x, double y) {
        for (Circle c : circles) {
            if (x == c.getLayoutX() && y == c.getLayoutY()) {
                return c;
            }
        }
        return null;
    }
    
    private static Node closestUnconnectedNode(Node node) {
        double distance = Double.MAX_VALUE;
        Node closest = null;
        for (Node n : plexus) {
            double d;
            if (n != node && n.getConnections().isEmpty() && (d = Node.distance(node, n)) < distance) {
                distance = d;
                closest = n;
            }
        }
        return closest;
    }
    
    private static boolean tooClose(Node node, double distance) {
        return plexus.stream().anyMatch(n -> (Node.distance(node, n) < distance));
    }
    
    private static boolean gameover() {
        return start == false && !plexus.stream().filter(n -> (n.getOwner() == turn)).anyMatch(m -> (m.getConnections().stream().anyMatch(c -> (c.getOwner() == 0))));
    }
    
    private static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    private static double randDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + Double.MIN_VALUE);
    }
    
    public static final String VERSION = "0.0.3";
    private static final File SAVES = new File("saves");
    private static ArrayList<Node> plexus;
    private static int players;
    private static int turn;
    private static int score1, score2, score3, score4;
    private static boolean start;
    private static boolean gameInProgress;
    private static boolean audio;
    private static boolean fullscreen;
    private static Group map;
    private static ArrayList<Circle> circles;
    private static ArrayList<Line> lines;
    private static ScrollPane scroll;
    private static Label turnText, number1, number2, number3, number4;
    private static VBox box1, box2, box3, box4;
    private static Alert error;
    private static StackPane root;
    private static double zoomExp, zoom;
    private static String mode;
    private Stage stage;
    private Scene titleScene, setupScene, instructionScene, gameScene, settingScene;
}
