package gui.menu;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    /** Entier indiquant l'option choisie dans les menus. */
    private static int selected = 0;
    /** Créer un rootPane général pour le background. */
    private BorderPane rootPane = new BorderPane();

    protected static final int INITIAL_TEXT_SIZE = 45;

    private DoubleProperty fontSize = new SimpleDoubleProperty(INITIAL_TEXT_SIZE);
    private Text titleText;
    private ArrayList<HBox> options = new ArrayList<>();
    private ArrayList<Text> selects = new ArrayList<>();
    private ArrayList<Text> choices = new ArrayList<>();

    /** Variable pour suivre si la musique a déjà été démarrée. */
    private static boolean musicStarted = false;

    /**
     * Méthode appelée à la fin de l'intro ou lorsqu'on revient
     * au menu principal.
     */
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(Resources.getImage("pacman"));

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

        // Musique de fond jouée en boucle
        if (!musicStarted) {
            Music.playLoopingBgMusic("bgm");
            setMusicStarted(true);
        }
    }

    /** Permet d'avoir le background.
     * @param primaryStage
     * @return {@code background}
     */
    protected static Background getBackground(final Stage primaryStage) {
        Image image = Resources.getImage("background");

        BackgroundImage backgroundImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(
                primaryStage.getWidth(),
                primaryStage.getHeight(),
                false,
                false,
                true,
                false
            ));
        return new Background(backgroundImage);
    }

    /**
     * Ajoute la {@link #mainBox} à la scène{@code gameScene}.
     * @param gameScene
     */
    protected void addMainBox(final Scene gameScene) {
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

        VBox optionBox = createVBox(
            spacingSize,
            Pos.CENTER,
            options.get(0), options.get(1), options.get(2), options.get(3)
        );

        selects.add(0, select0);
        selects.add(1, select1);
        selects.add(2, select2);
        selects.add(3, select3);

        choices.add(0, solo);
        choices.add(1, multiplayer);
        choices.add(2, settings);
        choices.add(3, quit);

        bindTextSizeToScene(gameScene);

        rootPane.setTop(titleBox);
        rootPane.setCenter(optionBox);
        //CHECKSTYLE:OFF
        BorderPane.setMargin(titleBox, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    /**
     * Relie la taille du texte à la taille de la fenêtre.
     * @param gameScene
     */
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

    /**
     * Ajoute les différents{@code EventHandler}(souris, clavier).
     * @param primaryStage
     * @param gameScene
     */
    protected void addEventHandlers(final Stage primaryStage, final Scene gameScene) {
        Solo soloMenu = new Solo();
        Multiplayer multiMenu = new Multiplayer();
        Settings settMenu = new Settings();

        options.get(0).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            soloMenu.start(primaryStage);
        });
        options.get(1).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            multiMenu.start(primaryStage);
        });
        options.get(2).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            settMenu.start(primaryStage);
        });
        //CHECKSTYLE:OFF
        options.get(3).addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            primaryStage.close();
            System.exit(0);
        });
        //CHECKSTYLE:ON

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            final int soloCase = 0;
            final int multiplayerCase = 1;
            final int settingsCase = 2;
            final int quitCase = 3;
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (selected) {
                    case soloCase:
                        soloMenu.start(primaryStage);
                        break;

                    case multiplayerCase:
                        multiMenu.start(primaryStage);
                        break;

                    case settingsCase:
                        settMenu.start(primaryStage);
                        break;

                    case quitCase:
                        primaryStage.close();
                        System.exit(0);
                        break;

                    default:
                        break;
                }
            }
        });

        KeySelector keySel = new KeySelector(
            selects, primaryStage, false
        );
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : choices) {
            MouseSelector m = new MouseSelector(selects, select++);
            text.setOnMouseEntered(m::handle);
        }
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
        text.setFont(Font.loadFont(Resources.getFile("small_pixel-7"), size));
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

    protected static void setSelected(final int selected) {
        Menu.selected = selected;
    }

    public static void setMusicStarted(final boolean musicStarted) {
        Menu.musicStarted = musicStarted;
    }

    public static class KeySelector implements EventHandler<KeyEvent> {
        /** Les ">". */
        private ArrayList<Text> selects;
        /** Le{@code Stage} principal.*/
        private Stage primaryStage;
        /** Plus grand indice de choix. */
        private int maxChoice;
        /**
         * {@code true}si l'{@code EventHandler}est pour
         * le menu pause.
         */
        private boolean isPauseMenu;

        /**
         * Constructeur KeySelector.
         * @param newSelects
         * @param newPrimaryStage
         * @param pauseMenu
         */
        public KeySelector(
            final List<Text> newSelects,
            final Stage newPrimaryStage,
            final boolean pauseMenu
            ) {
            selects = new ArrayList<>(newSelects);
            primaryStage = newPrimaryStage;
            isPauseMenu = pauseMenu;

            setSelected(0);
            maxChoice = selects.size() - 1;

            for (int i = 1; i <= maxChoice; i++) {
                selects.get(i).setVisible(false);
            }
        }


        /**
         * Lorsque la flèche du haut ou bas est appuyée,
         * le ">" actuel est mis en invisible et le prochain est révélé.
         * Si on appuie sur échap, on revient au menu principal.
         */
        @Override
        public void handle(final KeyEvent keyEvent) {
            if (isPauseMenu && !gui.InGameGui.isPaused()) {
                return;
            }

            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                selects.get(selected).setVisible(false);
                setSelected(selected - 1);
                if (selected < 0) {
                    setSelected(maxChoice);
                }
                selects.get(selected).setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                selects.get(selected).setVisible(false);
                setSelected(selected + 1);
                if (selected > maxChoice) {
                    setSelected(0);
                }
                selects.get(selected).setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE && !isPauseMenu) {
                Menu menu = new Menu();
                menu.start(primaryStage);
            }
        }
    }

    public static class MouseSelector implements EventHandler<MouseEvent> {
        /** Les ">". */
        private ArrayList<Text> selects;
        /** L'indice du choix. */
        private int select;

        /**
         * Constructeur MouseSelector.
         * @param newSelects
         * @param newSelect
         */
        public MouseSelector(final List<Text> newSelects, final int newSelect) {
            selects = new ArrayList<>(newSelects);
            select = newSelect;
        }

        /**
         * Affiche ">" lorsqu'on survole la souris sur une option.
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            for (int i = 0; i < selects.size(); i++) {
                selects.get(i).setVisible(false);
            }
            setSelected(select);
            selects.get(selected).setVisible(true);
        }
    }
}
