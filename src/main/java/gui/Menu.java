package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.nio.file.Paths;

import utils.Music;//ajouter le fichier utils/Music

public class Menu extends Application {
    protected static int selected = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(new Image(Paths.get("src/main/resources/pacman.png").toUri().toString()));

        StackPane rootPane = new StackPane();
        rootPane.setBackground(getBackground());

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(getBackground());
                
        Text titre = createText("Mac-Pan-Pac-Man", 68);
        HBox title = createHBox(100, Pos.CENTER, titre);
        // Lance la vidéo du lore?
        titre.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            // Lancement de l'histoire
        });

        Text select0 = createText(">", 38);
        Text solo = createText("Solo", 38);
        HBox option0 = createHBox(20, Pos.CENTER, select0, solo);
        solo.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Solo soloMenu = new Solo(backgroundPane);
            soloMenu.start(primaryStage);
        });

        Text select1 = createText(">", 38);
        Text multiplayer = createText("Multiplayer", 38);
        HBox option1 = createHBox(20, Pos.CENTER, select1, multiplayer);
        multiplayer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Multiplayer multiMenu = new Multiplayer(backgroundPane);
            multiMenu.start(primaryStage);
        });

        Text select2 = createText(">", 38);
        Text settings = createText("Settings", 38);
        HBox option2 = createHBox(20, Pos.CENTER, select2, settings);
        settings.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text select3 = createText(">", 38);
        Text quit = createText("Quit", 38);
        HBox option3 = createHBox(20, Pos.CENTER, select3, quit);
        quit.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            primaryStage.close();
            System.exit(0);
        });

        VBox optionBox = createVBox(20, Pos.CENTER, option0, option1, option2, option3);
        VBox mainBox = createVBox(100, Pos.CENTER, title, optionBox);

        rootPane.getChildren().addAll(mainBox);
        Scene gameScene = new Scene(rootPane, 690, 510);

        Text[] choices = {select0, select1, select2, select3};
        selector(gameScene, choices);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (selected) {
                    case 0:
                        Solo soloMenu = new Solo(backgroundPane);
                        soloMenu.start(primaryStage);
                        break;

                    case 1:
                        Multiplayer multiMenu = new Multiplayer(backgroundPane);
                        multiMenu.start(primaryStage);
                        break;
                        
                    case 2:
                        // Settings
                        break;

                    case 3:
                        primaryStage.close();
                        System.exit(0);
                        break;

                    default:
                        break;
                }
            }
        });
        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();

        Music.playLoopingBgMusic("bgm.wav");//file du fond de music en boucle(obligé:.wav)
    }

    private static Background getBackground() {
        Image image = new Image(Paths.get("src/main/resources/background.png").toUri().toString());
        BackgroundImage backgroundImage = new BackgroundImage(
            image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null
        );
        return new Background(backgroundImage);
    }

    public static Text createText(String str, int size) {
        Text text = new Text(str);
        text.setFill(Color.WHITE);
        text.setFont(Font.loadFont(Paths.get("src/main/resources/fonts/small_pixel-7.ttf").toUri().toString(), size));
        return text;
    }


    public static VBox createVBox(int spacing, Pos position, Node... nodes) {
        VBox box = new VBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(position);
        return box;
    }

    public static HBox createHBox(int spacing, Pos position, Node... nodes) {
        HBox box = new HBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(position);
        return box;
    }

    public static void selector(Scene scene, Text[] choices) {
        selected = 0;
        int totalChoices = choices.length - 1;

        for(int i = 1; i < totalChoices + 1; i++){
            choices[i].setVisible(false);
        }

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                choices[selected].setVisible(false);
                selected--;
                if (selected < 0) selected = totalChoices;
                choices[selected].setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                choices[selected].setVisible(false);
                selected++;
                if (selected > totalChoices) selected = 0;
                choices[selected].setVisible(true);
            }
        });
    }

}
