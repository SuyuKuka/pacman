package gui.menu;

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

import java.util.ArrayList;

import config.MazeConfig;
import gui.GameView;
import gui.InGameGui;
import gui.controller.Controller;
import gui.controller.PacmanController;
import model.MazeState;
import utils.Music;

public final class Solo extends Menu {
    private BorderPane soloPane;
    private Scene soloScene;
    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    public static final Solo INSTANCE = new Solo();

    private Solo() {
    }

    @Override
    public void start(final Stage primaryStage) {
        soloPane = new BorderPane();
        soloPane.setBackground(getBackground());

        soloScene = new Scene(
            soloPane,
            primaryStage.getScene().getWidth(),
            primaryStage.getScene().getHeight()
        );
        soloScene.setFill(Color.BLACK);

        addMainBox();
        addEventHandlers(soloScene);
    }

    @Override
    public void setScene() {
        soloScene.setRoot(new HBox());
        soloScene = new Scene(
            soloPane,
            Menu.getMainStage().getScene().getWidth(),
            Menu.getMainStage().getScene().getHeight()
        );
        soloScene.setFill(Color.BLACK);

        addEventHandlers(soloScene);
    }

    /**
     * Affiche le menu Solo.
     */
    public void toSolo() {
        setScene();
        Menu.getMainStage().setScene(soloScene);
    }

    @Override
    protected void addMainBox() {
        final int titleSize = 100;
        final int spacingSize = 20;

        titleText = createText("Level Selector", titleSize);
        HBox title = createHBox(0, Pos.TOP_CENTER, titleText);

        Text select0 = createText(">", fontSize.getValue().intValue());
        Text classique = createText("Classique", fontSize.getValue().intValue());

        Text selectBack = createText(">", fontSize.getValue().intValue());
        Text backText = createText("Back", fontSize.getValue().intValue());

        //CHECKSTYLE:OFF
        if (options.isEmpty()) {
            options.add(0, createHBox(spacingSize, Pos.CENTER, select0, classique));
            options.add(1, createHBox(spacingSize, Pos.BOTTOM_LEFT, selectBack, backText));
        }

        VBox optionBox = createVBox(
            spacingSize, Pos.CENTER,
            options.get(0)
        );

        if (selects.isEmpty() && choices.isEmpty()) {
            selects.add(0, select0);
            selects.add(1, selectBack);

            choices.add(0, classique);
            choices.add(1, backText);
        }

        bindTextSizeToScene();

        soloPane.setTop(title);
        soloPane.setCenter(optionBox);
        soloPane.setBottom(options.get(1));
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
        options.get(1).setOnMouseClicked(event -> {
            Menu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            final int back = 1;
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Menu.getSelected() == back) {
                    Menu.INSTANCE.toMenu();
                } else {
                    startLevel(getSelected());
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
            soloScene.widthProperty().divide(scale).add(soloScene.heightProperty().divide(scale))
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
     * @param level
     */
    public static void startLevel(final int level) {
        InGameGui.setCurrentLevel(level);
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
        final int classique = 0;
        switch (level) {
            case classique:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;

            default:
                maze = new MazeState(MazeConfig.classicalConfig(false, false));
                break;
        }

        Menu.getMainStage().setScene(gameScene);
        InGameGui inGameGui = new InGameGui(gameScene, "Solo");

        Controller.setInGameGui(inGameGui);
        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController::keyPressedHandler);

        GameView.INSTANCE.setGameView(maze, inGameGui, box);
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.animate();
    }
}
