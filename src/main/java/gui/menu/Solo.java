package gui.menu;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.EventHandler;

import config.MazeConfig;
import gui.GameView;
import gui.InGameGui;
import gui.PacmanController;
import model.MazeState;
import utils.Music;

public final class Solo extends Menu {
    /** Initialise un rootPane pour Solo. */
    private StackPane rootPane = new StackPane();

    @Override
    public void start(final Stage primaryStage) {
        final int mainBoxSpacing = 45;
        final int sceneWidth = 16;
        final int sceneHeight = 39;

        setMainBoxAutoSpacing(mainBoxSpacing);

        Scene gameScene = new Scene(
            rootPane,
            primaryStage.getWidth() - sceneWidth,
            primaryStage.getHeight() - sceneHeight
        );

        resizeAndShowMenu(primaryStage, gameScene);

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    /**
     * Méthode permettant de lancer un niveau, le niveau lancé dépend de
     * la variable statique {@code selected} de la classe {@code Menu}.
     * @param primaryStage
     * @param level
     */
    public static void startLevel(final Stage primaryStage, final int level) {
        InGameGui.INSTANCE.setCurrentLevel(level);

        final int scale = 17;
        final int missWidth = 125;
        final int missHeight = 145;
        Music.stopAllSounds();

        Pane pane = new Pane();

        Scene gameScene = new Scene(
            pane,
            primaryStage.getMinWidth() + missWidth,
            primaryStage.getMinHeight() + missHeight
        );

        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        MazeState maze;
        final int classique = 0;
        final int facile = 1;
        final int moyen = 2;
        final int difficile = 3;
        final int impossible = 4;
        switch (level) {
            case classique:
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case facile: // TODO : Autres niveaux à faire
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case moyen:
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case difficile:
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case impossible:
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            default:
                maze = new MazeState(MazeConfig.makeExample1());
                break;
        }

        primaryStage.setScene(gameScene);
        InGameGui.setInGameGui(primaryStage, gameScene);

        GameView.INSTANCE.setGameView(maze, pane, scale);
        GameView.INSTANCE.stopAnimation();
        primaryStage.show();
        GameView.INSTANCE.animate();
    }

    @Override
    protected void resizeAndShowMenu(
        final Stage primaryStage, final Scene gameScene
        ) {
        rootPane.getChildren().clear();
        rootPane.setBackground(getBackground(primaryStage));
        resizeText(primaryStage);
        addMainBox(primaryStage, gameScene);

        ChangeListener<Number> resizeListener =
        (observable, oldValue, newValue) -> {
            rootPane.getChildren().clear();
            rootPane.setBackground(getBackground(primaryStage));
            resizeText(primaryStage);
            addMainBox(primaryStage, gameScene);
        };

        primaryStage.widthProperty().addListener(resizeListener);
        primaryStage.heightProperty().addListener(resizeListener);
    }

    @Override
    protected void resizeText(final Stage primaryStage) {
        rootPane.getChildren().clear();
        final double variationMainBoxSpacing = 4;
        setResizeVar(primaryStage, variationMainBoxSpacing);
    }

    @Override
    protected void addMainBox(final Stage primaryStage, final Scene gameScene) {
        final int textAutoSpacing = 20;
        final int subBoxAutoSpacing = 40;
        final int optionAutoSpacing = textAutoSpacing;

        Text levelSelector = createText("Level Selector", getTitleAutoSize());
        HBox title = createHBox(0, Pos.CENTER, levelSelector);

        Text select1 = createText(">", getTextAutoSize());
        Text classique = createText("Classique", getTextAutoSize());
        HBox option1 = createHBox(
            textAutoSpacing, Pos.CENTER, select1, classique
        );

        Text select2 = createText(">", getTextAutoSize());
        Text facile = createText("Facile", getTextAutoSize());
        HBox option2 = createHBox(textAutoSpacing, Pos.CENTER, select2, facile);

        Text select3 = createText(">", getTextAutoSize());
        Text moyen = createText("Moyen", getTextAutoSize());
        HBox option3 = createHBox(textAutoSpacing, Pos.CENTER, select3, moyen);

        Text select4 = createText(">", getTextAutoSize());
        Text difficile = createText("Difficile", getTextAutoSize());
        HBox option4 = createHBox(
            textAutoSpacing, Pos.CENTER, select4, difficile
        );

        Text select5 = createText(">", getTextAutoSize());
        Text impossible = createText("Impossible", getTextAutoSize());
        HBox option5 = createHBox(
            textAutoSpacing, Pos.CENTER, select5, impossible
        );

        Text selectBack = createText(">", getTextAutoSize());
        Text backText = createText("Back", getTextAutoSize());
        HBox optionBack = createHBox(
            textAutoSpacing, Pos.BOTTOM_LEFT, selectBack, backText
        );

        VBox optionBox = createVBox(
            optionAutoSpacing, Pos.CENTER,
            option1, option2, option3, option4, option5
        );
        VBox subBox = createVBox(
            subBoxAutoSpacing, Pos.CENTER, title, optionBox
        );
        VBox mainBox = createVBox(
            0, Pos.CENTER, subBox, optionBack
        );

        rootPane.getChildren().add(mainBox);

        VBox.setVgrow(subBox, Priority.ALWAYS);

        final int marginSub = 50;
        final int marginBack = 10;

        HBox.setMargin(subBox, new Insets(0, 0, marginSub, 0));

        HBox.setMargin(subBox, new Insets(0, 0, 0, marginBack));


        Text[] choices = {
            select1, select2, select3, select4, select5, selectBack
        };
        Text[] options = {
            classique, facile, moyen, difficile, impossible, backText
        };

        addEventHandlers(primaryStage, gameScene, choices, options);
    }

    @Override
    protected void addEventHandlers(
        final Stage primaryStage, final Scene gameScene,
        final Text[] choices, final Text[] options
        ) {
        //CHECKSTYLE:OFF
        options[0].addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 0)
        );
        options[1].addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 1)
        );
        options[2].addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 2)
        );
        options[3].addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 3)
        );
        options[4].addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 4)
        );
        options[5].addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> {
                Menu menu = new Menu();
                menu.start(primaryStage);
            }
        );
        //CHECKSTYLE:ON

        KeySelector keySel = new KeySelector(choices, primaryStage, false);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : options) {
            MouseSelector m = new MouseSelector(choices, select++);
            text.setOnMouseEntered(m::handle);
        }

        EventHandler<KeyEvent> enterPressed = new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent keyEvent) {
                final int back = 5;
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    gameScene.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    if (Menu.getSelected() == back) {
                        Menu menu = new Menu();
                        menu.start(primaryStage);
                    } else {
                        startLevel(primaryStage, getSelected());
                    }
                }
            }
        };
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, enterPressed);
    }
}
