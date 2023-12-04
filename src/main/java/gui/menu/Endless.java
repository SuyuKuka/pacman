package gui.menu;

import java.util.ArrayList;
import java.util.List;

import config.Cell;
import config.MazeConfig;
import config.mazeGen.MainGrid;
import gui.GameView;
import gui.InGameGui;
import gui.controller.Controller;
import gui.controller.PacmanController;
import gui.controller.PlayerTwoController;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DirectionBuffer;
import model.Ghost;
import model.Ghost.GhostType;
import model.MazeState;
import utils.Music;

public final class Endless extends Menu {
    private ArrayList<Text> mainTexts;
    private ArrayList<Text> subTexts;
    private static int mazeWidth;
    private static int mazeHeight;
    private static String selectedMode;
    private static Cell[][] currentMap;

    public static final Endless INSTANCE = new Endless();

    private Endless() {
    }

    @Override
    public void start(final Stage primaryStage) {
    }

    /**
     * Affiche le menu Endless.
     */
    public void toEndless() {
        setScene();
        getMainStage().setScene(getMenuScene());
    }

    @Override
    protected void addOptionsToMenu() {
        final int titleSize = 100;
        final int spacingSize = 20;

        setTitleText(createText("Endless", titleSize));
        HBox title = createHBox(0, Pos.TOP_CENTER, getTitleText());

        Text selectStart = createText(">", getFontSize().doubleValue());
        Text startText = createText("Start", getFontSize().doubleValue());

        Text mode = createText("Mode", getFontSize().doubleValue());
        String[] modes = {"Solo", "Versus", "Coop"};

        ChoiceBox<String> modeChoice = new ChoiceBox<>();
        modeChoice.getItems().addAll(modes);
        modeChoice.setValue(modes[0]);
        setSelectedMode(modes[0]);
        modeChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            setSelectedMode(newValue);
        });
        VBox modeBox = createVBox(spacingSize, Pos.CENTER, mode, modeChoice);

        Text size = createText("Size", getFontSize().doubleValue());
        Slider widthSlider = new Slider(
            MainGrid.getMinMaxLongueur()[0],
            MainGrid.getMinMaxLongueur()[1],
            (MainGrid.getMinMaxLongueur()[0] + MainGrid.getMinMaxLongueur()[1]) / 2.0
        );
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setMazeWidth(newValue.intValue());
        });
        //CHECKSTYLE:OFF
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(4);
        widthSlider.setMinorTickCount(1);
        widthSlider.setBlockIncrement(1);
        Text width = createText("Width", getFontSize().doubleValue());
        HBox widthBox = createHBox(spacingSize, Pos.CENTER, width, widthSlider);
        Slider heightSlider = new Slider(
            MainGrid.getMinMaxHauteur()[0],
            MainGrid.getMinMaxHauteur()[1],
            (MainGrid.getMinMaxHauteur()[0] + MainGrid.getMinMaxHauteur()[1]) / 2.0
        );
        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setMazeHeight(newValue.intValue());
        });
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(4);
        heightSlider.setMinorTickCount(1);
        heightSlider.setBlockIncrement(1);
        //CHECKSTYLE:ON
        Text height = createText("Height", getFontSize().doubleValue());
        HBox heightBox = createHBox(spacingSize, Pos.CENTER, height, heightSlider);
        VBox sizeBox = createVBox(spacingSize, Pos.CENTER, size, widthBox, heightBox);

        VBox settings = createVBox(spacingSize, Pos.CENTER, modeBox, sizeBox);

        Text selectBack = createText(">", getFontSize().doubleValue());
        Text backText = createText("Back", getFontSize().doubleValue());

        //CHECKSTYLE:OFF
        if (getOptions().isEmpty()) {
            getOptions().add(createHBox(spacingSize, Pos.CENTER, selectStart, startText));
            getOptions().add(createHBox(spacingSize, Pos.BOTTOM_CENTER, settings));
            getOptions().add(createHBox(spacingSize, Pos.BOTTOM_LEFT, selectBack, backText));
        }

        VBox optionBox = createVBox(
            spacingSize, Pos.CENTER,
            getOptions().get(0), getOptions().get(1)
        );

        if (getSelects().isEmpty() && getChoices().isEmpty()) {
            getSelects().add(selectStart);
            getSelects().add(selectBack);

            getChoices().add(startText);
            getChoices().add(backText);
        }

        mainTexts = new ArrayList<>();
        subTexts = new ArrayList<>();
        mainTexts.add(mode);
        mainTexts.add(size);
        subTexts.add(width);
        subTexts.add(height);

        bindTextSizeToScene();

        getMenuPane().setTop(title);
        getMenuPane().setCenter(optionBox);
        getMenuPane().setBottom(getOptions().get(2));
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(getSelects(), getChoices(), gameScene, false);

        getOptions().get(0).setOnMouseClicked(event -> {
            startLevel(selectedMode, mazeWidth, mazeHeight);
            selector.getKeySelector().resetSelect();
        });
        getOptions().get(2).setOnMouseClicked(event -> {
            MainMenu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            final int back = 1;
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Selector.getSelectedIndex() == back) {
                    MainMenu.INSTANCE.toMenu();
                } else {
                    startLevel(selectedMode, mazeWidth, mazeHeight);
                }
                selector.getKeySelector().resetSelect();
            }
        });
    }

    /**
     * Méthode permettant de lancer un niveau généré aléatoirement,
     * le mode (Solo / Versus / Coop) dépend de{@code mode}.
     * @param mode
     * @param width
     * @param height
     */
    public static void startLevel(final String mode, final int width, final int height) {
        Music.stopBackgroundMusic();

        HBox box = new HBox();
        StackPane root = new StackPane(box);

        Scene gameScene = new Scene(
            root,
            getMainStage().getScene().getWidth(),
            getMainStage().getScene().getHeight()
        );

        boolean solo = mode.equals("Solo");
        boolean versus = mode.equals("Versus");
        boolean coop = mode.equals("Coop");

        MazeState maze = new MazeState(MazeConfig.endlessConfig(coop, width, height));

        getMainStage().setScene(gameScene);
        InGameGui inGameGui = new InGameGui(gameScene, solo ? "Solo" : "Multi", true);

        Controller.setInGameGui(inGameGui);
        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController::keyPressedHandler);

        if (versus) {
            List<Ghost> blinkys = maze.getConfig().getGhostKind(GhostType.BLINKY);
            Ghost playerGhost = blinkys.get(0);
            playerGhost.setDirectionBuffer(new DirectionBuffer());
            var ghostController = new PlayerTwoController(playerGhost);

            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, ghostController::keyPressedHandler);
        } else if (coop) {
            var pacmanController2 = new PlayerTwoController(maze.getConfig().getPacman2());
            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController2::keyPressedHandler);
        }

        GameView.INSTANCE.setGameView(maze, inGameGui, box);
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.animate();
    }

    /**
     * Recommence un niveau en Endless.
     * @param mode
     */
    public void restartLevel(final String mode) {
        Music.stopBackgroundMusic();

        HBox box = new HBox();
        StackPane root = new StackPane(box);

        Scene gameScene = new Scene(
            root,
            getMainStage().getScene().getWidth(),
            getMainStage().getScene().getHeight()
        );

        boolean solo = mode.equals("Solo");
        boolean versus = mode.equals("Versus");
        boolean coop = mode.equals("Coop");

        MazeState maze = new MazeState(MazeConfig.restartedEndlessConfig(coop));

        getMainStage().setScene(gameScene);
        InGameGui inGameGui = new InGameGui(gameScene, solo ? "Solo" : "Multi", true);

        Controller.setInGameGui(inGameGui);
        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController::keyPressedHandler);

        if (versus) {
            List<Ghost> blinkys = maze.getConfig().getGhostKind(GhostType.BLINKY);
            Ghost playerGhost = blinkys.get(0);
            playerGhost.setDirectionBuffer(new DirectionBuffer());
            var ghostController = new PlayerTwoController(playerGhost);

            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, ghostController::keyPressedHandler);
        } else if (coop) {
            var pacmanController2 = new PlayerTwoController(maze.getConfig().getPacman2());
            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController2::keyPressedHandler);
        }

        GameView.INSTANCE.setGameView(maze, inGameGui, box);
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.animate();

    }

    @Override
    protected void bindTextSizeToScene() {
        super.bindTextSizeToScene();

        for (Text text : mainTexts) {
            text.styleProperty().bind(Bindings.concat("-fx-font-size: ",
            getFontSize().asString()));
        }
        for (Text text : subTexts) {
            text.styleProperty().bind(Bindings.concat("-fx-font-size: ",
            getFontSize().divide(2).asString()));
        }
    }

    public static Cell[][] getCurrentMap() {
        return currentMap;
    }

    public static void setCurrentMap(final Cell[][] currentMap) {
        Endless.currentMap = currentMap;
    }

    public static void setMazeWidth(final int mazeWidth) {
        Endless.mazeWidth = mazeWidth;
    }

    public static void setMazeHeight(final int mazeHeight) {
        Endless.mazeHeight = mazeHeight;
    }

    public static void setSelectedMode(final String selectedMode) {
        Endless.selectedMode = selectedMode;
    }

    public static String getSelectedMode() {
        return selectedMode;
    }
}
