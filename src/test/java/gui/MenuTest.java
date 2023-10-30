package gui;

import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// import org.testfx.api.*;
// import org.testfx.framework.junit5.*;
// import org.testfx.matcher.control.*;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class MenuTest {

    @Start
    void start(final Stage stage) {
        new Menu().start(stage);
    }

    @Test
    void test() {

    }
}
