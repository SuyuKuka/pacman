package gui.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import config.MazeConfig;
import gui.GameView;
import gui.InGameGui;
import gui.controller.Controller;
import gui.controller.PacmanController;
import model.MazeState;
import utils.Music;

public final class Solo extends Menu {
    public static final Solo INSTANCE = new Solo();

    private Solo() {
    }

    @Override
    public void start(final Stage primaryStage) {
    }

    /**
     * Affiche le menu Solo.
     */
    public void toSolo() {
        setScene();
        getMainStage().setScene(getMenuScene());
    }

    @Override
    protected void addMainBox() {
        final int titleSize = 100;
        final int spacingSize = 20;

        setTitleText(createText("Level Selector", titleSize));
        HBox title = createHBox(0, Pos.TOP_CENTER, getTitleText());

        Text select0 = createText(">", getFontSize().getValue().intValue());
        Text classique = createText("Classique", getFontSize().getValue().intValue());

        Text selectBack = createText(">", getFontSize().getValue().intValue());
        Text backText = createText("Back", getFontSize().getValue().intValue());

        //CHECKSTYLE:OFF
        if (getOptions().isEmpty()) {
            getOptions().add(0, createHBox(spacingSize, Pos.CENTER, select0, classique));
            getOptions().add(1, createHBox(spacingSize, Pos.BOTTOM_LEFT, selectBack, backText));
        }

        VBox optionBox = createVBox(
            spacingSize, Pos.CENTER,
            getOptions().get(0)
        );

        if (getSelects().isEmpty() && getChoices().isEmpty()) {
            getSelects().add(0, select0);
            getSelects().add(1, selectBack);

            getChoices().add(0, classique);
            getChoices().add(1, backText);
        }

        bindTextSizeToScene();

        getMenuPane().setTop(title);
        getMenuPane().setCenter(optionBox);
        getMenuPane().setBottom(getOptions().get(1));
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(getSelects(), getChoices(), gameScene, false);

        getOptions().get(0).setOnMouseClicked(event -> {
            startLevel(0);
            selector.getKeySelector().resetSelect();
        });
        getOptions().get(1).setOnMouseClicked(event -> {
            MainMenu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            final int back = 1;
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Selector.getSelectedIndex() == back) {
                    MainMenu.INSTANCE.toMenu();
                } else {
                    startLevel(Selector.getSelectedIndex());
                }
                selector.getKeySelector().resetSelect();
            }
        });
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
            getMainStage().getScene().getWidth(),
            getMainStage().getScene().getHeight()
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

        getMainStage().setScene(gameScene);
        InGameGui inGameGui = new InGameGui(gameScene, "Solo");

        Controller.setInGameGui(inGameGui);
        var pacmanController = new PacmanController(maze.getConfig().getPacman());
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, pacmanController::keyPressedHandler);

        GameView.INSTANCE.setGameView(maze, inGameGui, box);
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.animate();
    }
}
