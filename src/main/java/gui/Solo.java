package gui;

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

import config.MazeConfig;
import model.MazeState;
import utils.Music;

public class Solo extends Menu {
    private StackPane background;

    public Solo(StackPane background) {
        this.background = background;
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane rootPane = new StackPane();

        Text levelSelector = createText("Level Selector", 68);
        HBox title = createHBox(0, Pos.CENTER, levelSelector);

        Text select1 = createText(">", 38);
        Text classique = createText("Classique", 38);
        HBox option1 = createHBox(20, Pos.CENTER, select1, classique);
        classique.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));

        Text select2 = createText(">", 38);
        Text facile = createText("Facile", 38);
        HBox option2 = createHBox(20, Pos.CENTER, select2, facile);
        facile.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));

        Text select3 = createText(">", 38);
        Text moyen = createText("Moyen", 38);
        HBox option3 = createHBox(20, Pos.CENTER, select3, moyen);
        moyen.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));

        Text select4 = createText(">", 38);
        Text difficile = createText("Difficile", 38);
        HBox option4 = createHBox(20, Pos.CENTER, select4, difficile);
        difficile.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));

        Text select5 = createText(">", 38);
        Text impossible = createText("Impossible", 38);
        HBox option5 = createHBox(20, Pos.CENTER, select5, impossible);
        impossible.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));

        Text selectBack = createText(">", 38);
        Text back = createText("Back", 38);
        HBox optionBack = createHBox(20, Pos.CENTER, selectBack, back);
        back.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });
        optionBack.setAlignment(Pos.BOTTOM_LEFT);

        VBox optionBox = createVBox(15, Pos.CENTER, option1, option2, option3, option4, option5);
        VBox subBox = createVBox(40, Pos.CENTER, title, optionBox);
        VBox mainBox = createVBox(45, Pos.BOTTOM_LEFT, subBox, optionBack);

        rootPane.getChildren().addAll(background, mainBox);

        Scene gameScene = new Scene(rootPane, 690, 510);
        Text[] choices = {select1, select2, select3, select4, select5, selectBack};
        
        selector(gameScene, choices);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Menu.selected == 5) {
                    Menu menuBack = new Menu();
                    menuBack.start(primaryStage);
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
     * Méthode permettant de lancer un niveau, le niveau lancé dépend de la variable statique {@code selected} de la classe {@code Menu}.
     * @param primaryStage
     */
    public static void startLevel(Stage primaryStage){
        Music.stopBgMusic();

        Pane rootPane = new Pane();
        Scene gameScene = new Scene(rootPane);
        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        MazeState maze;
        switch (selected) {
            case 0: // Classique
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case 1: // TODO: Autres niveaux à faire
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case 2:
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case 3:
                maze = new MazeState(MazeConfig.makeExample1());
                break;

            case 4:
                maze = new MazeState(MazeConfig.makeExample1());
                break;
        
            default:
                maze = new MazeState(MazeConfig.makeExample1());
                break;
        }
        
        primaryStage.setScene(gameScene);
        InGameGui.setInGameGui(primaryStage, gameScene);
        
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.setGameView(maze, rootPane, 30.0);
        primaryStage.sizeToScene();
        primaryStage.show();
        GameView.INSTANCE.animate();
    }
}