

package gui;


import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;

public class Settings extends Menu {
    private StackPane background;
    private static StackPane rootPane;
    private static VBox mainBox;
    private Slider volumeSlider;

    public Settings(final StackPane newBackground) {
        this.background = newBackground;
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

        Text title = createText("Settings", titleSize);
        HBox titleBox = createHBox(titleSpacing, Pos.CENTER, title);

        // Volume control
        Text volumeText = createText("Volume", textSize);
        volumeSlider = new Slider(0, 100, Music.getVolume() * 100); // Range [0, 100] with default value 50
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int volume = newValue.intValue();
            // Assuming you want to display the volume somewhere, 
            // for example in a label named currentVolumeLabel
            // currentVolumeLabel.setText(volume + "%");
            Music.setVolume(volume / 100.0f);
        });
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(10);

        HBox volumeBox = createHBox(optionSpacing, Pos.CENTER, volumeText, volumeSlider);
    
        Text back = createText("Back", textSize);
        back.setOnMouseClicked(event -> {
            Menu menu = new Menu();
            menu.start(primaryStage);
        });

        VBox optionsBox = createVBox(optionSpacing, Pos.CENTER, volumeBox, back);

        mainBox = createVBox(mainBoxSpacing, Pos.CENTER, titleBox, optionsBox);

        rootPane.getChildren().addAll(background, mainBox);
        transitionRightToLeft(primaryStage, mainBox);

        Scene settingsScene = new Scene(rootPane, width, height);
        settingsScene.setFill(Color.BLACK);
        primaryStage.setScene(settingsScene);
        primaryStage.show();
    }
    
    // Remaining methods, including transitionRightToLeft, are unchanged...
}
