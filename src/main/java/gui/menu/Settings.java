package gui.menu;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public final class Settings extends Menu {
    private StackPane rootPane = new StackPane();

    @Override
    public void start(final Stage primaryStage) {
        final int sceneWidth = 16;
        final int sceneHeight = 39;

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
    protected void addMainBox(final Stage primaryStage, final Scene gameScene) {
        final int textAutoSpacing = 20;
        final int optionSpacing = 20;
        final int titleHeightVar = 4;

        Text title = createText("Settings", getTitleAutoSize());
        HBox titleBox = createHBox(0, Pos.TOP_CENTER, title);
        titleBox.setTranslateY(primaryStage.getHeight() / titleHeightVar);

        // Volume control
        Text volumeText = createText("Volume", getTextAutoSize());
        // Range [0, 100] with default value 50
        final int minVol = 0;
        final int maxVol = 100;
        Slider volumeSlider = new Slider(minVol, maxVol, Music.getVolume() * maxVol);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int volume = newValue.intValue();
            final float f = 100.0f;
            Music.setVolume(volume / f);
        });
        //CHECKSTYLE:OFF
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(10);
        //CHECKSTYLE:ON

        HBox volumeBox = createHBox(optionSpacing, Pos.CENTER, volumeText, volumeSlider);

        Text selectback = createText(">", getTextAutoSize());
        Text back = createText("Back", getTextAutoSize());
        HBox optionBack = createHBox(
            textAutoSpacing, Pos.BOTTOM_LEFT, selectback, back
        );

        rootPane.getChildren().addAll(titleBox, volumeBox, optionBack);

        Text[] choices = {selectback};
        Text[] options = {back};
        addEventHandlers(primaryStage, gameScene, choices, options);
    }

    @Override
    protected void addEventHandlers(
        final Stage primaryStage, final Scene gameScene,
        final Text[] choices, final Text[] options
        ) {
        options[0].setOnMouseClicked(event -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                Menu menu = new Menu();
                menu.start(primaryStage);
            }
        });
    }
}
