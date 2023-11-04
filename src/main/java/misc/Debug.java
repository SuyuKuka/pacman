package misc;

public final class Debug {
    private Debug() {
    }

    /**
     * Méthode pour tester le code en faisant print un message.
     * @param message Le message à print
     */
    public static void out(final String message) {
        // comment this out if you do not want to see the debug messages
        System.out.println(">> DEBUG >> " + message);
    }
}
