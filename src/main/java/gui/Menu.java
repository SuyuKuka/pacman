package gui;

import javafx.application.Application;
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

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(getBackground());

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
        int select = 0;
        selector(gameScene, choices, primaryStage);
        for (Text text : options) {
            mouseSelector(text, choices, select++);
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
                        // Settings
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

    /**
     * La méthode prend en argument un tableau contenant les
     * différents ">". Lorsque la flèche du haut ou bas est appuyée,
     * le ">" actuel est mis en invisible et le prochain est révélé.
     * @param scene La{@code Scene}actuelle
     * @param choices Tableau de{@code Text}(">")
     * @param primaryStage Le{@code Stage}principal sur lequel les
     * {@code Scene}sont set.
     */
    public static void selector(
        final Scene scene, final Text[] choices, final Stage primaryStage
        ) {
        selected = 0;
        int totalChoices = choices.length - 1;

        for (int i = 1; i < totalChoices + 1; i++) {
            choices[i].setVisible(false);
        }

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                choices[selected].setVisible(false);
                selected--;
                if (selected < 0) {
                    selected = totalChoices;
                }
                choices[selected].setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                choices[selected].setVisible(false);
                selected++;
                if (selected > totalChoices) {
                    selected = 0;
                }
                choices[selected].setVisible(true);
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                Menu backMenu = new Menu();
                backMenu.start(primaryStage);
            }
        });
    }

    /**
     * Méthode qui affiche ">" lorsqu'on survole la souris sur
     * une option.
     * @param mouseText {@code Text}de l'option
     * @param choices {@code Text[]} des ">"
     * @param select Indice de l'option
     */
    public static void mouseSelector(
        final Text mouseText, final Text[] choices, final int select
        ) {
        mouseText.setOnMouseEntered(event -> {
            for (int i = 0; i < choices.length; i++) {
                choices[i].setVisible(false);
            }
            selected = select;
            choices[selected].setVisible(true);
        });
    }
}
