package gui.menu;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
    private StackPane rootPane = new StackPane();
    /** mainBox de Menu.*/
    private VBox mainBox;
    /** Set auto la taille du titre. */
    private static int titleAutoSize;
    /** Set auto la taille du texte. */
    private static int textAutoSize;
    /** Set auto l'espacement du texte. */
    private static int mainBoxAutoSpacing;
    /** Variable pour suivre si la musique a déjà été démarrée. */
    private static boolean musicStarted = false;

    // Constantes
    protected static final double VARIATION = 0.2;
    protected static final double VARIATION_TITLE_HEIGHT = 3;
    protected static final double VARIATION_TITLE_WIDTH = 6;
    protected static final double VARIATION_TITLE_SPACING = 0.5;
    protected static final double VARIATION_TEXT_HEIGHT = 6;
    protected static final double VARIATION_TEXT_WIDTH = 10;

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

    protected static void setMainBoxAutoSpacing(final int mainBoxAutoSpacing) {
        Menu.mainBoxAutoSpacing = mainBoxAutoSpacing;
    }

    protected static void setTitleAutoSize(final int titleAutoSize) {
        Menu.titleAutoSize = titleAutoSize;
    }

    protected static void setTextAutoSize(final int textAutoSize) {
        Menu.textAutoSize = textAutoSize;
    }

    public static void setMusicStarted(final boolean musicStarted) {
        Menu.musicStarted = musicStarted;
    }

    protected static int getTitleAutoSize() {
        return titleAutoSize;
    }

    protected static int getTextAutoSize() {
        return textAutoSize;
    }

    protected static int getMainBoxAutoSpacing() {
        return mainBoxAutoSpacing;
    }

    /**
     * Méthode appelée à la fin de l'intro ou lorsqu'on revient
     * au menu principal.
     */
    @Override
    public void start(final Stage primaryStage) {
        //CHECKSTYLE:OFF
        setTitleAutoSize(68);
        setTextAutoSize(38);
        //CHECKSTYLE:ON
        final int sceneWidth = 16;
        final int sceneHeight = 39;
        final int mainBoxSpacing = 100;

        setMainBoxAutoSpacing(mainBoxSpacing);

        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(Resources.getImage("pacman"));

        Scene gameScene = new Scene(
            rootPane,
            primaryStage.getWidth() - sceneWidth,
            primaryStage.getHeight() - sceneHeight
        );

        rootPane.getChildren().remove(mainBox);
        resizeAndShowMenu(primaryStage, gameScene);

        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();

        // Musique de fond jouée en boucle
        if (!musicStarted) {
            Music.playLoopingBgMusic("bgm");
            setMusicStarted(true);
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

    /** Permet d'avoir le background.
     * @param primaryStage
     * @return {@code background}
     */
    protected static Background getBackground(final Stage primaryStage) {
        Image image = Resources.getImage("background");

        BackgroundImage backgroundImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
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
     * Méthode qui affiche le menu. Le texte est resized lorsque la fenêtre est resized.
     * @param primaryStage
     * @param gameScene
     */
    protected void resizeAndShowMenu(
        final Stage primaryStage, final Scene gameScene
        ) {
        rootPane.setBackground(getBackground(primaryStage));
        resizeText(primaryStage);
        addMainBox(primaryStage, gameScene);

        ChangeListener<Number> resizeListener =
        (observable, oldValue, newValue) -> {
            rootPane.setBackground(getBackground(primaryStage));
            resizeText(primaryStage);
            addMainBox(primaryStage, gameScene);
        };

        primaryStage.widthProperty().addListener(resizeListener);
        primaryStage.heightProperty().addListener(resizeListener);
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
     * Resize le texte lorsque la fenêtre est resized.
     * @param primaryStage
     */
    protected void resizeText(final Stage primaryStage) {
        rootPane.getChildren().clear();
        setResizeVar(primaryStage, 0);
    }

    protected static void setResizeVar(final Stage primaryStage, final double mainBoxSpacing) {
        titleAutoSize = (int) (
            (primaryStage.getHeight() / VARIATION_TITLE_HEIGHT
            + primaryStage.getWidth() / VARIATION_TITLE_WIDTH)
            * VARIATION
        );
        textAutoSize = (int) (
            (primaryStage.getHeight() / VARIATION_TEXT_HEIGHT
            + primaryStage.getWidth() / VARIATION_TEXT_WIDTH)
            * VARIATION
        );
        mainBoxAutoSpacing = (int) (
            (primaryStage.getHeight() / VARIATION_TEXT_HEIGHT)
            * VARIATION_TITLE_SPACING);
    }

    /**
     * Ajoute la {@link #mainBox} à la scène{@code gameScene}.
     * @param primaryStage
     * @param gameScene
     */
    protected void addMainBox(final Stage primaryStage, final Scene gameScene) {
        final int titleAutoSpacing = 100;
        final int textAutoSpacing = 20;
        final int optionAutoSpacing = textAutoSpacing;

        Text titre = createText("Mac-Pan-Pac-Man", titleAutoSize);
        HBox title = createHBox(titleAutoSpacing, Pos.CENTER, titre);
        // Lance la vidéo du lore?
        titre.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            // Lancement de l'histoire
        });

        Text select0 = createText(">", textAutoSize);
        Text solo = createText("Solo", textAutoSize);
        HBox option0 = createHBox(textAutoSpacing, Pos.CENTER, select0, solo);

        Text select1 = createText(">", textAutoSize);
        Text multiplayer = createText("Multiplayer", textAutoSize);
        HBox option1 = createHBox(
            textAutoSpacing, Pos.CENTER, select1, multiplayer
        );

        Text select2 = createText(">", textAutoSize);
        Text settings = createText("Settings", textAutoSize);
        HBox option2 = createHBox(
            textAutoSpacing, Pos.CENTER, select2, settings
        );

        Text select3 = createText(">", textAutoSize);
        Text quit = createText("Quit", textAutoSize);
        HBox option3 = createHBox(textAutoSpacing, Pos.CENTER, select3, quit);

        VBox optionBox = createVBox(
            optionAutoSpacing,
            Pos.CENTER, option0, option1, option2, option3
        );
        mainBox = createVBox(mainBoxAutoSpacing, Pos.CENTER, title, optionBox);

        rootPane.getChildren().add(mainBox);

        Text[] choices = {select0, select1, select2, select3};
        Text[] options = {solo, multiplayer, settings, quit};
        addEventHandlers(primaryStage, gameScene, choices, options);
    }

    /**
     * Ajoute les différents{@code EventHandler}(souris, clavier).
     * @param primaryStage
     * @param gameScene
     * @param choices
     * @param options
     */
    protected void addEventHandlers(
        final Stage primaryStage, final Scene gameScene, final Text[] choices, final Text[] options
        ) {
        Solo soloMenu = new Solo();
        Multiplayer multiMenu = new Multiplayer();
        Settings settMenu = new Settings();

        //CHECKSTYLE:OFF
        options[0].addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            soloMenu.start(primaryStage);
        });
        options[1].addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            multiMenu.start(primaryStage);
        });
        options[2].addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            settMenu.start(primaryStage);
        });
        options[3].addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            primaryStage.close();
            System.exit(0);
        });
        //CHECKSTYLE:ON

        KeySelector keySel = new KeySelector(
            choices, primaryStage, false
        );
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySel::handle);

        int select = 0;
        for (Text text : options) {
            MouseSelector m = new MouseSelector(choices, select++);
            text.setOnMouseEntered(m::handle);
        }

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
    }

    public static class KeySelector implements EventHandler<KeyEvent> {
        /** Les ">". */
        private Text[] choices;
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
         * @param newChoices
         * @param newPrimaryStage
         * @param pauseMenu
         */
        public KeySelector(
            final Text[] newChoices,
            final Stage newPrimaryStage,
            final boolean pauseMenu
            ) {
            choices = newChoices;
            primaryStage = newPrimaryStage;
            isPauseMenu = pauseMenu;

            setSelected(0);
            maxChoice = choices.length - 1;

            for (int i = 1; i < maxChoice + 1; i++) {
                choices[i].setVisible(false);
            }
        }


        /**
         * Lorsque la flèche du haut ou bas est appuyée,
         * le ">" actuel est mis en invisible et le prochain est révélé.
         * Si on appuie sur échap, on revient au menu principal.
         */
        @Override
        public void handle(final KeyEvent keyEvent) {
            if (isPauseMenu && !gui.InGameGui.INSTANCE.isPaused()) {
                return;
            }

            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                choices[selected].setVisible(false);
                setSelected(selected - 1);
                if (selected < 0) {
                    setSelected(maxChoice);
                }
                choices[selected].setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                choices[selected].setVisible(false);
                setSelected(selected + 1);
                if (selected > maxChoice) {
                    setSelected(0);
                }
                choices[selected].setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE && !isPauseMenu) {
                Menu menu = new Menu();
                menu.start(primaryStage);
            }
        }
    }

    public static class MouseSelector implements EventHandler<MouseEvent> {
        /** Les ">". */
        private Text[] choices;
        /** L'indice du choix. */
        private int select;

        /**
         * Constructeur MouseSelector.
         * @param newChoices
         * @param newSelect
         */
        public MouseSelector(final Text[] newChoices, final int newSelect) {
            choices = newChoices;
            select = newSelect;
        }

        /**
         * Affiche ">" lorsqu'on survole la souris sur une option.
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            for (int i = 0; i < choices.length; i++) {
                choices[i].setVisible(false);
            }
            setSelected(select);
            choices[selected].setVisible(true);
        }
    }
}
