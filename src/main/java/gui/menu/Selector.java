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

public final class Selector {
    /** Indice de l'option actuellement choisi. */
    private int currentlySelected;
    private KeySelector keySelector;

    /**
     * Constructeur Selector.
     * Initialise le{@code KeySelector}et le{@code MouseSelector}.
     * @param selects Les ">"
     * @param choices
     * @param gameScene
     * @param pauseMenu {@code true}si c'est pour le menu pause
     */
    public Selector(
    final List<Text> selects,
    final List<Text> choices,
    final Scene gameScene,
    final boolean pauseMenu) {
        currentlySelected = 0;
        keySelector = new KeySelector(selects, pauseMenu);
        gameScene.setOnKeyPressed(keySelector);
        int index = 0;
        for (Text text : choices) {
            MouseSelector m = new MouseSelector(selects, index++);
            text.addEventHandler(MouseEvent.MOUSE_ENTERED, m);
        }
    }

    public KeySelector getKeySelector() {
        return keySelector;
    }

    public final class KeySelector implements EventHandler<KeyEvent> {
        /** Les ">". */
        private ArrayList<Text> selects;
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
         * @param pauseMenu
         */
        public KeySelector(
            final List<Text> newSelects,
            final boolean pauseMenu
            ) {
            selects = new ArrayList<>(newSelects);
            isPauseMenu = pauseMenu;

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
            if (isPauseMenu && !InGameGui.isPaused()) {
                return;
            }
            if (keyEvent.getCode() == KeyCode.UP) {
                // Flèche vers le haut
                selects.get(Menu.getSelected()).setVisible(false);
                currentlySelected = Menu.getSelected() - 1;
                if (currentlySelected < 0) {
                    currentlySelected = maxChoice;
                    Menu.setSelected(maxChoice);
                } else {
                    Menu.setSelected(currentlySelected);
                }
                selects.get(Menu.getSelected()).setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                // Flèche vers le bas
                selects.get(Menu.getSelected()).setVisible(false);
                currentlySelected = Menu.getSelected() + 1;
                if (currentlySelected > maxChoice) {
                    currentlySelected = 0;
                    Menu.setSelected(0);
                } else {
                    Menu.setSelected(currentlySelected);
                }
                selects.get(Menu.getSelected()).setVisible(true);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE && !InGameGui.isPaused()) {
                resetSelect();
                Menu.INSTANCE.toMenu();
            }
        }

        /**
         * Réinitialise les ">".
         */
        public void resetSelect() {
            Menu.setSelected(0);
            for (int i = 1; i <= maxChoice; i++) {
                selects.get(i).setVisible(false);
            }
            selects.get(0).setVisible(true);
        }
    }

    public final class MouseSelector implements EventHandler<MouseEvent> {
        /** Les ">". */
        private ArrayList<Text> selects;
        /** L'indice du choix. */
        private int selectKey;

        /**
         * Constructeur MouseSelector.
         * @param newSelects
         * @param key
         */
        public MouseSelector(final List<Text> newSelects, final int key) {
            selects = new ArrayList<>(newSelects);
            selectKey = key;
        }

        /**
         * Affiche ">" lorsqu'on survole la souris sur une option.
         */
        @Override
        public void handle(final MouseEvent mouseEvent) {
            for (int i = 0; i < selects.size(); i++) {
                selects.get(i).setVisible(false);
            }
            Menu.setSelected(selectKey);
            currentlySelected = selectKey;
            selects.get(selectKey).setVisible(true);
        }
    }
}
