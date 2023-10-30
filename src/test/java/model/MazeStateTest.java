package model;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import gui.Menu;
import gui.Solo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.testfx.api.*;
// import org.testfx.framework.junit5.*;
// import org.testfx.matcher.control.*;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxRobot;

@ExtendWith(ApplicationExtension.class)
class MazeStateTest extends Menu {

    @Start
    public void start(final Stage stage) {
        Solo.startLevel(stage);
    }

    @Test
    void test() {
        assertEquals(0, MazeState.getCurrentMaze().getScore());
    }

    @Test
    void test2(final FxRobot rob) throws InterruptedException {
        final int milli = 1000;
        final int nbPacGommes = 5;
        rob.press(KeyCode.RIGHT);
        Thread.sleep(2 * milli);
        assertEquals(nbPacGommes, MazeState.getCurrentMaze().getScore());
    }
}
