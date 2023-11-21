package gui.menu;

import java.util.ArrayList;
import java.util.List;

import gui.InGameGui;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Selector {
    private int selected;
    private Scene gamScene;
    private KeySelector keySelector;
    private ArrayList<MouseSelector> mouseSelectors = new ArrayList<>();

    /**
     * Constructeur Selector.
     * Initialise le{@code KeySelector}et le{@code MouseSelector}.
     * @param selects Les ">"
     * @param choices
     * @param primaryStage
     * @param gameScene
     * @param pauseMenu {@code true}si c'est pour le menu pause
     */
    public Selector(
    final List<Text> selects,
    final List<Text> choices,
    final Stage primaryStage,
    final Scene gameScene,
    final boolean pauseMenu) {
        selected = 0;
        this.gamScene = gameScene;
        keySelector = new KeySelector(selects, primaryStage, pauseMenu);
        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, keySelector);
        int index = 0;
        for (Text text : choices) {
            MouseSelector m = new MouseSelector(selects, index++);
            text.setOnMouseEntered(m);
            mouseSelectors.add(m);
        }
    }

    public class KeySelector implements EventHandler<KeyEvent> {
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

            Menu.setSelected(0);
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
                selected--;
                if (selected < 0) {
                    selected = maxChoice;
                    Menu.setSelected(maxChoice);
                } else {
                    Menu.setSelected(selected);
                }
                selects.get(Menu.getSelected()).setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                selects.get(selected).setVisible(false);
                selected++;
                if (selected > maxChoice) {
                    selected = 0;
                    Menu.setSelected(0);
                } else {
                    Menu.setSelected(selected);
                }
                selects.get(Menu.getSelected()).setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE && !isPauseMenu) {
                Menu.toMenu(primaryStage);
            } else if (keyEvent.getCode() == KeyCode.ENTER && !InGameGui.isPaused()) {
                gamScene.removeEventHandler(KeyEvent.KEY_PRESSED, this);
            }
        }
    }

    public class MouseSelector implements EventHandler<MouseEvent> {
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
            Menu.setSelected(select);
            selected = select;
            selects.get(selected).setVisible(true);
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
                gamScene.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }
        }
    }
}
