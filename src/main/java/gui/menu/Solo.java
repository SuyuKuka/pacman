package gui.menu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.EventHandler;

import java.util.ArrayList;

import config.MazeConfig;
import gui.GameView;
import gui.InGameGui;
import gui.controller.Controller;
import gui.controller.PacmanController;
import model.MazeState;
import utils.Music;

public final class Solo extends Menu {
    /** Initialise un rootPane pour Solo. */
    private BorderPane rootPane = new BorderPane();

    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    @Override
    public void start(final Stage primaryStage) {
        Scene gameScene = new Scene(
            rootPane,
            primaryStage.getScene() == null
            ? primaryStage.getWidth() : primaryStage.getScene().getWidth(),
            primaryStage.getScene() == null
            ? primaryStage.getHeight() : primaryStage.getScene().getHeight()
        );
        rootPane.prefWidthProperty().bind(gameScene.widthProperty());
        rootPane.prefHeightProperty().bind(gameScene.heightProperty());
        rootPane.setBackground(getBackground(primaryStage));

        addMainBox(gameScene);
        addEventHandlers(primaryStage, gameScene);

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    @Override
    protected void addMainBox(final Scene gameScene) {
        final int titleSize = 100;
        final int spacingSize = 20;

        titleText = createText("Level Selector", titleSize);
        HBox title = createHBox(0, Pos.TOP_CENTER, titleText);

        Text select0 = createText(">", fontSize.getValue().intValue());
        Text classique = createText("Classique", fontSize.getValue().intValue());

        Text select1 = createText(">", fontSize.getValue().intValue());
        Text facile = createText("Facile", fontSize.getValue().intValue());

        Text select2 = createText(">", fontSize.getValue().intValue());
        Text moyen = createText("Moyen", fontSize.getValue().intValue());

        Text select3 = createText(">", fontSize.getValue().intValue());
        Text difficile = createText("Difficile", fontSize.getValue().intValue());

        Text select4 = createText(">", fontSize.getValue().intValue());
        Text impossible = createText("Impossible", fontSize.getValue().intValue());

        Text selectBack = createText(">", fontSize.getValue().intValue());
        Text backText = createText("Back", fontSize.getValue().intValue());

        //CHECKSTYLE:OFF
        options.add(0, createHBox(spacingSize, Pos.CENTER, select0, classique));
        options.add(1, createHBox(spacingSize, Pos.CENTER, select1, facile));
        options.add(2, createHBox(spacingSize, Pos.CENTER, select2, moyen));
        options.add(3, createHBox(spacingSize, Pos.CENTER, select3, difficile));
        options.add(4, createHBox(spacingSize, Pos.CENTER, select4, impossible));
        options.add(5, createHBox(spacingSize, Pos.BOTTOM_LEFT, selectBack, backText));

        VBox optionBox = createVBox(
            spacingSize, Pos.CENTER,
            options.get(0), options.get(1), options.get(2), options.get(3), options.get(4)
        );

        selects.add(0, select0);
        selects.add(1, select1);
        selects.add(2, select2);
        selects.add(3, select3);
        selects.add(4, select4);
        selects.add(5, selectBack);

        choices.add(0, classique);
        choices.add(1, facile);
        choices.add(2, moyen);
        choices.add(3, difficile);
        choices.add(4, impossible);
        choices.add(5, backText);

        bindTextSizeToScene(gameScene);

        rootPane.setTop(title);
        rootPane.setCenter(optionBox);
        rootPane.setBottom(options.get(5));
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Stage primaryStage, final Scene gameScene) {
        //CHECKSTYLE:OFF
        options.get(0).addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 0)
        );
        options.get(1).addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 1)
        );
        options.get(2).addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 2)
        );
        options.get(3).addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 3)
        );
        options.get(4).addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage, 4)
        );
        options.get(5).addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> {
                Menu menu = new Menu();
                menu.start(primaryStage);
            }
        );
        //CHECKSTYLE:ON

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

        KeySelector keySel = new KeySelector(selects, primaryStage, false);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : choices) {
            MouseSelector m = new MouseSelector(selects, select++);
            text.setOnMouseEntered(m::handle);
        }
    }

    @Override
    protected void bindTextSizeToScene(final Scene gameScene) {
        final int scale = 30;
        final int titleScale = 50;
        final String fxFontSize = "-fx-font-size: ";

        fontSize.bind(
            gameScene.widthProperty().divide(scale).add(gameScene.heightProperty().divide(scale))
        );

        titleText.styleProperty().bind(
            Bindings.concat(fxFontSize, fontSize.add(titleScale).asString())
        );
        for (Text text : choices) {
            text.styleProperty().bind(Bindings.concat(fxFontSize, fontSize.asString()));
        }
        for (Text text : selects) {
            text.styleProperty().bind(Bindings.concat(fxFontSize, fontSize.asString()));
        }
    }

    /**
     * Méthode permettant de lancer un niveau, le niveau lancé dépend de
     * la variable statique {@code selected} de la classe {@code Menu}.
     * @param primaryStage
     * @param level
     */
    public static void startLevel(final Stage primaryStage, final int level) {
        InGameGui.setCurrentLevel(level);
        Music.stopBackgroundMusic();

        Pane pane = new Pane();

        Scene gameScene = new Scene(
            pane,
            primaryStage.getScene() == null
            ? primaryStage.getWidth() : primaryStage.getScene().getWidth(),
            primaryStage.getScene() == null
            ? primaryStage.getHeight() : primaryStage.getScene().getHeight()
        );

        MazeState maze;
        final int classique = 0;
        final int facile = 1;
        final int moyen = 2;
        final int difficile = 3;
        final int impossible = 4;
        switch (level) {
            case classique:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;

            case facile: // TODO : Autres niveaux à faire
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;

            case moyen:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;

            case difficile:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;

            case impossible:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;

            default:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;
        }

        primaryStage.setScene(gameScene);
        InGameGui inGameGui = new InGameGui(primaryStage, gameScene, "Solo");

        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);
        Controller.setInGameGui(inGameGui);

        GameView.INSTANCE.setGameView(maze, inGameGui, pane);
        GameView.INSTANCE.stopAnimation();
        primaryStage.show();
        GameView.INSTANCE.animate();
    }

}
