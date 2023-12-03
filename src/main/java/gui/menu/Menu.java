package gui.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Resources;

public abstract class Menu extends Application {
    protected static final int INITIAL_TEXT_SIZE = 45;
    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    /** Stage. */
    private static Stage mainStage;

    private BorderPane menuPane;
    private Scene menuScene;

    protected Menu() {
        menuPane = new BorderPane();
        menuPane.setBackground(getBackground());

        menuScene = new Scene(
            menuPane,
            mainStage.getScene().getWidth(),
            mainStage.getScene().getHeight()
        );
        menuScene.setFill(Color.BLACK);

        addMainBox();
        addEventHandlers(getMenuScene());
    }

    /**
     * Initialise la scène en question et permet d'actualiser sa taille
     * lorque la méthode est appelée.
     */
    protected void setScene() {
        menuScene.setRoot(new HBox());
        menuScene = new Scene(
            menuPane,
            mainStage.getScene().getWidth(),
            mainStage.getScene().getHeight()
        );
        menuScene.setFill(Color.BLACK);

        addEventHandlers(menuScene);
    }

    /**
     * Ajoute les options à la scène {@link #menuScene}.
     */
    protected void addMainBox() { }

    /**
     * Ajoute les différents{@code EventHandler}(souris, clavier).
     * @param scene
     */
    protected void addEventHandlers(final Scene scene) { }

    /**
     * Relie la taille du texte à la taille de la fenêtre.
     */
    protected void bindTextSizeToScene() {
        final int scale = 30;
        final int titleScale = 50;
        final String fxFontSize = "-fx-font-size: ";

        fontSize.bind(
            getMenuScene().widthProperty().divide(scale).add(
                getMenuScene().heightProperty().divide(scale)
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

    /** Permet d'avoir le background.
     * @return {@code background}
     */
    protected static Background getBackground() {
        Image image = Resources.getImage("background");

        BackgroundImage backgroundImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                mainStage.getWidth(),
                mainStage.getHeight(),
                false,
                false,
                true,
                false
            ));
        return new Background(backgroundImage);
    }

    /**
     * Méthode qui retourne un{@code Text}initialisé avec les paramètres
     * {@code str}et{@code size}.
     * @param str  Le texte contenu dans le nouveau{@code Text}
     * @param size La taille du{@code Text}
     * @return {@code Text}
     */
    public static Text createText(final String str, final int size) {
        Text text = new Text(str);
        text.setFill(Color.WHITE);
        text.setFont(Font.loadFont(Resources.getPathOrContent("small_pixel-7"), size));
        return text;
    }

    /**
     * Méthode qui retourne un{@code VBox}initialisé avec les paramètres
     * {@code spacing},{@code position}et{@code nodes}.
     * @param spacing  L'espace entre les différents{@code Node}
     * @param position L'alignement des{@code Node}
     * @param nodes    Les{@code Node}à mettre dans la{@code VBox}
     * @return {@code VBox}
     */
    public static VBox createVBox(
            final int spacing, final Pos position, final Node... nodes) {
        VBox box = new VBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(position);
        return box;
    }

    /**
     * Méthode qui retourne un{@code HBox}initialisé avec les paramètres
     * {@code spacing},{@code position}et{@code nodes}.
     * @param spacing  L'espace entre les différents{@code Node}
     * @param position L'alignement des{@code Node}
     * @param nodes    Les{@code Node}à mettre dans la{@code HBox}
     * @return {@code HBox}
     */
    public static HBox createHBox(
        final int spacing, final Pos position, final Node... nodes
        ) {
        HBox box = new HBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(position);
        return box;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(final Stage mainStage) {
        Menu.mainStage = mainStage;
    }

    public final BorderPane getMenuPane() {
        return menuPane;
    }

    public final Scene getMenuScene() {
        return menuScene;
    }

    public final DoubleProperty getFontSize() {
        return fontSize;
    }

    public final Text getTitleText() {
        return titleText;
    }

    public final List<HBox> getOptions() {
        return options;
    }

    public final List<Text> getSelects() {
        return selects;
    }

    public final List<Text> getChoices() {
        return choices;
    }

    public final void setTitleText(final Text titleText) {
        this.titleText = titleText;
    }
}
