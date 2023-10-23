import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testfx.api.*;
import org.testfx.framework.junit5.*;
import org.testfx.matcher.control.*;

import javafx.stage.Stage;

import gui.Intro;

class IntroTest {

    @Start
    void start(Stage stage) {
        Intro intro = new Intro();
        intro.start(stage);
    }

    @Test
    void test() {
        
    }
}
