package gui.menu;

import gui.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Music;
import utils.Resources;

public final class MainMenu extends Menu {
    public static final MainMenu INSTANCE = new MainMenu();

    protected MainMenu() {
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

        primaryStage.setScene(getMenuScene());
        primaryStage.show();

        // Musique de fond jouée en boucle
        Music.playLoopingBgMusic("bgm");
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
        getMainStage().setScene(getMenuScene());

        // Musique de fond jouée en boucle
        if (!Music.isBgmPlaying()) {
            Music.playLoopingBgMusic("bgm");
        }
    }

    @Override
    protected void addOptionsToMenu() {
        final int titleSize = 100;
        final int spacingSize = 20;

        setTitleText(createText("Mac-Pan-Pac-Man", titleSize));
        VBox titleBox = createVBox(0, Pos.TOP_CENTER, getTitleText());

        Text select0 = createText(">", getFontSize().getValue().intValue());
        Text solo = createText("Solo", getFontSize().getValue().intValue());

        Text select1 = createText(">", getFontSize().getValue().intValue());
        Text multiplayer = createText("Multiplayer", getFontSize().getValue().intValue());

        Text select2 = createText(">", getFontSize().getValue().intValue());
        Text settings = createText("Settings", getFontSize().getValue().intValue());

        Text select3 = createText(">", getFontSize().getValue().intValue());
        Text quit = createText("Quit", getFontSize().getValue().intValue());

        if (getOptions().isEmpty()) {
            getOptions().add(0, createHBox(
                spacingSize, Pos.CENTER, select0, solo
            ));
            getOptions().add(1, createHBox(
                spacingSize, Pos.CENTER, select1, multiplayer
            ));
            getOptions().add(2, createHBox(
                spacingSize, Pos.CENTER, select2, settings
            ));
            //CHECKSTYLE:OFF
            getOptions().add(3, createHBox(
                spacingSize, Pos.CENTER, select3, quit
            ));
        }

        VBox optionBox = createVBox(
            spacingSize,
            Pos.CENTER,
            getOptions().get(0), getOptions().get(1), getOptions().get(2), getOptions().get(3)
        );

        if (getSelects().isEmpty() && getChoices().isEmpty()) {
            getSelects().add(0, select0);
            getSelects().add(1, select1);
            getSelects().add(2, select2);
            getSelects().add(3, select3);

            getChoices().add(0, solo);
            getChoices().add(1, multiplayer);
            getChoices().add(2, settings);
            getChoices().add(3, quit);
        }

        bindTextSizeToScene();

        getMenuPane().setTop(titleBox);
        getMenuPane().setCenter(optionBox);
        //CHECKSTYLE:OFF
        BorderPane.setMargin(titleBox, new Insets(100, 0, 0, 0));
        //CHECKSTYLE:ON
    }

    @Override
    protected void addEventHandlers(final Scene gameScene) {
        Selector selector = new Selector(getSelects(), getChoices(), gameScene, false);

        getOptions().get(0).setOnMouseClicked(mouseEvent -> {
            Solo.INSTANCE.toSolo();
            selector.getKeySelector().resetSelect();
        });
        getOptions().get(1).setOnMouseClicked(mouseEvent -> {
            Multiplayer.INSTANCE.toMulti();
            selector.getKeySelector().resetSelect();
        });
        getOptions().get(2).setOnMouseClicked(mouseEvent -> {
            Settings.INSTANCE.toSettings();
            selector.getKeySelector().resetSelect();
        });
        //CHECKSTYLE:OFF
        getOptions().get(3).setOnMouseClicked(mouseEvent -> {
            getMainStage().close();
            System.exit(0);
        });
        //CHECKSTYLE:ON

        gameScene.setOnKeyPressed(keyEvent -> {
            final int soloCase = 0;
            final int multiplayerCase = 1;
            final int settingsCase = 2;
            final int quitCase = 3;
            if (keyEvent.getCode() == KeyCode.ENTER) {
                switch (Selector.getSelectedIndex()) {
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
                        getMainStage().close();
                        System.exit(0);
                        break;

                    default:
                        break;
                }
                selector.getKeySelector().resetSelect();
            }
        });
    }
}
