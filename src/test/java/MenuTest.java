import javafx.stage.Stage;

import gui.Menu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.*;

@ExtendWith(ApplicationExtension.class)
class MenuTest {

    @Start
    void start(Stage stage) {
        new Menu().start(stage);
    }

    @Test
    void test() {

    }
}
