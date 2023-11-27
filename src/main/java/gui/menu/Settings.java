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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public final class Settings extends Menu {
    private BorderPane settingsPane;
    private Scene settingsScene;
    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    public static final Settings INSTANCE = new Settings();

    private Settings() {
    }

    @Override
    public void start(final Stage primaryStage) {
        settingsPane = new BorderPane();
        settingsPane.setBackground(getBackground());

        settingsScene = new Scene(
            settingsPane,
            primaryStage.getScene().getWidth(),
            primaryStage.getScene().getHeight()
        );
        settingsScene.setFill(Color.BLACK);

        addMainBox();
        addEventHandlers(settingsScene);
    }

    @Override
    public void setScene() {
        settingsScene.setRoot(new HBox());
        settingsScene = new Scene(
            settingsPane,
            Menu.getMainStage().getScene().getWidth(),
            Menu.getMainStage().getScene().getHeight()
        );
        settingsScene.setFill(Color.BLACK);

        addEventHandlers(settingsScene);
    }

    /**
     * Affiche le menu Settings.
     */
    public void toSettings() {
        setScene();
        Menu.getMainStage().setScene(settingsScene);
    }

    @Override
    protected void addMainBox() {
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

        if (options.isEmpty() && selects.isEmpty() && choices.isEmpty()) {
            options.add(0, createHBox(spacingSize, Pos.BOTTOM_LEFT, selectback, back));

            selects.add(0, selectback);

            choices.add(0, back);
        }

        bindTextSizeToScene();

        settingsPane.setTop(title);
        settingsPane.setCenter(volumeBox);
        settingsPane.setBottom(options.get(0));
        //CHECKSTYLE:OFF
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(selects, choices, gameScene, false);
        options.get(0).setOnMouseClicked(event -> {
            Menu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                Menu.INSTANCE.toMenu();
            }
            selector.getKeySelector().resetSelect();
        });
    }

    @Override
    protected void bindTextSizeToScene() {
        final int scale = 30;
        final int titleScale = 50;
        final String fxFontSize = "-fx-font-size: ";

        fontSize.bind(
            settingsScene.widthProperty().divide(scale).add(
                settingsScene.heightProperty().divide(scale)
            )
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
