package gui;

import java.nio.file.Paths;

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

public class Menu extends Application {
    protected static int selected = 1;

    @Override
    public void start(Stage primaryStage) {
        selected = 1;
        StackPane root = new StackPane();
        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(new Image(Paths.get("src/main/resources/pacman.png").toUri().toString()));  
            
        Image backgroundImage = new Image(Paths.get("src/main/resources/background.png").toUri().toString());
        BackgroundImage backgroundImg = new BackgroundImage(
        backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null
        );
        Background background = new Background(backgroundImg);
        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(background);
        root.setBackground(background);
                
        Text titre = createText("Mac-Pan-Pac-Man", 68);
        HBox option0 = createHBox(100, titre);
        // Lance la vidéo du lore?
        titre.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            // Lancement de l'histoire
        });
        
        Text solo = createText("Solo", 38);
        Text select1 = createText(">", 38);
        HBox option1 = createHBox(20, select1, solo);
        solo.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Solo soloMenu = new Solo(backgroundPane);
            try {
                soloMenu.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Text multiplayer = createText("Multiplayer", 38);
        Text select2 = createText(">", 38);
        HBox option2 = createHBox(20, select2, multiplayer);
        multiplayer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Multiplayer multiMenu = new Multiplayer(backgroundPane);
            try {
                multiMenu.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Text settings = createText("Settings", 38);
        Text select3 = createText(">", 38);
        HBox option3 = createHBox(20, select3, settings);
        settings.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                
        });

        Text quit = createText("Quit", 38);
        Text select4 = createText(">", 38);
        HBox option4 = createHBox(20, select4, quit);
        quit.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                primaryStage.close();
                System.exit(0);
        });
        VBox titlePos = new VBox(100);
        VBox textPos = new VBox(20);
        
        textPos.getChildren().addAll(option1, option2, option3, option4);
        textPos.setAlignment(Pos.CENTER);

        titlePos.getChildren().addAll(option0, textPos);
        titlePos.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titlePos);
        Scene gameScene = new Scene(root, 690, 510);

        Text[] allText = {select1, select2, select3, select4};
        selector(gameScene, allText);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (selected) {
                    case 1:
                        Solo soloMenu = new Solo(backgroundPane);
                        try {
                                soloMenu.start(primaryStage);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        break;

                    case 2:
                        Multiplayer multiMenu = new Multiplayer(backgroundPane);
                        try {
                                multiMenu.start(primaryStage);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        break;
                        
                    case 3:
                        // Settings
                        break;

                    case 4:
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
    }

    public static Text createText(String str, int size) {
        Text text = new Text(str);
        text.setFill(Color.WHITE);
        text.setFont(Font.loadFont(Paths.get("src/main/resources/fonts/small_pixel-7.ttf").toUri().toString(), size));
        
        return text;
    }

    public static VBox createVBox(int spacing, Node... nodes) {
        VBox box = new VBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public static HBox createHBox(int spacing, Node... nodes) {
        HBox box = new HBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public static void selector(Scene scene, Text[] selects) {
        int numberSelect = selects.length;

        for(int i = 1; i < numberSelect; i++){
            selects[i].setVisible(false);
        }

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {    
            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                selected--;
                if (selected <= 0) {
                    selected = numberSelect;
                }
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                selected++;
                if (selected >= numberSelect + 1) {
                    selected = 1;
                }
            }

            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                for (int i = 0; i < numberSelect; i++) {
                    if (Menu.selected == i + 1) {
                        for (int j = 0; j < numberSelect; j++) {
                            selects[j].setVisible(i==j);
                        }
                    }
                }
            }
        });
    }

}
