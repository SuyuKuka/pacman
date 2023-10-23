package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Multiplayer extends Menu {
    private StackPane background;

    public Multiplayer(StackPane background) {
        this.background = background;
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane rootPane = new StackPane();
        
        Text gameSelection = Menu.createText("Game Selection", 68);
        HBox title = Menu.createHBox(100, Pos.CENTER, gameSelection);

        Text select0 = Menu.createText(">", 38);
        Text versus = Menu.createText("Versus", 38);
        HBox option0 = Menu.createHBox(20, Pos.CENTER, select0, versus);
        versus.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));

        Text select1 = Menu.createText(">", 38);
        Text coop = Menu.createText("Coop", 38);
        HBox option1 = Menu.createHBox(20, Pos.CENTER, select1, coop);
        coop.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage));
        
        Text selectback = Menu.createText(">", 38);
        Text back = Menu.createText("Back", 38);
        HBox optionBack = Menu.createHBox(20, Pos.CENTER, selectback, back);
        back.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });
        optionBack.setAlignment(Pos.BOTTOM_LEFT);

        VBox optionBox = createVBox(20, Pos.CENTER, option0, option1);
        VBox subBox = createVBox(80, Pos.TOP_CENTER, title, optionBox);
        VBox mainBox = createVBox(150, Pos.BOTTOM_LEFT, subBox, optionBack);

        rootPane.getChildren().addAll(background, mainBox);

        Scene gameScene = new Scene(rootPane, 690, 510);
        Text[] choices = {select0, select1, selectback};
        
        Menu.selector(gameScene, choices);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Menu.selected == 2) {
                    Menu menuBack = new Menu();
                    menuBack.start(primaryStage);
                } else {
                    startLevel(primaryStage);
                }
            }
        });

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        // primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void startLevel(Stage primaryStage) {
        int selected = Menu.selected;
    }
}