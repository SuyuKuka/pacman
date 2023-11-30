package gui.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public final class Settings extends Menu {
    public static final Settings INSTANCE = new Settings();

    private Settings() {
    }

    @Override
    public void start(final Stage primaryStage) {
    }

    /**
     * Affiche le menu Settings.
     */
    public void toSettings() {
        setScene();
        getMainStage().setScene(getMenuScene());
    }

    @Override
    protected void addMainBox() {
        final int titleSize = 100;
        final int spacingSize = 20;

        setTitleText(createText("Settings", titleSize));
        HBox title = createHBox(0, Pos.TOP_CENTER, getTitleText());

        // Volume control
        Text volumeText = createText("Volume", getFontSize().getValue().intValue());
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

        Text selectback = createText(">", getFontSize().getValue().intValue());
        Text back = createText("Back", getFontSize().getValue().intValue());

        if (getOptions().isEmpty() && getSelects().isEmpty() && getChoices().isEmpty()) {
            getOptions().add(0, createHBox(spacingSize, Pos.BOTTOM_LEFT, selectback, back));

            getSelects().add(0, selectback);

            getChoices().add(0, back);
        }

        bindTextSizeToScene();

        getMenuPane().setTop(title);
        getMenuPane().setCenter(volumeBox);
        getMenuPane().setBottom(getOptions().get(0));
        //CHECKSTYLE:OFF
        BorderPane.setMargin(title, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(getSelects(), getChoices(), gameScene, false);
        getOptions().get(0).setOnMouseClicked(event -> {
            MainMenu.INSTANCE.toMenu();
            selector.getKeySelector().resetSelect();
        });

        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                MainMenu.INSTANCE.toMenu();
            }
            selector.getKeySelector().resetSelect();
        });
    }
}
