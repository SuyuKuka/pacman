package gui.menu;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class Multiplayer extends Menu {
    /** Initialise un rootPane pour Multiplayer. */
    private StackPane rootPane = new StackPane();
    /** La box principale de Multiplayer. */
    private VBox mainBox;


    @Override
    public void start(final Stage primaryStage) {
        final int mainBoxSpacing = 150;
        final int sceneWidth = 16;
        final int sceneHeight = 39;

        setMainBoxAutoSpacing(mainBoxSpacing);

        Scene gameScene = new Scene(
            rootPane,
            primaryStage.getWidth() - sceneWidth,
            primaryStage.getHeight() - sceneHeight
        );

        resizeAndShowMenu(primaryStage, gameScene);

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    private void startLevel(final Stage primaryStage) {
        int selected = Menu.getSelected();
    }

    @Override
    protected void resizeAndShowMenu(
        final Stage primaryStage, final Scene gameScene
        ) {
        rootPane.getChildren().clear();
        rootPane.setBackground(getBackground(primaryStage));
        resizeText(primaryStage);
        addMainBox(primaryStage, gameScene);

        ChangeListener<Number> resizeListener =
        (observable, oldValue, newValue) -> {
            rootPane.getChildren().clear();
            rootPane.setBackground(getBackground(primaryStage));
            resizeText(primaryStage);
            addMainBox(primaryStage, gameScene);
        };

        primaryStage.widthProperty().addListener(resizeListener);
        primaryStage.heightProperty().addListener(resizeListener);
    }

    @Override
    protected void resizeText(final Stage primaryStage) {
        rootPane.getChildren().clear();
        final double variationMainBoxSpacing = 4;
        setResizeVar(primaryStage, variationMainBoxSpacing);
    }

    @Override
    protected void addMainBox(final Stage primaryStage, final Scene gameScene) {
        final int textAutoSpacing = 20;
        final int subBoxAutoSpacing = 40;
        final int optionAutoSpacing = textAutoSpacing;

        Text gameSelection = createText("Game Selection", getTitleAutoSize());
        HBox title = createHBox(0, Pos.CENTER, gameSelection);

        Text select1 = createText(">", getTextAutoSize());
        Text versus = createText("Versus", getTextAutoSize());
        HBox option1 = createHBox(
            textAutoSpacing, Pos.CENTER, select1, versus
        );

        Text select2 = createText(">", getTextAutoSize());
        Text coop = createText("Coop", getTextAutoSize());
        HBox option2 = createHBox(
            textAutoSpacing, Pos.CENTER, select2, coop
        );

        Text selectback = createText(">", getTextAutoSize());
        Text back = createText("Back", getTextAutoSize());
        HBox optionBack = createHBox(
            textAutoSpacing, Pos.BOTTOM_LEFT, selectback, back
        );

        VBox optionBox = createVBox(
            optionAutoSpacing, Pos.CENTER, option1, option2
        );
        VBox subBox = createVBox(
            subBoxAutoSpacing, Pos.CENTER, title, optionBox
        );
        mainBox = createVBox(getMainBoxAutoSpacing(), Pos.BOTTOM_LEFT);

        mainBox.getChildren().addAll(subBox, optionBack);

        final int marginSub = 50;
        final int marginBack = 10;

        VBox.setVgrow(subBox, Priority.ALWAYS);
        HBox.setMargin(subBox, new Insets(0, 0, marginSub, 0));
        HBox.setMargin(
            optionBack,
            new Insets(
                0, marginBack, marginBack, marginBack
            )
        );

        rootPane.getChildren().add(mainBox);

        Text[] choices = {select1, select2, selectback};
        Text[] options = {versus, coop, back};

        addEventHandlers(primaryStage, gameScene, choices, options);
    }

    @Override
    protected void addEventHandlers(
        final Stage primaryStage, final Scene gameScene,
        final Text[] choices, final Text[] options
        ) {
        EventHandler<MouseEvent> mouseClick0 = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                options[0].removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                startLevel(primaryStage);
            }
        };
        EventHandler<MouseEvent> mouseClick1 = new EventHandler<>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                options[1].removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                startLevel(primaryStage);
            }
        };

        // TODO : Niveau versus
        options[0].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick0);
        // TODO : Niveau coop
        options[1].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick1);
        options[2].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            Menu quit = new Menu();
            quit.start(primaryStage);
        });

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
                    Menu menu = new Menu();
                    menu.start(primaryStage);
                } else {
                    startLevel(primaryStage);
                }
            }
        });
    }
}
