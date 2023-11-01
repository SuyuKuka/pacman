package gui;

import javafx.application.Application;
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

    /**
     * Retourne l'indice de l'option choisie.
     * @return {@code int}
     */
    public static int getSelected() {
        return selected;
    }

    /** {@code StackPane}de l'arrière-plan. */
    private static StackPane backgroundPane;

    static {
        backgroundPane = new StackPane();
        backgroundPane.setBackground(getBackground());
    }

    /**
     * Méthode appelée à la fin de l'intro ou lorsqu'on revient
     * au menu principal.
     */
    @Override
    public void start(final Stage primaryStage) {
        final int titleSize = 68;
        final int titleSpacing = 100;
        final int textSize = 38;
        final int textSpacing = 20;
        final int optionSpacing = textSpacing;
        final int mainBoxSpacing = 100;
        final int width = 690;
        final int height = 510;

        primaryStage.setTitle("Mac-Pan-Pac-Man");
        primaryStage.getIcons().add(Resources.getImage("pacman"));

        StackPane rootPane = new StackPane();
        rootPane.setBackground(getBackground());

        Text titre = createText("Mac-Pan-Pac-Man", titleSize);
        HBox title = createHBox(titleSpacing, Pos.CENTER, titre);
        // Lance la vidéo du lore?
        titre.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            // Lancement de l'histoire
        });

        Text select0 = createText(">", textSize);
        Text solo = createText("Solo", textSize);
        HBox option0 = createHBox(textSpacing, Pos.CENTER, select0, solo);
        solo.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Solo soloMenu = new Solo(backgroundPane);
            soloMenu.start(primaryStage);
        });

        Text select1 = createText(">", textSize);
        Text multiplayer = createText("Multiplayer", textSize);
        HBox option1 = createHBox(
            textSpacing, Pos.CENTER, select1, multiplayer
        );
        multiplayer.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            Multiplayer multiMenu = new Multiplayer(backgroundPane);
            multiMenu.start(primaryStage);
        });

        Text select2 = createText(">", textSize);
        Text settings = createText("Settings", textSize);
        HBox option2 = createHBox(textSpacing, Pos.CENTER, select2, settings);
        settings.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            // TODO : Settings
        });

        Text select3 = createText(">", textSize);
        Text quit = createText("Quit", textSize);
        HBox option3 = createHBox(textSpacing, Pos.CENTER, select3, quit);
        quit.addEventHandler(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            primaryStage.close();
            System.exit(0);
        });

        VBox optionBox = createVBox(
            optionSpacing, Pos.CENTER, option0, option1, option2, option3
        );
        VBox mainBox = createVBox(mainBoxSpacing, Pos.CENTER, title, optionBox);

        rootPane.getChildren().addAll(mainBox);
        Scene gameScene = new Scene(rootPane, width, height);

        Text[] choices = {select0, select1, select2, select3};
        Text[] options = {solo, multiplayer, settings, quit};

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
                        Solo soloMenu = new Solo(backgroundPane);
                        soloMenu.start(primaryStage);
                        break;

                    case multiplayerCase:
                        Multiplayer multiMenu = new Multiplayer(backgroundPane);
                        multiMenu.start(primaryStage);
                        break;

                    case settingsCase:
                        // TODO : Settings
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
        gameScene.setFill(Color.BLACK);
        primaryStage.setScene(gameScene);
        primaryStage.show();
        // Musique de fond jouée en boucle
        Music.playLoopingBgMusic("bgm");
    }

    private static Background getBackground() {
        Image image = Resources.getImage("background");
        BackgroundImage backgroundImage = new BackgroundImage(
            image,
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            null
        );
        return new Background(backgroundImage);
    }

    /**
     * Méthode qui retourne un{@code Text}initialisé avec les paramètres
     * {@code str}et{@code size}.
     * @param str Le texte contenu dans le nouveau{@code Text}
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
     * @param spacing L'espace entre les différents{@code Node}
     * @param position L'alignement des{@code Node}
     * @param nodes Les{@code Node}à mettre dans la{@code VBox}
     * @return {@code VBox}
     */
    public static VBox createVBox(
        final int spacing, final Pos position, final Node... nodes
        ) {
        VBox box = new VBox(spacing);
        box.getChildren().addAll(nodes);
        box.setAlignment(position);
        return box;
    }

    /**
     * Méthode qui retourne un{@code HBox}initialisé avec les paramètres
     * {@code spacing},{@code position}et{@code nodes}.
     * @param spacing L'espace entre les différents{@code Node}
     * @param position L'alignement des{@code Node}
     * @param nodes Les{@code Node}à mettre dans la{@code HBox}
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

    public class KeySelector implements EventHandler<KeyEvent> {
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

        KeySelector(
            final Text[] newChoices,
            final Stage newPrimaryStage,
            final boolean pauseMenu
            ) {
            choices = newChoices;
            primaryStage = newPrimaryStage;
            isPauseMenu = pauseMenu;

            selected = 0;
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
            if (isPauseMenu && !InGameGui.INSTANCE.isPaused()) {
                return;
            }

            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                choices[selected].setVisible(false);
                selected--;
                if (selected < 0) {
                    selected = maxChoice;
                }
                choices[selected].setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                choices[selected].setVisible(false);
                selected++;
                if (selected > maxChoice) {
                    selected = 0;
                }
                choices[selected].setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE && !isPauseMenu) {
                Menu backMenu = new Menu();
                backMenu.start(primaryStage);
            }
        }
    }

    public class MouseSelector implements EventHandler<MouseEvent> {
        /** Les ">". */
        private Text[] choices;
        /** L'indice du choix. */
        private int select;

        MouseSelector(final Text[] newChoices, final int newSelect) {
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
            selected = select;
            choices[selected].setVisible(true);
        }
    }
}
