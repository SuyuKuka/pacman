import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import model.MazeState;
import gui.Menu;
import gui.Solo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testfx.api.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.*;

@ExtendWith(ApplicationExtension.class)
class MazeStateTest extends Menu {

    @Start
    public void start(Stage stage) {
        selected = 0;
        Solo.startLevel(stage);
    }

    @Test
    void test() {
        assertEquals(0, MazeState.getCurrentMaze().getScore());
    }

    @Test
    void test2(FxRobot rob) throws InterruptedException {
        rob.press(KeyCode.RIGHT);
        Thread.sleep(2 * 1000);
        assertEquals(5, MazeState.getCurrentMaze().getScore());
    }
}
