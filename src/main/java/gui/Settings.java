package gui;

import gui.menu.Menu;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public final class Settings extends Menu {
    private StackPane rootPane = new StackPane();
    private VBox mainBox;
    private Slider volumeSlider;

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
        final int mainBoxSpacing = 150;
        final int optionSpacing = 20;

        Text title = createText("Settings", getTitleAutoSize());
        HBox titleBox = createHBox(0, Pos.CENTER, title);

        // Volume control
        Text volumeText = createText("Volume", getTextAutoSize());
        // Range [0, 100] with default value 50
        final int minVol = 0;
        final int maxVol = 100;
        volumeSlider = new Slider(minVol, maxVol, Music.getVolume() * maxVol);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int volume = newValue.intValue();
            final float f = 100.0f;
            Music.setVolume(volume / f);
        });
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(10);

        HBox volumeBox = createHBox(optionSpacing, Pos.CENTER, volumeText, volumeSlider);

        Text back = createText("Back", getTextAutoSize());
        back.setOnMouseClicked(event -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });

        VBox optionsBox = createVBox(optionSpacing, Pos.CENTER, volumeBox, back);

        mainBox = createVBox(mainBoxSpacing, Pos.CENTER, titleBox, optionsBox);

        rootPane.getChildren().add(mainBox);
    }
}
