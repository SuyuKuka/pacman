package gui.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public final class Settings extends Menu {
    private HBox bgMusicVolumeBox;
    private HBox effectsVolumeBox;
    private final int spacingSize = 20;
    private static final double FONT_VALUE = 0.6;
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
    protected void addOptionsToMenu() {
        final int titleSize = 100;

        setTitleText(createText("Settings", titleSize));
        HBox title = createHBox(0, Pos.TOP_CENTER, getTitleText());

        setupVolumeControl();

        VBox mainBox = new VBox(spacingSize);
        mainBox.setAlignment(Pos.CENTER);

        mainBox.getChildren().addAll(bgMusicVolumeBox, effectsVolumeBox);

        BorderPane.setMargin(title, new Insets(spacingSize, 0, spacingSize, 0));
        BorderPane.setMargin(mainBox, new Insets(spacingSize));

        getMenuPane().setTop(title);
        getMenuPane().setCenter(mainBox);

        setupBottomBackButton();
    }

    /**
     * Configure les contrôles de volume pour la musique de fond et les effets sonores.
     */
    private void setupVolumeControl() {
        //CHECKSTYLE:OFF
        Slider bgMusicVolumeSlider = new Slider(0, 100, Music.getBgMusicVolume() * 100);
        bgMusicVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) ->
            Music.setBgMusicVolume(newValue.floatValue() / 100)
        );

        Slider effectsVolumeSlider = new Slider(0, 100, Music.getEffectsVolume() * 100);
        effectsVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) ->
            Music.setEffectsVolume(newValue.floatValue() / 100)
        );

        bgMusicVolumeSlider.setMajorTickUnit(25);
        bgMusicVolumeSlider.setMinorTickCount(5);
        bgMusicVolumeSlider.setBlockIncrement(1);

        effectsVolumeSlider.setMajorTickUnit(25);
        effectsVolumeSlider.setMinorTickCount(5);
        effectsVolumeSlider.setBlockIncrement(1);
        //CHECKSTYLE:ON

        Text bgMusicTitle = createText(
            "Background Music Volume :", getFontSize().doubleValue() * FONT_VALUE
        );
        Text effectsTitle = createText(
            "Sound Effects Volume :", getFontSize().doubleValue() * FONT_VALUE
        );

        bgMusicVolumeBox = createHBox(
            spacingSize, Pos.CENTER, bgMusicTitle, bgMusicVolumeSlider
        );
        effectsVolumeBox = createHBox(
            spacingSize, Pos.CENTER, effectsTitle, effectsVolumeSlider
        );
    }

    /**
     * Configure le bouton de retour en bas de l'écran.
     */
    private void setupBottomBackButton() {
        Text selectback = createText(">", getFontSize().intValue());
        Text backText = createText("Back", getFontSize().intValue());

        backText.setFill(Color.WHITE);

        backText.setOnMouseEntered(event -> backText.setUnderline(true));
        backText.setOnMouseExited(event -> backText.setUnderline(false));

        backText.setOnMouseClicked(event -> {
            MainMenu.INSTANCE.toMenu();
        });

        HBox backButtonBox = createHBox(spacingSize, Pos.BOTTOM_LEFT, selectback, backText);

        if (getOptions().isEmpty() && getSelects().isEmpty() && getChoices().isEmpty()) {
            getOptions().add(0, backButtonBox);
            getSelects().add(0, selectback);
            getChoices().add(0, backText);
        }
        getMenuPane().setBottom(getOptions().get(0));
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
