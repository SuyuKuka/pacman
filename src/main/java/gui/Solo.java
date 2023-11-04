package gui;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import config.MazeConfig;
import model.MazeState;
import utils.Music;

public final class Solo extends Menu {
    /** StackPane pour l'arrère-plan. */
    private StackPane background;
    /** rootPane de Solo. */
    private static StackPane rootPane;
    /** mainBox de Solo.*/
    private static VBox mainBox;

    /**
     * Constructeur prenant en paramètre l'arrière-plan du menu{@code Solo}.
     * @param bg {@code StackPane}de l'arrière-plan
     */
    public Solo(final StackPane bg) {
        background = bg;
    }

    @Override
    public void start(final Stage primaryStage) {
        rootPane = new StackPane();
        final int titleSize = 68;
        final int selectSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = 15;
        final int subBoxSpacing = 40;
        final int mainBoxSpacing = 45;
        final int sceneWidth = 690;
        final int sceneHeight = 510;

        Text levelSelector = createText("Level Selector", titleSize);
        HBox title = createHBox(0, Pos.CENTER, levelSelector);

        Text select1 = createText(">", selectSize);
        Text classique = createText("Classique", selectSize);
        HBox option1 = createHBox(textSpacing, Pos.CENTER, select1, classique);
        classique.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text select2 = createText(">", selectSize);
        Text facile = createText("Facile", selectSize);
        HBox option2 = createHBox(textSpacing, Pos.CENTER, select2, facile);
        facile.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text select3 = createText(">", selectSize);
        Text moyen = createText("Moyen", selectSize);
        HBox option3 = createHBox(textSpacing, Pos.CENTER, select3, moyen);
        moyen.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text select4 = createText(">", selectSize);
        Text difficile = createText("Difficile", selectSize);
        HBox option4 = createHBox(textSpacing, Pos.CENTER, select4, difficile);
        difficile.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text select5 = createText(">", selectSize);
        Text impossible = createText("Impossible", selectSize);
        HBox option5 = createHBox(textSpacing, Pos.CENTER, select5, impossible);
        impossible.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text selectBack = createText(">", selectSize);
        Text backText = createText("Back", selectSize);
        HBox optionBack = createHBox(
            textSpacing, Pos.CENTER, selectBack, backText
        );
        backText.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            setBacked(true);
            setSoloChecking(true);
            Menu menu = new Menu();
            menu.start(primaryStage);
        });
        optionBack.setAlignment(Pos.BOTTOM_LEFT);

        VBox optionBox = createVBox(
            optionSpacing, Pos.CENTER,
            option1, option2, option3, option4, option5
        );
        VBox subBox = createVBox(subBoxSpacing, Pos.CENTER, title, optionBox);
        mainBox = createVBox(
            mainBoxSpacing, Pos.BOTTOM_LEFT, subBox, optionBack
        );

        rootPane.getChildren().addAll(background, mainBox);
        transitionRightToLeft(primaryStage, mainBox);

        Scene gameScene = new Scene(rootPane, sceneWidth, sceneHeight);
        Text[] choices = {
            select1, select2, select3, select4, select5, selectBack
        };
        Text[] options = {
            classique, facile, moyen, difficile, impossible, backText
        };

        KeySelector keySel = new KeySelector(choices, primaryStage, false);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : options) {
            MouseSelector m = new MouseSelector(choices, select++);
            text.setOnMouseEntered(m::handle);
        }

        final int back = 5;
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Menu.getSelected() == back) {
                    setBacked(true);
                    setSoloChecking(true);
                    Menu menu = new Menu();
                    menu.start(primaryStage);
                } else {
                    startLevel(primaryStage);
                }
            }
        });

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * Méthode permettant de lancer un niveau, le niveau lancé dépend de
     * la variable statique {@code selected} de la classe {@code Menu}.
     * @param primaryStage
     */
    public static void startLevel(final Stage primaryStage) {
        final int scale = 30;
        Music.stopBgMusic();

        Pane pane = new Pane();
        Scene gameScene = new Scene(pane);
        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        MazeState maze;
        final int classique = 0;
        final int facile = 1;
        final int moyen = 2;
        final int difficile = 3;
        final int impossible = 4;
        switch (getSelected()) {
            case classique: // Classique
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

        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.setGameView(maze, pane, scale);
        primaryStage.sizeToScene();
        primaryStage.show();
        GameView.INSTANCE.animate();
    }

    /**
     * Methode qui fais la transition de gauche a droite.
     * @param primaryStage
     */
    public static void transitionLeftToRight(final Stage primaryStage) {
        final double transitionDuration = 0.8;
        //Suppression de ce qui y'avais avant (au cas ou)
        rootPane.getChildren().clear();

        //On créer un StackPane qui contient toutes les box sauf le background
        StackPane originalContent = new StackPane();
        originalContent.getChildren().addAll(mainBox);

        //On en créer un autre qui contient toutes les box de Menu
        StackPane previewContent = new StackPane();
        previewContent.getChildren().addAll(getMainBox());

        //On l'ajoute dans le rootPane du Menu
        getRootPane().getChildren().addAll(originalContent, previewContent);

        //On fait la transition de la box actuel du milieu vers la droite
        TranslateTransition deleteTransition = new TranslateTransition(
            Duration.seconds(transitionDuration), originalContent
        );
        deleteTransition.setFromX(0);
        deleteTransition.setToX(primaryStage.getWidth());
        deleteTransition.setOnFinished(event -> {
            //On la supprime apres que la transition ait fini
            getRootPane().getChildren().remove(originalContent);
        });
        //On applique la transition
        deleteTransition.play();

        //On fait la transition de la box de Menu de la gauche au milieu
        TranslateTransition originalTransition = new TranslateTransition(
            Duration.seconds(transitionDuration), previewContent
        );
        originalTransition.setFromX(-primaryStage.getWidth());
        originalTransition.setToX(0);
        //On applique la transition
        originalTransition.play();
    }
}
