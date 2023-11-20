package gui.menu;

import java.util.ArrayList;
import java.util.List;

import config.MazeConfig;
import gui.GameView;
import gui.InGameGui;
import gui.controller.Controller;
import gui.controller.GhostController;
import gui.controller.PacmanController;
import gui.controller.PacmanController2;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
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
import model.DirectionBuffer;
import model.Ghost;
import model.MazeState;
import model.Ghost.GhostType;
import utils.Music;

public final class Multiplayer extends Menu {
    /** Initialise un rootPane pour Multiplayer. */
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

    /**
     * Méthode permettant de lancer un niveau, le niveau lancé dépend de
     * la variable statique {@code selected} de la classe {@code Menu}.
     * @param primaryStage
     * @param mode
     */
    public static void startLevel(final Stage primaryStage, final int mode) {
        InGameGui.setCurrentLevel(mode);
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
        final int versus = 0;
        final int coop = 1;
        switch (mode) {
            case versus:
                maze = new MazeState(MazeConfig.classicalConfig(true, false));
                break;
            case coop:
                maze = new MazeState(MazeConfig.classicalConfig(false, true));
                break;

            default:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;
        }

        primaryStage.setScene(gameScene);
        InGameGui inGameGui = new InGameGui(primaryStage, gameScene, "Multi");

        Controller.setInGameGui(inGameGui);
        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController::keyPressedHandler);
        gameScene.addEventHandler(KeyEvent.KEY_RELEASED, pacmanController::keyReleasedHandler);

        if (mode == versus) {
            List<Ghost> blinkys = maze.getConfig().getGhostKind(GhostType.BLINKY);
            Ghost playerGhost = blinkys.get(0);
            playerGhost.setDirectionBuffer(new DirectionBuffer());
            var playerGhostController = new GhostController(playerGhost);

            gameScene.addEventHandler(
                KeyEvent.KEY_PRESSED, playerGhostController::keyPressedHandler
            );
            gameScene.addEventHandler(
                KeyEvent.KEY_RELEASED, playerGhostController::keyReleasedHandler
            );
        } else if (mode == coop) {
            var pacmanController2 = new PacmanController2(maze.getConfig().getPacman2());
            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController2::keyPressedHandler);
            gameScene.addEventHandler(KeyEvent.KEY_RELEASED, pacmanController2::keyReleasedHandler);
        }

        GameView.INSTANCE.setGameView(maze, inGameGui, pane);
        GameView.INSTANCE.stopAnimation();
        primaryStage.show();
        GameView.INSTANCE.animate();
    }

    @Override
    protected void addMainBox(final Scene gameScene) {
        final int titleSize = 100;
        final int spacingSize = 20;

        titleText = createText("Game Selection", titleSize);
        HBox title = createHBox(0, Pos.TOP_CENTER, titleText);

        Text select0 = createText(">", fontSize.getValue().intValue());
        Text versus = createText("Versus", fontSize.getValue().intValue());

        Text select1 = createText(">", fontSize.getValue().intValue());
        Text coop = createText("Coop", fontSize.getValue().intValue());

        Text selectBack = createText(">", fontSize.getValue().intValue());
        Text back = createText("Back", fontSize.getValue().intValue());

        options.add(0, createHBox(
            spacingSize, Pos.CENTER, select0, versus
        ));
        options.add(1, createHBox(
            spacingSize, Pos.CENTER, select1, coop
        ));
        options.add(2, createHBox(
            spacingSize, Pos.BOTTOM_LEFT, selectBack, back
        ));

        VBox optionBox = createVBox(
            spacingSize, Pos.CENTER, options.get(0), options.get(1)
        );

        selects.add(0, select0);
        selects.add(1, select1);
        selects.add(2, selectBack);

        choices.add(0, versus);
        choices.add(1, coop);
        choices.add(2, back);

        bindTextSizeToScene(gameScene);

        rootPane.setTop(title);
        rootPane.setCenter(optionBox);
        rootPane.setBottom(options.get(2));
        //CHECKSTYLE:OFF
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Stage primaryStage, final Scene gameScene) {
        EventHandler<MouseEvent> mouseClick0 = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                options.get(0).removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                startLevel(primaryStage, 0);
            }
        };
        EventHandler<MouseEvent> mouseClick1 = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                options.get(1).removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                startLevel(primaryStage, 1);
            }
        };

        options.get(0).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick0);
        // TODO : Niveau coop
        options.get(1).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick1);
        options.get(2).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });

        EventHandler<KeyEvent> enterPressed = new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    gameScene.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    if (Menu.getSelected() == 2) {
                        Menu menu = new Menu();
                        menu.start(primaryStage);
                    } else {
                        startLevel(primaryStage, Menu.getSelected());
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
}
