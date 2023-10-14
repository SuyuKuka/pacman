package gui;

import config.MazeConfig;
import geometry.IntCoordinates;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.MazeState;

public class Solo extends Application{
    private StackPane background;

    public Solo(StackPane background){
        this.background=background;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
            StackPane root = new StackPane();
            Menu.select=1;
            //Solo
            Text levelSelector = Menu.createText("Level Selector", 56);
            HBox option0 = Menu.createHBox(100,  levelSelector);

            Text classique = Menu.createText("Classique", 38);
            Text select1 = Menu.createText(">", 38);
            HBox option1 = Menu.createHBox(20, select1, classique);
            classique.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                classique(primaryStage);
            });

            Text facile = Menu.createText("Facile", 38);
            Text select2 = Menu.createText(">", 38);
            HBox option2 = Menu.createHBox(20, select2, facile);
            facile.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                facile(primaryStage);
            });
            Text moyen = Menu.createText("Moyen", 38);
            Text select3 = Menu.createText(">", 38);
            HBox option3 = Menu.createHBox(20, select3, moyen);
            moyen.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                moyen(primaryStage);
            });
            Text difficile = Menu.createText("Difficile", 38);
            Text select4 = Menu.createText(">", 38);
            HBox option4 = Menu.createHBox(20, select4, difficile);
            difficile.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                difficile(primaryStage);
            });

            Text impossible = Menu.createText("Impossible", 38);
            Text select5 = Menu.createText(">", 38);
            HBox option5 = Menu.createHBox(20, select5, impossible);
            impossible.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                impossible(primaryStage);
            });

            Text back = Menu.createText("Back", 38);
            Text selectback = Menu.createText(">", 38);
            HBox optionback = Menu.createHBox(20, selectback, back);
            back.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                Menu menu = new Menu();
                menu.start(primaryStage);
            });
            optionback.setAlignment(Pos.TOP_LEFT);


            VBox listPos = new VBox(80);
            VBox levelPos = new VBox(20);
                
            levelPos.getChildren().addAll(option1, option2, option3, option4, option5, optionback);
            levelPos.setAlignment(Pos.CENTER);

            listPos.getChildren().addAll(option0, levelPos);
            listPos.setAlignment(Pos.CENTER);

            root.getChildren().addAll(background, listPos);

            Scene gameScene = new Scene(root, 690, 510);
            Text[] allText = {select1, select2, select3, select4, select5, selectback};
            
            Menu.selector(gameScene, allText);

            gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    if (Menu.select == 1) {
                        classique(primaryStage);
                    }else if (Menu.select == 2) {
                        facile(primaryStage);
                    }else if (Menu.select == 3) {
                        moyen(primaryStage);
                    }else if (Menu.select == 4){
                        difficile(primaryStage);
                    }else if (Menu.select == 5){
                        impossible(primaryStage);
                    }else if (Menu.select == 6){
                        Menu menuBack = new Menu();
                        menuBack.start(primaryStage);
                    }
                
            }});

            primaryStage.setScene(gameScene);
            
            primaryStage.sizeToScene();
            primaryStage.show();
    }

    //Une fonction par niveau
    public static void classique(Stage primaryStage){
            //Classique
            Pane root = new Pane();
            Scene gameScene = new Scene(root);
            var pacmanController = new PacmanController();
            gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
            gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

            MazeConfig map = new MazeConfig( MazeConfig.getMaze("src/main/java/config/Level1.txt"),
                new IntCoordinates(8, 16), // Pacman
                new IntCoordinates(8, 8), // Blinky (Rouge)
                new IntCoordinates(8, 10), // Pinky (Rose)
                new IntCoordinates(7, 10), // Inky (Bleu)
                new IntCoordinates(9, 10) // Clyde (Orange)
            );
            MazeState maze = new MazeState(map);
            
            primaryStage.setScene(gameScene);
            MenuGraphics graphics = new MenuGraphics(gameScene, primaryStage);
            GameView gameView = new GameView(maze, root, 30.0, graphics);
            
            primaryStage.sizeToScene();
            primaryStage.show();
            gameView.animate();
    }

    public static void facile(Stage primaryStage){
        Pane root = new Pane();
        Scene gameScene = new Scene(root);
        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        //MazeConfig pour niveau facile
        MazeState maze = null;

        MenuGraphics graphics = new MenuGraphics(gameScene, primaryStage);
        var gameView = new GameView(maze, root, 30.0, graphics);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();
        primaryStage.show();
        gameView.animate();
    }

    public static void moyen(Stage primaryStage){
        Pane root = new Pane();
        Scene gameScene = new Scene(root);
        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        //MazeConfig pour niveau moyen
        MazeState maze = null;

        MenuGraphics graphics = new MenuGraphics(gameScene, primaryStage);
        var gameView = new GameView(maze, root, 30.0, graphics);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();
        primaryStage.show();
        gameView.animate();
    }

    public static void difficile(Stage primaryStage){
        Pane root = new Pane();
        Scene gameScene = new Scene(root);
        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        //MazeConfig pour niveau difficile
        MazeState maze = null;

        MenuGraphics graphics = new MenuGraphics(gameScene, primaryStage);
        var gameView = new GameView(maze, root, 30.0, graphics);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();
        primaryStage.show();
        gameView.animate();
    }

    public static void impossible(Stage primaryStage){
        Pane root = new Pane();
        Scene gameScene = new Scene(root);
        var pacmanController = new PacmanController();
        gameScene.setOnKeyPressed(pacmanController::keyPressedHandler);
        gameScene.setOnKeyReleased(pacmanController::keyReleasedHandler);

        //MazeConfig pour niveau impossible
        MazeState maze = null;

        MenuGraphics graphics = new MenuGraphics(gameScene, primaryStage);
        var gameView = new GameView(maze, root, 30.0, graphics);
        primaryStage.setScene(gameScene);
        primaryStage.sizeToScene();
        primaryStage.show();
        gameView.animate();
    }
    
}
