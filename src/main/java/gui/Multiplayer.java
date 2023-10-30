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
    /** L'arrière-plan. */
    private StackPane background;

    /**
     * Constructeur qui initialise l'arrière-plan.
     * @param newBackground {@code StackPane}
     */
    public Multiplayer(final StackPane newBackground) {
        background = newBackground;
    }

    @Override
    public final void start(final Stage primaryStage) {
        final int titleSize = 68;
        final int titleSpacing = 100;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = textSpacing;
        final int subBoxSpacing = 80;
        final int mainBoxSpacing = 150;
        final int width = 690;
        final int height = 510;

        StackPane rootPane = new StackPane();

        Text gameSelection = Menu.createText("Game Selection", titleSize);
        HBox title = Menu.createHBox(titleSpacing, Pos.CENTER, gameSelection);

        Text select0 = Menu.createText(">", textSize);
        Text versus = Menu.createText("Versus", textSize);
        HBox option0 = Menu.createHBox(
            textSpacing, Pos.CENTER, select0, versus
        );
        versus.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text select1 = Menu.createText(">", textSize);
        Text coop = Menu.createText("Coop", textSize);
        HBox option1 = Menu.createHBox(textSpacing, Pos.CENTER, select1, coop);
        coop.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text selectback = Menu.createText(">", textSize);
        Text back = Menu.createText("Back", textSize);
        HBox optionBack = Menu.createHBox(
            textSpacing, Pos.CENTER, selectback, back
        );
        back.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });
        optionBack.setAlignment(Pos.BOTTOM_LEFT);

        VBox optionBox = createVBox(
            optionSpacing, Pos.CENTER, option0, option1
        );
        VBox subBox = createVBox(
            subBoxSpacing, Pos.TOP_CENTER, title, optionBox
        );
        VBox mainBox = createVBox(
            mainBoxSpacing, Pos.BOTTOM_LEFT, subBox, optionBack
        );

        rootPane.getChildren().addAll(background, mainBox);

        Scene gameScene = new Scene(rootPane, width, height);
        Text[] choices = {select0, select1, selectback};

        Menu.selector(gameScene, choices);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Menu.getSelected() == 2) {
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

    private void startLevel(final Stage primaryStage) {
        int selected = Menu.getSelected();
    }
}
