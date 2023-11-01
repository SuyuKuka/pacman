package gui;


import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;

public class Multiplayer extends Menu {
    /** L'arrière-plan. */
    private StackPane background;
    private static StackPane rootPane;
    private static VBox mainBox;

    /**
     * Constructeur qui initialise l'arrière-plan.
     * @param newBackground {@code StackPane}
     */
    public Multiplayer(final StackPane newBackground) {
        background = newBackground;
    }

    @Override
    public final void start(final Stage primaryStage) {
        rootPane = new StackPane();

        final int titleSize = 68;
        final int titleSpacing = 100;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = textSpacing;
        final int subBoxSpacing = 80;
        final int mainBoxSpacing = 150;
        final int width = 690;
        final int height = 510;

        Text gameSelection = createText("Game Selection", titleSize);
        HBox title = createHBox(titleSpacing, Pos.CENTER, gameSelection);

        Text select0 = createText(">", textSize);
        Text versus = createText("Versus", textSize);
        HBox option0 = createHBox(
            textSpacing, Pos.CENTER, select0, versus
        );
        versus.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text select1 = createText(">", textSize);
        Text coop = createText("Coop", textSize);
        HBox option1 = createHBox(textSpacing, Pos.CENTER, select1, coop);
        coop.addEventHandler(
            MouseEvent.MOUSE_CLICKED, keyEvent -> startLevel(primaryStage)
        );

        Text selectback = createText(">", textSize);
        Text back = createText("Back", textSize);
        HBox optionBack = createHBox(
            textSpacing, Pos.CENTER, selectback, back
        );

        back.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            setBacked(true);
            setMultiplayerChecking(true);
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
        mainBox = createVBox(
            mainBoxSpacing, Pos.BOTTOM_LEFT, subBox, optionBack
        );

        rootPane.getChildren().addAll(background, mainBox);
        transitionRightToLeft(primaryStage, mainBox);

        Scene gameScene = new Scene(rootPane, width, height);
        Text[] choices = {select0, select1, selectback};
        Text[] options = {versus, coop, back};

        KeySelector keySel = new KeySelector(choices, primaryStage, false);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : options) {
            MouseSelector m = new MouseSelector(choices, select++);
            text.setOnMouseEntered(m::handle);
        }

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Menu.getSelected() == 2) {
                    setBacked(true);
                    setMultiplayerChecking(true);
                    Menu menu = new Menu();
                    menu.start(primaryStage);
                } else {
                    startLevel(primaryStage);
                }
            }
        });

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    private void startLevel(final Stage primaryStage) {
        int selected = Menu.getSelected();
    }

    /**
     * Methode qui fais la transition de gauche à droite.
     * @param primaryStage
     */
    public static void transitionLeftToRight(final Stage primaryStage) {
        final double transitionDuration = 0.8;
        rootPane.getChildren().clear();

        StackPane originalContent = new StackPane();
        originalContent.getChildren().addAll(mainBox);

        StackPane previewContent = new StackPane();
        previewContent.getChildren().addAll(getMainBox());

        getRootPane().getChildren().addAll(originalContent, previewContent);

        TranslateTransition deleteTransition = new TranslateTransition(
            Duration.seconds(transitionDuration), originalContent
        );
        deleteTransition.setFromX(0);
        deleteTransition.setToX(primaryStage.getWidth());
        deleteTransition.setOnFinished(event -> {
            getRootPane().getChildren().remove(originalContent);
        });
        deleteTransition.play();

        TranslateTransition originalTransition = new TranslateTransition(
            Duration.seconds(transitionDuration), previewContent
        );
        originalTransition.setFromX(-primaryStage.getWidth());
        originalTransition.setToX(0);
        originalTransition.play();
    }

}
