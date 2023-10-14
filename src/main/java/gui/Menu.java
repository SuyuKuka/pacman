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

public class Menu extends Application {
        protected static int select;
        @Override
        public void start(Stage primaryStage) {
                Menu.select=1;
                StackPane root = new StackPane();
                primaryStage.setTitle("Mac-Pan-Pac-Man");
                primaryStage.getIcons().add(new Image("file:src\\main\\resources\\pacman.png"));  
                
                Image backgroundImage = new Image("file:src\\main\\resources\\background.png");
                BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null);
                Background background = new Background(backgroundImg);
                StackPane backgroundPane = new StackPane();
                backgroundPane.setBackground(background);
                root.setBackground(background);
                
                

                //Lance la vidéo du lore?
                Text titre = createText("Mac-Pan-Pac-Man", 68);
                HBox option0 = createHBox(100,  titre);
                titre.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                        //lancement de l'histoire
                });
                
                Text solo = createText("Solo", 38);
                Text select1 = createText(">", 38);
                HBox option1 = createHBox(20, select1, solo);
                solo.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                        Solo soloMenu = new Solo(backgroundPane);
                        try {
                                soloMenu.start(primaryStage);
                        } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                });

                Text multiplayer = createText("Multiplayer", 38);
                Text select2 = createText(">", 38);
                HBox option2 = createHBox(20, select2, multiplayer);

                multiplayer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
                        
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
                        if (Menu.select == 1) {
                                Solo soloMenu = new Solo(backgroundPane);
                                try {
                                        soloMenu.start(primaryStage);
                                } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }
                        }else if (Menu.select == 2) {
                                // Multi
                        }else if (Menu.select == 3) {
                                // Settings
                        }else if (Menu.select == 4){
                                primaryStage.close();
                                System.exit(0);
                        }
                    
                }});
                primaryStage.setScene(gameScene);
                primaryStage.show();
                    
        }

        public static Text createText(String st, int size){
                Text text = new Text(st);
                text.setFill(Color.WHITE);
                text.setFont(Font.loadFont("file:src\\main\\resources\\fonts\\small_pixel-7.ttf", size));
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
                
                for(int i=1; i<numberSelect; i=i+1){
                        selects[i].setVisible(false);
                }
                scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {    
                    if (keyEvent.getCode() == KeyCode.UP) {
                            // Flèche vers le haut
                            Menu.select--;
                            if (Menu.select <= 0) {
                                Menu.select = numberSelect;
                            }
                    } else if (keyEvent.getCode() == KeyCode.DOWN) {
                            // Flèche vers le bas
                            Menu.select++;
                            if (Menu.select >= numberSelect+1) {
                                Menu.select = 1;
                            }
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                        for (int i=0; i < numberSelect; i=i+1) {
                            if (Menu.select == i+1) {
                                for (int j=0; j < numberSelect; j=j+1) {
                                    if (i == j) {
                                        selects[j].setVisible(true);
                                    }
                                    else {
                                        selects[j].setVisible(false);
                                    }
                                }
                            }
                        }
                    }
                    });
            }

}
