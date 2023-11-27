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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private BorderPane multiPane;
    private Scene multiScene;
    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    public static final Multiplayer INSTANCE = new Multiplayer();

    private Multiplayer() {
    }

    @Override
    public void start(final Stage primaryStage) {
        multiPane = new BorderPane();
        multiPane.setBackground(getBackground());

        multiScene = new Scene(
            multiPane,
            primaryStage.getScene().getWidth(),
            primaryStage.getScene().getHeight()
        );
        multiScene.setFill(Color.BLACK);

        addMainBox();
        addEventHandlers(multiScene);
    }

    @Override
    public void setScene() {
        multiScene.setRoot(new HBox());
        multiScene = new Scene(
            multiPane,
            Menu.getMainStage().getScene().getWidth(),
            Menu.getMainStage().getScene().getHeight()
        );
        multiScene.setFill(Color.BLACK);

        addEventHandlers(multiScene);
    }

    /**
     * Affiche le menu Multiplayer.
     */
    public void toMulti() {
        setScene();
        Menu.getMainStage().setScene(multiScene);
    }

    /**
     * Méthode permettant de lancer un niveau, le niveau lancé dépend de
     * la variable statique {@code selected} de la classe {@code Menu}.
     * @param mode
     */
    public static void startLevel(final int mode) {
        InGameGui.setCurrentLevel(mode);
        Music.stopBackgroundMusic();

        StackPane root = new StackPane();
        HBox box = new HBox();
        root.getChildren().add(box);

        Scene gameScene = new Scene(
            root,
            Menu.getMainStage().getScene().getWidth(),
            Menu.getMainStage().getScene().getHeight()
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

        Menu.getMainStage().setScene(gameScene);
        InGameGui inGameGui = new InGameGui(gameScene, "Multi");

        Controller.setInGameGui(inGameGui);
        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController::keyPressedHandler);

        if (mode == versus) {
            List<Ghost> blinkys = maze.getConfig().getGhostKind(GhostType.BLINKY);
            Ghost playerGhost = blinkys.get(0);
            playerGhost.setDirectionBuffer(new DirectionBuffer());
            var ghostController = new GhostController(playerGhost);

            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, ghostController::keyPressedHandler);
        } else if (mode == coop) {
            var pacmanController2 = new PacmanController2(maze.getConfig().getPacman2());
            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController2::keyPressedHandler);
        }

        GameView.INSTANCE.setGameView(maze, inGameGui, box);
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.animate();
    }

    @Override
    protected void addMainBox() {
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

        if (options.isEmpty()) {
            options.add(0, createHBox(
                spacingSize, Pos.CENTER, select0, versus
            ));
            options.add(1, createHBox(
                spacingSize, Pos.CENTER, select1, coop
            ));
            options.add(2, createHBox(
                spacingSize, Pos.BOTTOM_LEFT, selectBack, back
            ));
        }

        VBox optionBox = createVBox(
            spacingSize, Pos.CENTER, options.get(0), options.get(1)
        );

        if (selects.isEmpty() && choices.isEmpty()) {
            selects.add(0, select0);
            selects.add(1, select1);
            selects.add(2, selectBack);

            choices.add(0, versus);
            choices.add(1, coop);
            choices.add(2, back);
        }

        bindTextSizeToScene();

        multiPane.setTop(title);
        multiPane.setCenter(optionBox);
        multiPane.setBottom(options.get(2));
        //CHECKSTYLE:OFF
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(selects, choices, gameScene, false);

        options.get(0).setOnMouseClicked(event -> {
            startLevel(0);
            selector.getKeySelector().resetSelect();
        });
        // TODO : Niveau coop
        options.get(1).setOnMouseClicked(event -> {
            startLevel(1);
            selector.getKeySelector().resetSelect();
        });
        options.get(2).setOnMouseClicked(event -> {
            Menu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (Menu.getSelected() == 2) {
                    Menu.INSTANCE.toMenu();
                } else {
                    startLevel(Menu.getSelected());
                }
                selector.getKeySelector().resetSelect();
            }
        });
    }

    @Override
    protected void bindTextSizeToScene() {
        final int scale = 30;
        final int titleScale = 50;
        final String fxFontSize = "-fx-font-size: ";

        fontSize.bind(
            multiScene.widthProperty().divide(scale).add(multiScene.heightProperty().divide(scale))
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
