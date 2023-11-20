package gui.controller;

import gui.InGameGui;

public abstract class Controller {
    private static InGameGui inGameGui;

    protected Controller() { }

    public static void setInGameGui(final InGameGui inGameGui) {
        Controller.inGameGui = inGameGui;
    }

    protected static InGameGui getInGameGui() {
        return inGameGui;
    }
}
