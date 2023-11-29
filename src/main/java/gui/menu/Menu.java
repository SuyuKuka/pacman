package gui.menu;

import java.util.ArrayList;

import gui.GameView;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
import utils.Music;
import utils.Resources;

public class Menu extends Application {
    /** Stage. */
    private static Stage mainStage;

    /** Root pane. */
    private BorderPane rootPane;

    private BorderPane menuPane;
    private Scene menuScene;

    /** Entier indiquant l'option choisie dans les menus. */
    private static int selected = 0;

    protected static final int INITIAL_TEXT_SIZE = 45;

    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    public static final Menu INSTANCE = new Menu();

    protected Menu() {
    }

    /**
     * Méthode appelée à la fin de l'intro ou lorsqu'on revient
     * au menu principal.
     */
    @Override
    public void start(final Stage primaryStage) {
        setMainStage(primaryStage);
        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(Resources.getImage("pacman"));

        menuPane = new BorderPane();
        menuPane.setBackground(getBackground());

        menuScene = new Scene(
            menuPane,
            primaryStage.getScene().getWidth(),
            primaryStage.getScene().getHeight()
        );
        menuScene.setFill(Color.BLACK);

        addMainBox();
        addEventHandlers(menuScene);

        primaryStage.setScene(menuScene);
        primaryStage.show();

        Solo.INSTANCE.start(primaryStage);
        Multiplayer.INSTANCE.start(primaryStage);
        Settings.INSTANCE.start(primaryStage);

        // Musique de fond jouée en boucle
        Music.playLoopingBgMusic("bgm");
    }

    /**
     * Initialise la scène en question et permet d'actualiser sa taille
     * lorque la méthode est appelée.
     */
    public void setScene() {
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
     * Affiche le menu suite à l'appel de la méthode.
     */
    public void toMenu() {
        if (GameView.INSTANCE.isInGame()) {
            Music.stopBackgroundMusic();
        }
        GameView.INSTANCE.stopAnimation();
        GameView.INSTANCE.resetInstance();
        setScene();
        mainStage.setScene(INSTANCE.menuScene);

        // Musique de fond jouée en boucle
        if (!Music.isBgmPlaying()) {
            Music.playLoopingBgMusic("bgm");
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
     * Ajoute les options à la scène{@code gameScene}.
     */
    protected void addMainBox() {
        final int titleSize = 100;
        final int spacingSize = 20;

        titleText = createText("Mac-Pan-Pac-Man", titleSize);
        VBox titleBox = createVBox(0, Pos.TOP_CENTER, titleText);

        Text select0 = createText(">", fontSize.getValue().intValue());
        Text solo = createText("Solo", fontSize.getValue().intValue());

        Text select1 = createText(">", fontSize.getValue().intValue());
        Text multiplayer = createText("Multiplayer", fontSize.getValue().intValue());

        Text select2 = createText(">", fontSize.getValue().intValue());
        Text settings = createText("Settings", fontSize.getValue().intValue());

        Text select3 = createText(">", fontSize.getValue().intValue());
        Text quit = createText("Quit", fontSize.getValue().intValue());

        if (options.isEmpty()) {
            options.add(0, createHBox(
                spacingSize, Pos.CENTER, select0, solo
            ));
            options.add(1, createHBox(
                spacingSize, Pos.CENTER, select1, multiplayer
            ));
            options.add(2, createHBox(
                spacingSize, Pos.CENTER, select2, settings
            ));
            //CHECKSTYLE:OFF
            options.add(3, createHBox(
                spacingSize, Pos.CENTER, select3, quit
            ));
        }

        VBox optionBox = createVBox(
            spacingSize,
            Pos.CENTER,
            options.get(0), options.get(1), options.get(2), options.get(3)
        );

        if (selects.isEmpty() && choices.isEmpty()) {
            selects.add(0, select0);
            selects.add(1, select1);
            selects.add(2, select2);
            selects.add(3, select3);

            choices.add(0, solo);
            choices.add(1, multiplayer);
            choices.add(2, settings);
            choices.add(3, quit);
        }

        bindTextSizeToScene();

        menuPane.setTop(titleBox);
        menuPane.setCenter(optionBox);
        //CHECKSTYLE:OFF
        BorderPane.setMargin(titleBox, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    /**
     * Relie la taille du texte à la taille de la fenêtre.
     */
    protected void bindTextSizeToScene() {
        final int scale = 30;
        final int titleScale = 50;
        final String fxFontSize = "-fx-font-size: ";

        fontSize.bind(
            menuScene.widthProperty().divide(scale).add(menuScene.heightProperty().divide(scale))
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

    /**
     * Ajoute les différents{@code EventHandler}(souris, clavier).
     * @param gameScene
     */
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(selects, choices, gameScene, false);

        options.get(0).setOnMouseClicked(mouseEvent -> {
            Solo.INSTANCE.toSolo();
            selector.getKeySelector().resetSelect();
        });
        options.get(1).setOnMouseClicked(mouseEvent -> {
            Multiplayer.INSTANCE.toMulti();
            selector.getKeySelector().resetSelect();
        });
        options.get(2).setOnMouseClicked(mouseEvent -> {
            Settings.INSTANCE.toSettings();
            selector.getKeySelector().resetSelect();
        });
        //CHECKSTYLE:OFF
        options.get(3).setOnMouseClicked(mouseEvent -> {
            mainStage.close();
            System.exit(0);
        });
        //CHECKSTYLE:ON

        gameScene.setOnKeyPressed(keyEvent -> {
            final int soloCase = 0;
            final int multiplayerCase = 1;
            final int settingsCase = 2;
            final int quitCase = 3;
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (selected) {
                    case soloCase:
                        Solo.INSTANCE.toSolo();
                        break;

                    case multiplayerCase:
                        Multiplayer.INSTANCE.toMulti();
                        break;

                    case settingsCase:
                        Settings.INSTANCE.toSettings();
                        break;

                    case quitCase:
                        mainStage.close();
                        System.exit(0);
                        break;

                    default:
                        break;
                }
                selector.getKeySelector().resetSelect();
            }
        });
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

    /**
     * Retourne l'indice de l'option choisie.
     * @return {@code int}
     */
    public static int getSelected() {
        return selected;
    }

    public static void setSelected(final int selected) {
        Menu.selected = selected;
    }

    public final BorderPane getRootPane() {
        return rootPane;
    }

    public final void setRootPane(final BorderPane rootPane) {
        this.rootPane = rootPane;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(final Stage mainStage) {
        Menu.mainStage = mainStage;
    }
}
