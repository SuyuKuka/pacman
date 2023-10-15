package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Multiplayer extends Application {
    private StackPane background;

    public Multiplayer(StackPane background) {
        this.background = background;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        Menu.selected = 1;
        
        Text gameSelection = Menu.createText("Game Selection", 68);
        HBox option0 = Menu.createHBox(100, gameSelection);

        Text versus = Menu.createText("Versus", 38);
        Text select1 = Menu.createText(">", 38);
        HBox option1 = Menu.createHBox(20, select1, versus);
        versus.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            versus(primaryStage);
        });

        Text coop = Menu.createText("Coop", 38);
        Text select2 = Menu.createText(">", 38);
        HBox option2 = Menu.createHBox(20, select2, coop);
        coop.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            coop(primaryStage);
        });
        
        Text back = Menu.createText("Back", 38);
        Text selectback = Menu.createText(">", 38);
        HBox optionback = Menu.createHBox(20, selectback, back);
        back.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });
        optionback.setAlignment(Pos.BOTTOM_LEFT);

        VBox position = new VBox(20);
        VBox titlePos = new VBox(80);
        VBox textPos = new VBox(20);
        VBox backPos = new VBox(150);

            
        textPos.getChildren().addAll(option1, option2);
        textPos.setAlignment(Pos.CENTER);

        titlePos.getChildren().addAll(position ,option0, textPos);
        titlePos.setAlignment(Pos.TOP_CENTER);

        backPos.getChildren().addAll(titlePos, optionback);
        backPos.setAlignment(Pos.BOTTOM_LEFT);

        root.getChildren().addAll(background, backPos);

        Scene gameScene = new Scene(root, 690, 510);
        Text[] allText = {select1, select2, selectback};
        
        Menu.selector(gameScene, allText);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (Menu.selected) {
                    case 1:
                        versus(primaryStage);
                        break;
                    
                    case 2:
                        coop(primaryStage);
                        break;
                    
                    case 3:
                        Menu menuBack = new Menu();
                        menuBack.start(primaryStage);
                        break;
    
                    default:
                        break;
                }
            }
        });

        primaryStage.setScene(gameScene);
        //primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void versus(Stage primaryStage) {
        int selected = Menu.selected;
        
    }

    private void coop(Stage primaryStage) {
        int selected = Menu.selected;
    }
    
    
}