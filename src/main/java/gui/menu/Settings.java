package gui.menu;

import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public final class Settings extends Menu {
    private BorderPane rootPane = new BorderPane();

    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    @Override
    public void start(final Stage primaryStage) {
        Scene gameScene = new Scene(
            rootPane,
            primaryStage.getScene() == null
            ? primaryStage.getWidth() : primaryStage.getScene().getWidth(),
            primaryStage.getScene() == null
            ? primaryStage.getHeight() : primaryStage.getScene().getHeight()
        );
        rootPane.prefWidthProperty().bind(gameScene.widthProperty());
        rootPane.prefHeightProperty().bind(gameScene.heightProperty());
        rootPane.setBackground(getBackground(primaryStage));

        addMainBox(gameScene);
        addEventHandlers(primaryStage, gameScene);

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }

    @Override
    protected void addMainBox(final Scene gameScene) {
        final int titleSize = 100;
        final int spacingSize = 20;

        titleText = createText("Settings", titleSize);
        HBox title = createHBox(0, Pos.TOP_CENTER, titleText);

        // Volume control
        Text volumeText = createText("Volume", fontSize.getValue().intValue());
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

        HBox volumeBox = createHBox(spacingSize, Pos.CENTER, volumeText, volumeSlider);

        Text selectback = createText(">", fontSize.getValue().intValue());
        Text back = createText("Back", fontSize.getValue().intValue());

        options.add(0, createHBox(spacingSize, Pos.BOTTOM_LEFT, selectback, back));

        selects.add(0, selectback);

        choices.add(0, volumeText);
        choices.add(1, back);

        bindTextSizeToScene(gameScene);

        rootPane.setTop(title);
        rootPane.setCenter(volumeBox);
        rootPane.setBottom(options.get(0));
        //CHECKSTYLE:OFF
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Stage primaryStage, final Scene gameScene) {
        options.get(0).setOnMouseClicked(
            event -> Menu.toMenu(primaryStage)
        );

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                Menu.toMenu(primaryStage);
            }
        });

        new Selector(selects, choices, primaryStage, gameScene, false);
    }

    @Override
    protected void bindTextSizeToScene(final Scene gameScene) {
        final int scale = 30;
        final int titleScale = 50;
        final String fxFontSize = "-fx-font-size: ";

        fontSize.bind(
            gameScene.widthProperty().divide(scale).add(gameScene.heightProperty().divide(scale))
        );

        titleText.styleProperty().bind(
            Bindings.concat(fxFontSize, fontSize.add(titleScale).asString())
        );
        for (Text text : choices) {
            text.styleProperty().bind(Bindings.concat(fxFontSize, fontSize.asString()));
        }
        for (Text text : selects) {
            text.styleProperty().bind(Bindings.concat(fxFontSize, fontSize.asString()));
        }
    }
}
